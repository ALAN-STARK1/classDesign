const communityService = require('../../services/community')
const { showError } = require('../../utils/request')

Page({
  data: {
    loading: true,
    posts: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadPosts()
  },

  async loadPosts() {
    this.setData({ loading: true })
    try {
      const result = await communityService.fetchCommunityPosts({ page: 1, pageSize: 20 })
      this.setData({ posts: result.items || [], loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  goDetail(e) {
    wx.navigateTo({ url: `/pages/community-detail/community-detail?id=${e.currentTarget.dataset.id}` })
  },
})
