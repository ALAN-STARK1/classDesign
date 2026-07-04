package com.example.indras.community.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.indras.airecipe.entity.AiRecipe;
import com.example.indras.airecipe.mapper.AiRecipeMapper;
import com.example.indras.common.api.PageQuery;
import com.example.indras.common.api.PageResult;
import com.example.indras.common.context.UserContext;
import com.example.indras.common.exception.BizException;
import com.example.indras.common.storage.LocalFileStorageService;
import com.example.indras.common.util.JsonHelper;
import com.example.indras.common.util.PageUtils;
import com.example.indras.common.vo.NutritionVO;
import com.example.indras.community.dto.CommunityCommentCreateRequest;
import com.example.indras.community.dto.CommunityPostCreateRequest;
import com.example.indras.community.dto.CommunityPostFromAiRequest;
import com.example.indras.community.entity.*;
import com.example.indras.community.mapper.*;
import com.example.indras.community.service.CommunityService;
import com.example.indras.community.vo.*;
import com.example.indras.user.entity.UserProfile;
import com.example.indras.user.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CommunityPostMapper communityPostMapper;
    private final CommunityPostImageMapper communityPostImageMapper;
    private final CommunityCommentMapper communityCommentMapper;
    private final CommunityLikeMapper communityLikeMapper;
    private final CommunityFavoriteMapper communityFavoriteMapper;
    private final AiRecipeMapper aiRecipeMapper;
    private final UserProfileMapper userProfileMapper;
    private final JsonHelper jsonHelper;
    private final LocalFileStorageService localFileStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostImageUploadVO uploadPostImage(Long userId, MultipartFile file) {
        LocalFileStorageService.StoredFile storedFile = localFileStorageService.saveCommunityPostImage(file);
        CommunityPostImage image = CommunityPostImage.builder()
                .userId(userId)
                .storageKey(storedFile.key())
                .imageUrl(storedFile.url())
                .sortNo(0)
                .width(1280)
                .height(960)
                .fileSize(file.getSize())
                .status("TEMP")
                .createdAt(LocalDateTime.now())
                .build();
        communityPostImageMapper.insert(image);
        return PostImageUploadVO.builder()
                .imageId(image.getId())
                .imageUrl(image.getImageUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .fileSize(image.getFileSize())
                .status(image.getStatus())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityPostDetailVO createPost(Long userId, CommunityPostCreateRequest request) {
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .recipeName(request.getRecipeName())
                .tagsJson(jsonHelper.write(request.getTags() == null ? List.of() : request.getTags()))
                .sourceType("MANUAL")
                .status("PENDING")
                .likeCount(0)
                .commentCount(0)
                .favoriteCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        communityPostMapper.insert(post);
        bindImages(userId, post.getId(), request.getImageIds());
        return toDetailVo(post, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityPostDetailVO createPostFromAiRecipe(Long userId, Long aiRecipeId, CommunityPostFromAiRequest request) {
        AiRecipe aiRecipe = aiRecipeMapper.selectById(aiRecipeId);
        if (aiRecipe == null || !Objects.equals(aiRecipe.getUserId(), userId)) {
            throw BizException.notFound("AI 菜谱不存在");
        }
        List<Long> imageIds = new ArrayList<>(request.getImageIds() == null ? List.of() : request.getImageIds());
        if (Boolean.TRUE.equals(request.getUseAiSourceImage()) && StringUtils.hasText(aiRecipe.getSourceImageUrl())) {
            CommunityPostImage image = CommunityPostImage.builder()
                    .userId(userId)
                    .storageKey("ai/" + aiRecipeId)
                    .imageUrl(aiRecipe.getSourceImageUrl())
                    .sortNo(0)
                    .width(1280)
                    .height(960)
                    .fileSize(450000L)
                    .status("TEMP")
                    .createdAt(LocalDateTime.now())
                    .build();
            communityPostImageMapper.insert(image);
            imageIds.add(0, image.getId());
        }
        if (imageIds.isEmpty()) {
            throw BizException.badRequest("发布社区帖子至少需要 1 张图片");
        }
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .recipeName(aiRecipe.getRecipeName())
                .tagsJson("[]")
                .sourceType("AI_RECIPE")
                .sourceId(aiRecipeId)
                .status("PENDING")
                .likeCount(0)
                .commentCount(0)
                .favoriteCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        communityPostMapper.insert(post);
        bindImages(userId, post.getId(), imageIds);
        return toDetailVo(post, userId);
    }

    @Override
    public PageResult<CommunityPostListItemVO> pagePosts(Long userId, PageQuery query, String status) {
        String dbStatus = toDbStatus(status);
        if (!StringUtils.hasText(dbStatus) && !"ADMIN".equals(UserContext.getRole())) {
            dbStatus = "ONLINE";
        }
        Page<CommunityPost> page = communityPostMapper.selectPage(new Page<>(query.getPage(), query.getSize()),
                Wrappers.<CommunityPost>lambdaQuery()
                        .eq(StringUtils.hasText(dbStatus), CommunityPost::getStatus, dbStatus)
                        .like(StringUtils.hasText(query.getKeyword()), CommunityPost::getTitle, query.getKeyword())
                        .orderByDesc(CommunityPost::getId));
        return PageUtils.toPageResult(page, post -> toListItem(post, userId));
    }

    @Override
    public CommunityPostDetailVO postDetail(Long userId, Long id) {
        CommunityPost post = communityPostMapper.selectById(id);
        if (post == null) {
            throw BizException.notFound("帖子不存在");
        }
        return toDetailVo(post, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunityCommentVO createComment(Long userId, Long postId, CommunityCommentCreateRequest request) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post == null) {
            throw BizException.notFound("帖子不存在");
        }
        CommunityComment comment = CommunityComment.builder()
                .postId(postId)
                .userId(userId)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        communityCommentMapper.insert(comment);
        post.setCommentCount((post.getCommentCount() == null ? 0 : post.getCommentCount()) + 1);
        communityPostMapper.updateById(post);
        return CommunityCommentVO.builder()
                .id(comment.getId())
                .authorName(resolveNickname(userId))
                .content(comment.getContent())
                .createdAt(formatTime(comment.getCreatedAt()))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likePost(Long userId, Long postId) {
        CommunityPost post = requirePost(postId);
        Long count = communityLikeMapper.selectCount(Wrappers.<CommunityLike>lambdaQuery()
                .eq(CommunityLike::getPostId, postId)
                .eq(CommunityLike::getUserId, userId));
        if (count != null && count > 0) {
            return true;
        }
        communityLikeMapper.insert(CommunityLike.builder()
                .postId(postId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build());
        post.setLikeCount((post.getLikeCount() == null ? 0 : post.getLikeCount()) + 1);
        communityPostMapper.updateById(post);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favoritePost(Long userId, Long postId) {
        CommunityPost post = requirePost(postId);
        Long count = communityFavoriteMapper.selectCount(Wrappers.<CommunityFavorite>lambdaQuery()
                .eq(CommunityFavorite::getPostId, postId)
                .eq(CommunityFavorite::getUserId, userId));
        if (count != null && count > 0) {
            return true;
        }
        communityFavoriteMapper.insert(CommunityFavorite.builder()
                .postId(postId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build());
        post.setFavoriteCount((post.getFavoriteCount() == null ? 0 : post.getFavoriteCount()) + 1);
        communityPostMapper.updateById(post);
        return true;
    }

    private CommunityPost requirePost(Long postId) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post == null) {
            throw BizException.notFound("帖子不存在");
        }
        return post;
    }

    private void bindImages(Long userId, Long postId, List<Long> imageIds) {
        int sortNo = 1;
        for (Long imageId : imageIds) {
            CommunityPostImage image = communityPostImageMapper.selectById(imageId);
            if (image == null || !Objects.equals(image.getUserId(), userId)) {
                throw BizException.badRequest("图片不存在或无权使用");
            }
            image.setPostId(postId);
            image.setSortNo(sortNo++);
            image.setStatus("ACTIVE");
            communityPostImageMapper.updateById(image);
        }
    }

    private CommunityPostListItemVO toListItem(CommunityPost post, Long userId) {
        List<CommunityPostImage> images = listImages(post.getId());
        return CommunityPostListItemVO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(resolveNickname(post.getUserId()))
                .recipeName(post.getRecipeName())
                .status(toClientStatus(post.getStatus()))
                .coverImageUrl(images.isEmpty() ? null : images.get(0).getImageUrl())
                .imageCount(images.size())
                .tags(jsonHelper.readStringList(post.getTagsJson()))
                .likeCount(post.getLikeCount())
                .favoriteCount(post.getFavoriteCount())
                .commentCount(post.getCommentCount())
                .liked(isLiked(userId, post.getId()))
                .favorite(isFavorite(userId, post.getId()))
                .createdAt(formatTime(post.getCreatedAt()))
                .publishedAt(post.getPublishedAt() == null ? null : formatTime(post.getPublishedAt()))
                .build();
    }

    private CommunityPostDetailVO toDetailVo(CommunityPost post, Long userId) {
        List<CommunityPostImage> images = listImages(post.getId());
        List<CommunityComment> comments = communityCommentMapper.selectList(Wrappers.<CommunityComment>lambdaQuery()
                .eq(CommunityComment::getPostId, post.getId())
                .orderByAsc(CommunityComment::getId));
        NutritionVO nutrition = null;
        if ("AI_RECIPE".equals(post.getSourceType()) && post.getSourceId() != null) {
            AiRecipe aiRecipe = aiRecipeMapper.selectById(post.getSourceId());
            if (aiRecipe != null) {
                Map<String, Object> map = jsonHelper.readMap(aiRecipe.getNutritionJson());
                nutrition = NutritionVO.builder()
                        .calorie(toBigDecimal(map.get("calories")))
                        .protein(toBigDecimal(map.get("protein")))
                        .fat(toBigDecimal(map.get("fat")))
                        .carbohydrate(toBigDecimal(map.get("carbohydrate")))
                        .build();
            }
        }
        return CommunityPostDetailVO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(resolveNickname(post.getUserId()))
                .recipeName(post.getRecipeName())
                .status(toClientStatus(post.getStatus()))
                .coverImageUrl(images.isEmpty() ? null : images.get(0).getImageUrl())
                .imageCount(images.size())
                .images(images.stream().map(image -> CommunityPostImageVO.builder()
                        .id(image.getId())
                        .url(image.getImageUrl())
                        .sortNo(image.getSortNo())
                        .width(image.getWidth())
                        .height(image.getHeight())
                        .build()).toList())
                .tags(jsonHelper.readStringList(post.getTagsJson()))
                .nutrition(nutrition)
                .comments(comments.stream().map(comment -> CommunityCommentVO.builder()
                        .id(comment.getId())
                        .authorName(resolveNickname(comment.getUserId()))
                        .content(comment.getContent())
                        .createdAt(formatTime(comment.getCreatedAt()))
                        .build()).toList())
                .likeCount(post.getLikeCount())
                .favoriteCount(post.getFavoriteCount())
                .commentCount(post.getCommentCount())
                .liked(isLiked(userId, post.getId()))
                .favorite(isFavorite(userId, post.getId()))
                .createdAt(formatTime(post.getCreatedAt()))
                .publishedAt(post.getPublishedAt() == null ? null : formatTime(post.getPublishedAt()))
                .build();
    }

    private List<CommunityPostImage> listImages(Long postId) {
        return communityPostImageMapper.selectList(Wrappers.<CommunityPostImage>lambdaQuery()
                .eq(CommunityPostImage::getPostId, postId)
                .eq(CommunityPostImage::getStatus, "ACTIVE")
                .orderByAsc(CommunityPostImage::getSortNo));
    }

    private boolean isLiked(Long userId, Long postId) {
        Long count = communityLikeMapper.selectCount(Wrappers.<CommunityLike>lambdaQuery()
                .eq(CommunityLike::getPostId, postId)
                .eq(CommunityLike::getUserId, userId));
        return count != null && count > 0;
    }

    private boolean isFavorite(Long userId, Long postId) {
        Long count = communityFavoriteMapper.selectCount(Wrappers.<CommunityFavorite>lambdaQuery()
                .eq(CommunityFavorite::getPostId, postId)
                .eq(CommunityFavorite::getUserId, userId));
        return count != null && count > 0;
    }

    private String resolveNickname(Long userId) {
        UserProfile profile = userProfileMapper.selectOne(Wrappers.<UserProfile>lambdaQuery()
                .eq(UserProfile::getUserId, userId));
        return profile == null ? "用户" + userId : profile.getNickname();
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? null : time.format(DISPLAY_TIME);
    }

    private java.math.BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return java.math.BigDecimal.ZERO;
        }
        return new java.math.BigDecimal(String.valueOf(value));
    }

    private String toDbStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        return "PUBLISHED".equals(status) ? "ONLINE" : status;
    }

    private String toClientStatus(String status) {
        if ("ONLINE".equals(status)) {
            return "PUBLISHED";
        }
        return status;
    }
}
