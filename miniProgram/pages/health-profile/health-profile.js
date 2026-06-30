const healthService = require('../../services/health')
const { GenderLabel, ActivityLevelLabel, HealthGoalLabel } = require('../../constants/labels')
const { labelOf } = require('../../utils/format')
const { showError } = require('../../utils/request')

Page({
  data: {
    loading: true,
    profile: {},
    summary: null,
    genderOptions: Object.keys(GenderLabel),
    genderLabels: Object.values(GenderLabel),
    activityOptions: Object.keys(ActivityLevelLabel),
    activityLabels: Object.values(ActivityLevelLabel),
    goalOptions: Object.keys(HealthGoalLabel),
    goalLabels: Object.values(HealthGoalLabel),
    genderIndex: 0,
    activityIndex: 0,
    goalIndex: 0,
  },

  onLoad() {
    if (!getApp().ensureLogin()) return
    this.loadProfile()
  },

  async loadProfile() {
    try {
      const [profile, summary] = await Promise.all([
        healthService.fetchHealthProfile(),
        healthService.fetchHealthSummary().catch(() => null),
      ])
      const genderIndex = Math.max(0, this.data.genderOptions.indexOf(profile.gender || 'UNKNOWN'))
      const activityIndex = Math.max(0, this.data.activityOptions.indexOf(profile.activityLevel || 'MODERATE'))
      const goalIndex = Math.max(0, this.data.goalOptions.indexOf(profile.healthGoal || 'MAINTAIN'))
      this.setData({ profile, summary, genderIndex, activityIndex, goalIndex, loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`profile.${field}`]: e.detail.value })
  },

  onBirthdayChange(e) {
    this.setData({ 'profile.birthday': e.detail.value })
  },

  onGenderChange(e) {
    const genderIndex = Number(e.detail.value)
    this.setData({ genderIndex, 'profile.gender': this.data.genderOptions[genderIndex] })
  },

  onActivityChange(e) {
    const activityIndex = Number(e.detail.value)
    this.setData({ activityIndex, 'profile.activityLevel': this.data.activityOptions[activityIndex] })
  },

  onGoalChange(e) {
    const goalIndex = Number(e.detail.value)
    this.setData({ goalIndex, 'profile.healthGoal': this.data.goalOptions[goalIndex] })
  },

  goHealthTags() {
    wx.navigateTo({ url: '/pages/health-tags/health-tags' })
  },

  goWeightRecords() {
    wx.navigateTo({ url: '/pages/weight-records/weight-records' })
  },

  async onSave() {
    try {
      await healthService.saveHealthProfile(this.data.profile)
      const summary = await healthService.fetchHealthSummary().catch(() => null)
      this.setData({ summary })
      wx.showToast({ title: '保存成功', icon: 'success' })
    } catch (err) {
      showError(err)
    }
  },

  labelOf,
  GenderLabel,
  ActivityLevelLabel,
  HealthGoalLabel,
})
