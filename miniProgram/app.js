const { TOKEN_KEY, USER_KEY } = require('./config/index')

App({
  globalData: {
    token: '',
    user: null,
  },

  onLaunch() {
    this.globalData.token = wx.getStorageSync(TOKEN_KEY) || ''
    this.globalData.user = wx.getStorageSync(USER_KEY) || null
  },

  setAuth(token, user) {
    this.globalData.token = token
    this.globalData.user = user
    wx.setStorageSync(TOKEN_KEY, token)
    wx.setStorageSync(USER_KEY, user)
  },

  clearAuth() {
    this.globalData.token = ''
    this.globalData.user = null
    wx.removeStorageSync(TOKEN_KEY)
    wx.removeStorageSync(USER_KEY)
  },

  ensureLogin() {
    if (!this.globalData.token) {
      wx.reLaunch({ url: '/pages/login/login' })
      return false
    }
    return true
  },
})
