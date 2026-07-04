import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function uploadCommunityPostImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(ENDPOINTS.community.postImages, formData)
}

export function createCommunityPost(payload) {
  return request.post(ENDPOINTS.community.posts, payload)
}

export function createCommunityPostFromAiRecipe(aiRecipeId, payload = {}) {
  return request.post(ENDPOINTS.community.fromAiRecipe(aiRecipeId), payload)
}

export function fetchCommunityPosts(params) {
  return request.get(ENDPOINTS.community.posts, { params })
}

export function fetchCommunityPostDetail(id) {
  return request.get(ENDPOINTS.community.postById(id))
}

export function createCommunityComment(id, payload) {
  return request.post(ENDPOINTS.community.comments(id), payload)
}

export function likeCommunityPost(id) {
  return request.post(ENDPOINTS.community.like(id))
}

export function favoriteCommunityPost(id) {
  return request.post(ENDPOINTS.community.favorite(id))
}
