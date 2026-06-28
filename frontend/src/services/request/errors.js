export class AppError extends Error {
  constructor({ code = 50000, message = '请求失败', data = null, type = 'business' } = {}) {
    super(message)
    this.name = 'AppError'
    this.code = code
    this.data = data
    this.type = type
  }
}

export function normalizeError(error) {
  if (error instanceof AppError) return error
  if (error.code === 'ECONNABORTED') {
    return new AppError({ code: 40800, message: '请求超时，请稍后重试', type: 'timeout' })
  }
  if (error.response?.data) {
    return new AppError({
      code: error.response.data.code || error.response.status || 50000,
      message: error.response.data.message || '服务响应异常',
      data: error.response.data.data || null,
      type: 'http',
    })
  }
  return new AppError({ code: 50000, message: error.message || '网络连接异常', type: 'network' })
}
