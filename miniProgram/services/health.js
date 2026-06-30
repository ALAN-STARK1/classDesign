const ENDPOINTS = require('../api/endpoints')
const { get, post, put, patch } = require('../utils/request')

function fetchHealthProfile() {
  return get(ENDPOINTS.healthProfile.me)
}

function saveHealthProfile(payload) {
  return put(ENDPOINTS.healthProfile.me, payload)
}

function fetchHealthSummary() {
  return get(ENDPOINTS.healthProfile.summary)
}

function fetchCurrentGoalCycle() {
  return get(ENDPOINTS.healthGoalCycles.current)
}

function fetchGoalCycles(params) {
  return get(ENDPOINTS.healthGoalCycles.base, params)
}

function fetchGoalProgress(id) {
  return get(ENDPOINTS.healthGoalCycles.progress(id))
}

module.exports = {
  fetchHealthProfile,
  saveHealthProfile,
  fetchHealthSummary,
  fetchCurrentGoalCycle,
  fetchGoalCycles,
  fetchGoalProgress,
}
