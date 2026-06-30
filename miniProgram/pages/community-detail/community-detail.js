const communityService = require('../../services/community')
const { showError } = require('../../utils/request')

Page({
  data: {
    id: null,
    loading: true,
    post: null,
    comments: [],
    commentText: '',
  },

  onLoad(options) {
    if (!getApp().ensureLogin()) return
    this.setData({ id: options.id })
    this.loadDetail()
  },

  async loadDetail() {
    try {
      const post = await communityService.fetchCommunityPostDetail(this.data.id)
      this.setData({
        post,
        comments: post.comments || [],
        loading: false,
      })
      wx.setNavigationBarTitle({ title: post.title || '帖子详情' })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  onCommentInput(e) {
    this.setData({ commentText: e.detail.value })
  },

  async onSubmitComment() {
    if (!this.data.commentText.trim()) return
    try {
      await communityService.createCommunityComment(this.data.id, { content: this.data.commentText })
      wx.showToast({ title: '评论成功', icon: 'success' })
      this.setData({ commentText: '' })
      this.loadDetail()
    } catch (err) {
      showError(err)
    }
  },

  async onLike() {
    try {
      await communityService.likeCommunityPost(this.data.id)
      wx.showToast({ title: '已点赞', icon: 'success' })
      this.loadDetail()
    } catch (err) {
      showError(err)
    }
  },

  async onFavorite() {
    try {
      await communityService.favoriteCommunityPost(this.data.id)
      wx.showToast({ title: '已收藏', icon: 'success' })
    } catch (err) {
      showError(err)
    }
  },
})
