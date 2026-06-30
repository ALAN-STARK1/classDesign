const ENDPOINTS = require('../api/endpoints')
const { get, post, put, patch } = require('../utils/request')

function login(payload) {
  return post(ENDPOINTS.auth.login, payload)
}

function register(payload) {
  return post(ENDPOINTS.auth.register, payload)
}

function fetchMe() {
  return get(ENDPOINTS.auth.me)
}

module.exports = { login, register, fetchMe }
