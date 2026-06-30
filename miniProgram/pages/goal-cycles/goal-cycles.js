const healthService = require('../../services/health')
const { HealthGoalLabel, GoalCycleStatusLabel } = require('../../constants/labels')
const { labelOf } = require('../../utils/format')
const { showError } = require('../../utils/request')

Page({
  data: {
    loading: true,
    current: null,
    cycles: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      const [current, cyclesResult] = await Promise.all([
        healthService.fetchCurrentGoalCycle().catch(() => null),
        healthService.fetchGoalCycles({ page: 1, pageSize: 20 }),
      ])
      const cycles = (cyclesResult.items || cyclesResult || []).map((item) => Object.assign({}, item, {
        goalLabel: labelOf(HealthGoalLabel, item.goalType),
        statusLabel: labelOf(GoalCycleStatusLabel, item.status),
      }))
      this.setData({
        current: current ? Object.assign({}, current, {
          goalLabel: labelOf(HealthGoalLabel, current.goalType),
          statusLabel: labelOf(GoalCycleStatusLabel, current.status),
        }) : null,
        cycles,
        loading: false,
      })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },
})
