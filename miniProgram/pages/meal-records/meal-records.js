const mealService = require('../../services/meal')
const { today } = require('../../utils/date')
const { MealTypeLabel } = require('../../constants/labels')
const { labelOf } = require('../../utils/format')
const { showError } = require('../../utils/request')

Page({
  data: {
    date: today(),
    loading: true,
    records: [],
    summary: null,
    showForm: false,
    mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'],
    mealTypeIndex: 1,
    form: { foodName: '', amount: '', calorie: '' },
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadRecords()
  },

  onDateChange(e) {
    this.setData({ date: e.detail.value }, () => this.loadRecords())
  },

  async loadRecords() {
    this.setData({ loading: true })
    try {
      const result = await mealService.fetchDayMealRecords({ date: this.data.date })
      const records = (result.records || []).map((item) => Object.assign({}, item, {
        mealTypeLabel: labelOf(MealTypeLabel, item.mealType),
      }))
      this.setData({ records, summary: result.summary || null, loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  toggleForm() {
    this.setData({ showForm: !this.data.showForm })
  },

  onFormInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  onMealTypeChange(e) {
    this.setData({ mealTypeIndex: Number(e.detail.value) })
  },

  async onSubmit() {
    const { form, mealTypes, mealTypeIndex, date } = this.data
    if (!form.foodName) {
      wx.showToast({ title: '请输入食物名称', icon: 'none' })
      return
    }
    try {
      await mealService.createManualMealRecord({
        date,
        mealType: mealTypes[mealTypeIndex],
        foodName: form.foodName,
        amount: Number(form.amount || 1),
        calorie: Number(form.calorie || 0),
      })
      wx.showToast({ title: '记录成功', icon: 'success' })
      this.setData({ showForm: false, form: { foodName: '', amount: '', calorie: '' } })
      this.loadRecords()
    } catch (err) {
      showError(err)
    }
  },

  async onDelete(e) {
    const id = e.currentTarget.dataset.id
    try {
      await mealService.deleteMealRecord(id)
      wx.showToast({ title: '已删除', icon: 'success' })
      this.loadRecords()
    } catch (err) {
      showError(err)
    }
  },

  MealTypeLabel,
})
