import { MOCK_SCENARIO, endpointScenario } from '../api/scenario'

const defaultScenario = import.meta.env.VITE_MOCK_DEFAULT_SCENARIO || MOCK_SCENARIO.SUCCESS

export function resolveScenario(endpointKey) {
  return window.__MOCK_SCENARIO__?.[endpointKey] || endpointScenario[endpointKey] || defaultScenario
}

export function withScenario(endpointKey, handlers) {
  const scenario = resolveScenario(endpointKey)
  const timeoutDelay = Number(import.meta.env.VITE_MOCK_TIMEOUT_DELAY || 8000)
  if (scenario === MOCK_SCENARIO.TIMEOUT) {
    return new Promise((resolve) => {
      window.setTimeout(() => resolve(handlers.timeout?.() || handlers.error?.()), timeoutDelay)
    })
  }
  return handlers[scenario] ? handlers[scenario]() : handlers.success()
}
