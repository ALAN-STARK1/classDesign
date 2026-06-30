function formatCalorie(value) {
  const num = Number(value || 0)
  return `${Math.round(num)} kcal`
}

function percent(value) {
  const num = Number(value || 0)
  return `${Math.round(num)}%`
}

function labelOf(map, key, fallback) {
  if (map && map[key]) return map[key]
  return fallback || key || '-'
}

module.exports = { formatCalorie, percent, labelOf }
