const ENDPOINTS = require('../api/endpoints')
const { get, post } = require('../utils/request')

function fetchCommunityPosts(params) {
  return get(ENDPOINTS.community.posts, params)
}

function fetchCommunityPostDetail(id) {
  return get(ENDPOINTS.community.postById(id))
}

function createCommunityComment(postId, payload) {
  return post(ENDPOINTS.community.comments(postId), payload)
}

function likeCommunityPost(postId) {
  return post(ENDPOINTS.community.like(postId))
}

function favoriteCommunityPost(postId) {
  return post(ENDPOINTS.community.favorite(postId))
}

module.exports = {
  fetchCommunityPosts,
  fetchCommunityPostDetail,
  createCommunityComment,
  likeCommunityPost,
  favoriteCommunityPost,
}
