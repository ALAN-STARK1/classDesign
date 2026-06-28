import dayjs from 'dayjs'

export function today() {
  return dayjs().format('YYYY-MM-DD')
}

export function addDays(date, days) {
  return dayjs(date).add(days, 'day').format('YYYY-MM-DD')
}

export function formatDate(date) {
  return date ? dayjs(date).format('YYYY-MM-DD') : ''
}
