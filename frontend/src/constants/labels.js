export const GenderLabel = {
  MALE: '男',
  FEMALE: '女',
  UNKNOWN: '未知',
}

export const ActivityLevelLabel = {
  SEDENTARY: '久坐',
  LIGHT: '轻度活动',
  MODERATE: '中度活动',
  HIGH: '高活动量',
}

export const HealthGoalLabel = {
  FAT_LOSS: '减脂',
  MUSCLE_GAIN: '增肌',
  SUGAR_CONTROL: '控糖',
  MAINTAIN: '维持',
}

export const GoalCycleStatusLabel = {
  ACTIVE: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  EXPIRED: '已过期',
}

export const IngredientCategoryLabel = {
  GRAIN: '谷物主食',
  VEGETABLE: '蔬菜',
  FRUIT: '水果',
  MEAT: '肉禽蛋',
  SEAFOOD: '水产',
  DAIRY: '乳制品',
  BEAN: '豆制品',
  NUT: '坚果',
  SEASONING: '调味品',
}

export const IngredientStatusLabel = {
  ENABLED: '启用',
  DISABLED: '停用',
}

export const RecipeCategoryLabel = {
  BREAKFAST: '早餐',
  LUNCH: '午餐',
  DINNER: '晚餐',
  SNACK: '加餐',
}

export const RecipeDifficultyLabel = {
  EASY: '简单',
  NORMAL: '普通',
  HARD: '复杂',
}

export const RecipeStatusLabel = {
  DRAFT: '草稿',
  PENDING: '待审核',
  ONLINE: '已上线',
  OFFLINE: '已下线',
}

export const MealTypeLabel = RecipeCategoryLabel

export const MealPlanStatusLabel = {
  DRAFT: '草稿',
  ACTIVE: '进行中',
  GENERATED: '已生成',
  CONFIRMED: '已确认',
  COMPLETED: '已完成',
}

export const MealItemStatusLabel = {
  PLANNED: '计划中',
  REPLACED: '已替换',
  COMPLETED: '已完成',
  SKIPPED: '已跳过',
}

export const MealFeedbackLevelLabel = {
  LIKE: '喜欢',
  NORMAL: '一般',
  DISLIKE: '不喜欢',
}

export const RiskSeverityLabel = {
  INFO: '提示',
  WARNING: '预警',
  DANGER: '危险',
}

export const RiskRuleStatusLabel = {
  ENABLED: '启用',
  DISABLED: '停用',
}

export const ReportTypeLabel = {
  WEEKLY: '周报',
  MONTHLY: '月报',
}

export const ReportStatusLabel = {
  GENERATING: '生成中',
  READY: '已完成',
  FAILED: '失败',
}

export const AiRecipeStatusLabel = {
  PARSED: '待确认',
  CONFIRMED: '已确认',
  CONVERTED: '已转换',
  DELETED: '已删除',
}

export const AiRecipeSourceTypeLabel = {
  TEXT: '文本解析',
  IMAGE: '图片识别',
}

export const CommunityPostStatusLabel = {
  PENDING: '审核中',
  PUBLISHED: '已发布',
  REJECTED: '已拒绝',
}

export function toOptions(values, labelMap) {
  return values.map((value) => ({
    label: labelMap[value] || value,
    value,
  }))
}
