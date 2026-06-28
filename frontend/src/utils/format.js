export function formatKg(value) {
  return value || value === 0 ? `${Number(value).toFixed(1)} kg` : '-'
}

export function formatCalorie(value) {
  return value || value === 0 ? `${Math.round(Number(value))} kcal` : '-'
}

export function percent(value) {
  return value || value === 0 ? `${Math.round(Number(value))}%` : '-'
}
