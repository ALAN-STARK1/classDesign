const ENDPOINTS = require('../api/endpoints')
const { get, post, put, patch, del } = require('../utils/request')

function fetchHealthProfile() {
  return get(ENDPOINTS.healthProfile.me)
}

function saveHealthProfile(payload) {
  return put(ENDPOINTS.healthProfile.me, payload)
}

function fetchHealthSummary() {
  return get(ENDPOINTS.healthProfile.summary)
}

function updateAllergens(allergens) {
  return put(ENDPOINTS.healthProfile.allergens, { allergens })
}

function updateRestrictions(restrictions) {
  return put(ENDPOINTS.healthProfile.restrictions, { restrictions })
}

function updateChronicDiseases(chronicDiseases) {
  return put(ENDPOINTS.healthProfile.chronicDiseases, { chronicDiseases })
}

function fetchWeightRecords(params) {
  return get(ENDPOINTS.weightRecords.base, params)
}

function fetchWeightTrend(params) {
  return get(ENDPOINTS.weightRecords.trend, params)
}

function createWeightRecord(payload) {
  return post(ENDPOINTS.weightRecords.base, {
    recordDate: payload.recordDate,
    weightKg: payload.weightKg,
  })
}

function deleteWeightRecord(id) {
  return del(ENDPOINTS.weightRecords.byId(id))
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
  updateAllergens,
  updateRestrictions,
  updateChronicDiseases,
  fetchWeightRecords,
  fetchWeightTrend,
  createWeightRecord,
  deleteWeightRecord,
  fetchCurrentGoalCycle,
  fetchGoalCycles,
  fetchGoalProgress,
}
