const { API_BASE_URL, TOKEN_KEY, REQUEST_TIMEOUT, AI_REQUEST_TIMEOUT } = require('../config/index')

class AppError extends Error {
  constructor({ code, message, type }) {
    super(message)
    this.code = code
    this.type = type || 'business'
  }
}

function buildQuery(params) {
  if (!params) return ''
  const parts = Object.keys(params)
    .filter((key) => params[key] !== undefined && params[key] !== null && params[key] !== '')
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
  return parts.length ? `?${parts.join('&')}` : ''
}

function request(options) {
  const token = wx.getStorageSync(TOKEN_KEY)
  const header = Object.assign(
    { 'Content-Type': 'application/json' },
    options.header || {},
  )
  if (token) {
    header.Authorization = `Bearer ${token}`
  }

  return new Promise((resolve, reject) => {
    wx.request({
      url: `${API_BASE_URL}${options.url}${buildQuery(options.params)}`,
      method: options.method || 'GET',
      data: options.data,
      header,
      timeout: options.timeout || REQUEST_TIMEOUT,
      success(res) {
        const body = res.data
        if (!body || typeof body.code === 'undefined') {
          resolve(body)
          return
        }
        if (body.code !== 0) {
          if (body.code === 40100) {
            wx.removeStorageSync(TOKEN_KEY)
            wx.removeStorageSync('indras_user')
            const app = getApp()
            if (app && app.clearAuth) app.clearAuth()
            wx.reLaunch({ url: '/pages/login/login' })
          }
          reject(new AppError({
            code: body.code,
            message: body.message || '业务处理失败',
            type: body.code === 40100 ? 'auth' : 'business',
          }))
          return
        }
        resolve(body.data)
      },
      fail(err) {
        reject(new AppError({
          code: -1,
          message: err.errMsg || '网络请求失败',
          type: 'network',
        }))
      },
    })
  })
}

function get(url, params, options) {
  return request(Object.assign({}, options, { url, params, method: 'GET' }))
}

function post(url, data, options) {
  return request(Object.assign({}, options, { url, data, method: 'POST' }))
}

function put(url, data, options) {
  return request(Object.assign({}, options, { url, data, method: 'PUT' }))
}

function patch(url, data, options) {
  return request(Object.assign({}, options, { url, data, method: 'PATCH' }))
}

function del(url, params, options) {
  return request(Object.assign({}, options, { url, params, method: 'DELETE' }))
}

function upload(url, filePath, formData, options) {
  const token = wx.getStorageSync(TOKEN_KEY)
  const header = Object.assign({}, options && options.header)
  if (token) header.Authorization = `Bearer ${token}`

  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: `${API_BASE_URL}${url}`,
      filePath,
      name: 'file',
      formData: formData || {},
      header,
      timeout: (options && options.timeout) || AI_REQUEST_TIMEOUT,
      success(res) {
        let body
        try {
          body = JSON.parse(res.data)
        } catch (e) {
          reject(new AppError({ code: -1, message: '上传响应解析失败', type: 'network' }))
          return
        }
        if (body.code !== 0) {
          reject(new AppError({ code: body.code, message: body.message || '上传失败', type: 'business' }))
          return
        }
        resolve(body.data)
      },
      fail(err) {
        reject(new AppError({ code: -1, message: err.errMsg || '上传失败', type: 'network' }))
      },
    })
  })
}

function showError(error) {
  wx.showToast({
    title: (error && error.message) || '请求失败',
    icon: 'none',
    duration: 2500,
  })
}

module.exports = { request, get, post, put, patch, del, upload, AppError, showError }
