const authService = require('../../services/auth')
const { showError } = require('../../utils/request')

Page({
  data: {
    username: '',
    email: '',
    password: '',
    loading: false,
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [field]: e.detail.value })
  },

  async onRegister() {
    const { username, email, password } = this.data
    if (!username || !email || !password) {
      wx.showToast({ title: '请填写用户名、邮箱和密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    try {
      await authService.register({ username, email, password })
      wx.showToast({ title: '注册成功，请登录', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 800)
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ loading: false })
    }
  },
})
