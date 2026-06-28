import Mock from 'mockjs'

export function success(data) {
  return {
    code: 0,
    message: 'success',
    data,
    timestamp: new Date().toISOString(),
  }
}

export function failure(code = 40001, message = '参数校验失败', data = null) {
  return {
    code,
    message,
    data,
    timestamp: new Date().toISOString(),
  }
}

export function page(items, page = 1, size = 10, total = items.length) {
  return {
    items,
    page,
    size,
    total,
    pages: Math.max(1, Math.ceil(total / size)),
  }
}

export function pick(values) {
  return values[Mock.Random.integer(0, values.length - 1)]
}

export function parseBody(options) {
  try {
    return options.body ? JSON.parse(options.body) : {}
  } catch {
    return {}
  }
}

export function parseQuery(url) {
  const search = url.split('?')[1] || ''
  return Object.fromEntries(new URLSearchParams(search))
}
