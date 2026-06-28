import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { fetchMe, login as loginApi, register as registerApi } from '../services/modules/auth.service'

const TOKEN_KEY = 'indras_token'
const USER_KEY = 'indras_user'

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref(readStoredUser())
  const bootstrapped = ref(false)

  const isAuthenticated = computed(() => Boolean(token.value))
  const role = computed(() => user.value?.role || 'USER')

  function persist(nextToken, nextUser) {
    token.value = nextToken || ''
    user.value = nextUser || null
    if (token.value) localStorage.setItem(TOKEN_KEY, token.value)
    else localStorage.removeItem(TOKEN_KEY)
    if (user.value) localStorage.setItem(USER_KEY, JSON.stringify(user.value))
    else localStorage.removeItem(USER_KEY)
  }

  async function login(payload) {
    const result = await loginApi(payload)
    persist(result.token, result.user)
    return result
  }

  async function register(payload) {
    return registerApi(payload)
  }

  async function loadMe() {
    if (!token.value) {
      bootstrapped.value = true
      return null
    }
    const result = await fetchMe()
    user.value = result
    localStorage.setItem(USER_KEY, JSON.stringify(result))
    bootstrapped.value = true
    return result
  }

  function logout() {
    persist('', null)
  }

  return {
    token,
    user,
    bootstrapped,
    isAuthenticated,
    role,
    login,
    register,
    loadMe,
    logout,
  }
})
