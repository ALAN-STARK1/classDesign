module.exports = {
  GenderLabel: { MALE: '男', FEMALE: '女', UNKNOWN: '未知' },
  ActivityLevelLabel: {
    SEDENTARY: '久坐',
    LIGHT: '轻度活动',
    MODERATE: '中度活动',
    HIGH: '高活动量',
  },
  HealthGoalLabel: {
    FAT_LOSS: '减脂',
    MUSCLE_GAIN: '增肌',
    SUGAR_CONTROL: '控糖',
    MAINTAIN: '维持',
  },
  MealTypeLabel: {
    BREAKFAST: '早餐',
    LUNCH: '午餐',
    DINNER: '晚餐',
    SNACK: '加餐',
  },
  GoalCycleStatusLabel: {
    ACTIVE: '进行中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
  },
  RiskSeverityLabel: { INFO: '提示', WARNING: '预警', DANGER: '危险' },
  AiRecipeSourceTypeLabel: { TEXT: '文本', IMAGE: '图片' },
  CommunityPostStatusLabel: {
    PENDING: '审核中',
    PUBLISHED: '已发布',
    ONLINE: '已发布',
    REJECTED: '已拒绝',
  },
  AllergenOptions: ['花生', '牛奶', '鸡蛋', '海鲜', '坚果', '大豆', '小麦', '芝麻'],
  RestrictionOptions: ['素食', '低盐', '低糖', '低脂', '无麸质', '清真'],
  ChronicDiseaseOptions: ['高血压', '糖尿病', '高血脂', '痛风', '肾病'],
}
