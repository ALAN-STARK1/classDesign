package com.example.indras.airecipe.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.airecipe.dto.AiRecipeParseRequest;
import com.example.indras.airecipe.dto.AiRecipeToMealRecordRequest;
import com.example.indras.airecipe.entity.AiCallLog;
import com.example.indras.airecipe.entity.AiRecipe;
import com.example.indras.airecipe.entity.AiRecipeStep;
import com.example.indras.airecipe.mapper.AiCallLogMapper;
import com.example.indras.airecipe.mapper.AiRecipeMapper;
import com.example.indras.airecipe.mapper.AiRecipeStepMapper;
import com.example.indras.airecipe.service.AiRecipeService;
import com.example.indras.airecipe.vo.AiIngredientVO;
import com.example.indras.airecipe.vo.AiRecipeListItemVO;
import com.example.indras.airecipe.vo.AiRecipeVO;
import com.example.indras.airecipe.vo.NutritionEstimateVO;
import com.example.indras.airecipe.vo.RecognizedFoodVO;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.enums.SourceType;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.util.JsonHelper;
import com.example.indras.common.util.PageUtils;
import com.example.indras.mealrecord.entity.MealRecord;
import com.example.indras.mealrecord.mapper.MealRecordMapper;
import com.example.indras.mealrecord.vo.MealRecordVO;
import com.example.indras.recipe.dto.RecipeCreateRequest;
import com.example.indras.recipe.dto.RecipeStepDTO;
import com.example.indras.recipe.service.RecipeService;
import com.example.indras.recipe.vo.RecipeDetailVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.indras.common.storage.LocalFileStorageService;
import com.example.indras.common.storage.LocalFileStorageService.StoredFile;
import com.example.indras.airecipe.support.AiRecipeContextBuilder;
import com.example.indras.config.AiServiceHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiRecipeServiceImpl implements AiRecipeService {

    private final AiRecipeMapper aiRecipeMapper;
    private final AiRecipeStepMapper aiRecipeStepMapper;
    private final AiCallLogMapper aiCallLogMapper;
    private final MealRecordMapper mealRecordMapper;
    private final RecipeService recipeService;
    private final JsonHelper jsonHelper;
    private final ObjectMapper objectMapper;
    private final AiServiceHttpClient aiServiceHttpClient;
    private final AiRecipeContextBuilder aiRecipeContextBuilder;
    private final LocalFileStorageService localFileStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiRecipeVO parseText(Long userId, AiRecipeParseRequest request) {
        long start = System.currentTimeMillis();
        String userInput = buildUserInput(request.getPrompt(), request.getText());
        try {
            JsonNode data = callAiParseText(userId, userInput);
            AiRecipe saved = saveFromAiResult(userId, "TEXT", data, null, null, userInput);
            logAiCall(userId, "TEXT_PARSE", userInput, (int) (System.currentTimeMillis() - start), true, null);
            return toDetailVo(saved);
        } catch (BizException ex) {
            logAiCall(userId, "TEXT_PARSE", userInput, (int) (System.currentTimeMillis() - start), false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logAiCall(userId, "TEXT_PARSE", userInput, (int) (System.currentTimeMillis() - start), false, ex.getMessage());
            throw BizException.aiUnavailable("AI 文本解析失败: " + ex.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiRecipeVO parseImage(Long userId, MultipartFile file, String prompt) {
        long start = System.currentTimeMillis();
        StoredFile storedImage = localFileStorageService.saveAiRecipeImage(file);
        try {
            JsonNode data = callAiParseImage(userId, file, prompt);
            AiRecipe saved = saveFromAiResult(userId, "IMAGE", data, storedImage.url(), storedImage.key(), prompt);
            logAiCall(userId, "IMAGE_PARSE", "上传食物图片解析", (int) (System.currentTimeMillis() - start), true, null);
            return toDetailVo(saved);
        } catch (BizException ex) {
            logAiCall(userId, "IMAGE_PARSE", "上传食物图片解析", (int) (System.currentTimeMillis() - start), false, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logAiCall(userId, "IMAGE_PARSE", "上传食物图片解析", (int) (System.currentTimeMillis() - start), false, ex.getMessage());
            throw BizException.aiUnavailable("AI 图片解析失败: " + ex.getMessage());
        }
    }

    @Override
    public AiRecipeVO detail(Long userId, Long id) {
        return toDetailVo(requireOwned(userId, id));
    }

    @Override
    public PageResult<AiRecipeListItemVO> history(Long userId, PageQuery query, String sourceType) {
        Page<AiRecipe> page = aiRecipeMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<AiRecipe>lambdaQuery()
                        .eq(AiRecipe::getUserId, userId)
                        .ne(AiRecipe::getStatus, "DELETED")
                        .eq(StringUtils.hasText(sourceType), AiRecipe::getSourceType, sourceType)
                        .like(StringUtils.hasText(query.getKeyword()), AiRecipe::getRecipeName, query.getKeyword())
                        .orderByDesc(AiRecipe::getId));
        return PageUtils.toPageResult(page, this::toListItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiRecipeVO confirm(Long userId, Long id, Map<String, Object> payload) {
        AiRecipe recipe = requireOwned(userId, id);
        if (payload != null) {
            if (payload.get("recipeName") != null) {
                recipe.setRecipeName(String.valueOf(payload.get("recipeName")));
            }
            if (payload.get("name") != null) {
                recipe.setRecipeName(String.valueOf(payload.get("name")));
            }
            if (payload.get("description") != null) {
                recipe.setDescription(String.valueOf(payload.get("description")));
            }
        }
        recipe.setStatus("CONFIRMED");
        aiRecipeMapper.updateById(recipe);
        return toDetailVo(recipe);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecipeDetailVO toRecipe(Long userId, Long id) {
        AiRecipeVO aiRecipe = toDetailVo(requireOwned(userId, id));
        RecipeCreateRequest request = RecipeCreateRequest.builder()
                .name(aiRecipe.getRecipeName())
                .description(aiRecipe.getDescription())
                .category("LUNCH")
                .steps(aiRecipe.getCookingSteps() == null ? List.of() : aiRecipe.getCookingSteps().stream()
                        .map(step -> RecipeStepDTO.builder().content(step).build())
                        .toList())
                .build();
        RecipeDetailVO created = recipeService.create(userId, request);
        AiRecipe recipe = requireOwned(userId, id);
        recipe.setStatus("CONVERTED");
        aiRecipeMapper.updateById(recipe);
        return created;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealRecordVO toMealRecord(Long userId, Long id, AiRecipeToMealRecordRequest request) {
        AiRecipeVO aiRecipe = toDetailVo(requireOwned(userId, id));
        NutritionEstimateVO nutrition = aiRecipe.getNutritionEstimate();
        BigDecimal ratio = request.getServingRatio() == null ? BigDecimal.ONE : request.getServingRatio();
        MealRecord record = MealRecord.builder()
                .userId(userId)
                .recordDate(request.getRecordDate())
                .mealType(request.getMealType())
                .sourceType(SourceType.AI_RECIPE.name())
                .sourceId(id)
                .totalCalorie(scale(nutrition == null ? null : nutrition.getCalories(), ratio))
                .totalProtein(scale(nutrition == null ? null : nutrition.getProtein(), ratio))
                .totalFat(scale(nutrition == null ? null : nutrition.getFat(), ratio))
                .totalCarbohydrate(scale(nutrition == null ? null : nutrition.getCarbohydrate(), ratio))
                .remark(aiRecipe.getRecipeName())
                .build();
        mealRecordMapper.insert(record);
        return MealRecordVO.builder()
                .id(record.getId())
                .recordDate(record.getRecordDate())
                .mealType(record.getMealType())
                .sourceType(record.getSourceType())
                .totalCalorie(record.getTotalCalorie())
                .totalProtein(record.getTotalProtein())
                .totalFat(record.getTotalFat())
                .totalCarbohydrate(record.getTotalCarbohydrate())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long userId, Long id) {
        AiRecipe recipe = requireOwned(userId, id);
        recipe.setStatus("DELETED");
        return aiRecipeMapper.updateById(recipe) > 0;
    }

    private AiRecipe requireOwned(Long userId, Long id) {
        AiRecipe recipe = aiRecipeMapper.selectById(id);
        if (recipe == null || !Objects.equals(recipe.getUserId(), userId)) {
            throw BizException.notFound("AI 菜谱不存在");
        }
        return recipe;
    }

    private JsonNode callAiParseText(Long userId, String userInput) throws Exception {
        Map<String, Object> body = aiRecipeContextBuilder.build(userId, userInput);
        String response = aiServiceHttpClient.postJson("/ai/v1/recipes/parse", body);
        return parseAiResponse(response);
    }

    private JsonNode callAiParseImage(Long userId, MultipartFile file, String prompt) throws Exception {
        if (file == null || file.isEmpty()) {
            throw BizException.badRequest("请上传图片文件");
        }
        // ai-service: FoodClassifier 预训练模型识别 → 合并 recognitionResults → DeepSeek 生成菜谱
        Map<String, Object> context = aiRecipeContextBuilder.build(userId, prompt == null ? "" : prompt);
        String response = aiServiceHttpClient.postMultipartImage(
                "/ai/v1/recipes/parse-from-image",
                file,
                objectMapper.writeValueAsString(context));
        return parseAiResponse(response);
    }

    private JsonNode parseAiResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        if (!root.path("success").asBoolean(false)) {
            throw BizException.aiUnavailable(root.path("errorMessage").asText("AI 服务不可用"));
        }
        JsonNode data = root.path("data");
        if (data.isMissingNode() || data.isNull()) {
            throw BizException.aiUnavailable("AI 返回为空");
        }
        return data;
    }

    private AiRecipe saveFromAiResult(Long userId, String sourceType, JsonNode data, String imageUrl, String imageKey, String summary) {
        JsonNode suitability = data.path("suitability");
        JsonNode nutrition = data.path("nutritionEstimate");
        AiRecipe recipe = AiRecipe.builder()
                .userId(userId)
                .sourceType(sourceType)
                .recipeName(data.path("recipeName").asText("未命名菜谱"))
                .description(data.path("description").asText(""))
                .recognizedFoodsJson(jsonHelper.write(readRecognizedFoods(data)))
                .ingredientsJson(jsonHelper.write(readIngredients(data)))
                .nutritionJson(jsonHelper.write(readNutrition(nutrition)))
                .suitabilityScore(suitability.path("score").asInt(0))
                .suitabilityReason(suitability.path("reason").asText(""))
                .healthTipsJson(jsonHelper.write(readStringList(data, "healthTips")))
                .warningsJson(jsonHelper.write(readStringList(data, "warnings")))
                .rawResponseJson(data.toString())
                .sourceImageUrl(imageUrl)
                .sourceImageKey(imageKey)
                .status("PARSED")
                .createdAt(LocalDateTime.now())
                .build();
        aiRecipeMapper.insert(recipe);
        saveSteps(recipe.getId(), readStringList(data, "cookingSteps"));
        return recipe;
    }

    private void saveSteps(Long aiRecipeId, List<String> steps) {
        int stepNo = 1;
        for (String step : steps) {
            aiRecipeStepMapper.insert(AiRecipeStep.builder()
                    .aiRecipeId(aiRecipeId)
                    .stepNo(stepNo++)
                    .content(step)
                    .build());
        }
    }

    private void logAiCall(Long userId, String scene, String summary, int elapsedMs, boolean success, String error) {
        aiCallLogMapper.insert(AiCallLog.builder()
                .userId(userId)
                .scene(scene)
                .requestSummary(summary)
                .elapsedMs(elapsedMs)
                .success(success)
                .errorMessage(error)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private AiRecipeListItemVO toListItem(AiRecipe recipe) {
        NutritionEstimateVO nutrition = jsonHelper.read(recipe.getNutritionJson(), NutritionEstimateVO.class);
        return AiRecipeListItemVO.builder()
                .id(recipe.getId())
                .recipeName(recipe.getRecipeName())
                .name(recipe.getRecipeName())
                .sourceType(recipe.getSourceType())
                .status(recipe.getStatus())
                .suitabilityScore(recipe.getSuitabilityScore())
                .totalCalorie(nutrition == null ? null : nutrition.getCalories())
                .createdAt(recipe.getCreatedAt() == null ? null
                        : OffsetDateTime.of(recipe.getCreatedAt(), ZoneOffset.ofHours(8)))
                .build();
    }

    private AiRecipeVO toDetailVo(AiRecipe recipe) {
        List<AiRecipeStep> steps = aiRecipeStepMapper.selectList(Wrappers.<AiRecipeStep>lambdaQuery()
                .eq(AiRecipeStep::getAiRecipeId, recipe.getId())
                .orderByAsc(AiRecipeStep::getStepNo));
        NutritionEstimateVO nutrition = jsonHelper.read(recipe.getNutritionJson(), NutritionEstimateVO.class);
        return AiRecipeVO.builder()
                .id(recipe.getId())
                .recipeName(recipe.getRecipeName())
                .name(recipe.getRecipeName())
                .description(recipe.getDescription())
                .suitabilityScore(recipe.getSuitabilityScore())
                .suitabilityReason(recipe.getSuitabilityReason())
                .nutritionEstimate(nutrition)
                .ingredients(jsonHelper.readList(recipe.getIngredientsJson(), AiIngredientVO.class))
                .cookingSteps(steps.stream().map(AiRecipeStep::getContent).toList())
                .warnings(jsonHelper.readStringList(recipe.getWarningsJson()))
                .recognizedFoods(jsonHelper.readList(recipe.getRecognizedFoodsJson(), RecognizedFoodVO.class))
                .sourceImageUrl(recipe.getSourceImageUrl())
                .sourceType(recipe.getSourceType())
                .status(recipe.getStatus())
                .build();
    }

    private List<String> readStringList(JsonNode data, String field) {
        if (!data.has(field) || !data.get(field).isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        data.get(field).forEach(node -> values.add(node.asText()));
        return values;
    }

    private List<Map<String, Object>> readIngredients(JsonNode data) {
        if (!data.has("ingredients") || !data.get("ingredients").isArray()) {
            return List.of();
        }
        List<Map<String, Object>> items = new ArrayList<>();
        data.get("ingredients").forEach(node -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", node.path("name").asText());
            item.put("amount", node.path("amount").asDouble());
            item.put("unit", node.path("unit").asText());
            items.add(item);
        });
        return items;
    }

    private List<Map<String, Object>> readRecognizedFoods(JsonNode data) {
        if (!data.has("recognizedFoods") || !data.get("recognizedFoods").isArray()) {
            return List.of();
        }
        List<Map<String, Object>> items = new ArrayList<>();
        data.get("recognizedFoods").forEach(node -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", node.path("name").asText());
            item.put("confidence", node.path("confidence").asDouble());
            items.add(item);
        });
        return items;
    }

    private Map<String, Object> readNutrition(JsonNode nutrition) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("calories", nutrition.path("calories").asDouble());
        map.put("protein", nutrition.path("protein").asDouble());
        map.put("fat", nutrition.path("fat").asDouble());
        map.put("carbohydrate", nutrition.path("carbohydrate").asDouble());
        return map;
    }

    private String buildUserInput(String prompt, String text) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.hasText(prompt)) {
            parts.add(prompt.trim());
        }
        if (StringUtils.hasText(text)) {
            parts.add(text.trim());
        }
        return String.join("\n\n", parts);
    }

    private BigDecimal scale(BigDecimal value, BigDecimal ratio) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(ratio);
    }
}
