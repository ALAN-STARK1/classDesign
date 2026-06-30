// 开发环境：微信开发者工具需勾选「不校验合法域名」
// 真机调试请将 localhost 改为电脑局域网 IP，如 http://192.168.1.100:8080/api/v1
const API_BASE_URL = 'http://localhost:8080/api/v1'

module.exports = {
  API_BASE_URL,
  TOKEN_KEY: 'indras_token',
  USER_KEY: 'indras_user',
  REQUEST_TIMEOUT: 15000,
}
