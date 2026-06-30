function pad(n) {
  return n < 10 ? `0${n}` : `${n}`
}

function formatDate(d) {
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

function today() {
  return formatDate(new Date())
}

function addDays(dateStr, days) {
  const d = new Date(`${dateStr}T00:00:00`)
  d.setDate(d.getDate() + days)
  return formatDate(d)
}

function subtractDays(dateStr, days) {
  return addDays(dateStr, -days)
}

function monthOf(dateStr) {
  return dateStr.slice(0, 7)
}

module.exports = { today, addDays, subtractDays, monthOf, formatDate }
