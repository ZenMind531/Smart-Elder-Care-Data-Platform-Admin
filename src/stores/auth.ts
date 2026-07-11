import { defineStore } from 'pinia'
import { login, logout, getCurrentUser } from '@/api/auth'
import {
  getAuthToken,
  getStoredUser,
  setAuthSession,
  clearAuthSession,
  type StoredUserInfo,
} from '@/api/http'
import { defaultStaffRole, normalizeStaffRole } from '@/config/roles'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getAuthToken(),
    userInfo: getStoredUser() as StoredUserInfo | null,
    loading: false,
  }),

  getters: {
    isAuthenticated: (state) => Boolean(state.token),
    roleName: (state) => normalizeStaffRole(state.userInfo?.roleName || defaultStaffRole),
    displayName: (state) => state.userInfo?.realName || state.userInfo?.username || '工作人员',
  },

  actions: {
    async loginWithPassword(username: string, password: string) {
      const result = await login({ username, password })
      const roleName = normalizeStaffRole(result.userInfo.roleName)

      setAuthSession(result.token, {
        ...result.userInfo,
        roleName,
      })

      this.token = result.token
      this.userInfo = {
        ...result.userInfo,
        roleName,
      }
    },

    async loadCurrentUser() {
      if (!this.token) return

      try {
        const userInfo = await getCurrentUser()
        const roleName = normalizeStaffRole(userInfo.roleName)

        this.userInfo = {
          ...userInfo,
          roleName,
        }

        setAuthSession(this.token, this.userInfo)
      } catch {
        this.clearSession()
      }
    },

    async logoutUser() {
      try {
        await logout()
      } catch {
        // 后端退出失败时，前端仍然清理本地登录态
      }

      this.clearSession()
    },

    clearSession() {
      clearAuthSession()
      this.token = null
      this.userInfo = null
    },
  },
})