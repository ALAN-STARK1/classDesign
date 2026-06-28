export const mockUser = {
  id: 2,
  username: 'alice',
  email: 'alice@example.com',
  role: 'ADMIN',
  status: 'ENABLED',
}

export function createLoginResult(overrides = {}) {
  return {
    token: 'mock-jwt-token-for-indras',
    tokenType: 'Bearer',
    expiresIn: 7200,
    user: mockUser,
    ...overrides,
  }
}
