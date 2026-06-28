import Mock from 'mockjs'
import { createLoginResult, mockUser } from '../factories/auth'
import { failure, parseBody, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'
const delay = Number(import.meta.env.VITE_MOCK_DELAY || 300)

export function registerAuthMocks() {
  Mock.setup({ timeout: delay })

  Mock.mock(`${baseUrl}/auth/register`, 'post', (options) =>
    withScenario('POST /auth/register', {
      success: () => {
        const body = parseBody(options)
        return success({
          id: 3,
          username: body.username,
          email: body.email,
          role: 'USER',
          status: 'ENABLED',
        })
      },
      empty: () => success(null),
      error: () =>
        failure(40900, '用户名或邮箱已存在', {
          fieldErrors: [{ field: 'username', message: '该用户名已被使用' }],
        }),
      timeout: () => failure(40800, '注册请求超时'),
    }),
  )

  Mock.mock(`${baseUrl}/auth/login`, 'post', (options) =>
    withScenario('POST /auth/login', {
      success: () => success(createLoginResult()),
      empty: () => success(null),
      error: () => {
        const body = parseBody(options)
        if (body.username === 'expired') return failure(40100, 'Token 已失效')
        return failure(40001, '用户名或密码错误')
      },
      timeout: () => failure(40800, '登录接口超时'),
    }),
  )

  Mock.mock(`${baseUrl}/auth/me`, 'get', () =>
    withScenario('GET /auth/me', {
      success: () => success(mockUser),
      empty: () => success(null),
      error: () => failure(40100, '登录已过期，请重新登录'),
      timeout: () => failure(40800, '获取用户信息超时'),
    }),
  )

  Mock.mock(`${baseUrl}/auth/password`, 'put', () =>
    withScenario('PUT /auth/password', {
      success: () => success(true),
      empty: () => success(false),
      error: () => failure(40001, '旧密码不正确'),
      timeout: () => failure(40800, '修改密码超时'),
    }),
  )
}
