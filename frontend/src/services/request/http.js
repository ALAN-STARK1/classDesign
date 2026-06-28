import axios from 'axios'
import { ElMessage } from 'element-plus'
import { AppError, normalizeError } from './errors'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

export const request = axios.create({
  baseURL,
  timeout: 6000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('indras_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (!body || typeof body.code === 'undefined') {
      return body
    }
    if (body.code !== 0) {
      throw new AppError({
        code: body.code,
        message: body.message || '业务处理失败',
        data: body.data,
        type: body.code === 40100 ? 'auth' : 'business',
      })
    }
    return body.data
  },
  (error) => {
    throw normalizeError(error)
  },
)

export function handleRequestError(error) {
  const normalized = normalizeError(error)
  if (normalized.code === 40100) {
    localStorage.removeItem('indras_token')
    localStorage.removeItem('indras_user')
    if (window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
  }
  ElMessage.error(normalized.message)
  return normalized
}
