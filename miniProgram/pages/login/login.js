const authService = require('../../services/auth')
const { showError } = require('../../utils/request')

Page({
  data: {
    username: 'alice',
    password: 'user123',
    loading: false,
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  async onLogin() {
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    try {
      const result = await authService.login({ username, password })
      const app = getApp()
      app.setAuth(result.token, result.user || { username })
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => wx.switchTab({ url: '/pages/dashboard/dashboard' }), 500)
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ loading: false })
    }
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' })
  },
})
