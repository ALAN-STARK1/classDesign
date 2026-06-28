import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function register(payload) {
  return request.post(ENDPOINTS.auth.register, payload)
}

export function login(payload) {
  return request.post(ENDPOINTS.auth.login, payload)
}

export function fetchMe() {
  return request.get(ENDPOINTS.auth.me)
}

export function changePassword(payload) {
  return request.put(ENDPOINTS.auth.password, payload)
}
