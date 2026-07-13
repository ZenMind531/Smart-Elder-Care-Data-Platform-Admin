//eslint-disable-next-line vue/multi-word-component-names
<template>
  <FullScreenLayout>
    <main class="flex min-h-dvh items-center justify-center bg-gray-50 p-6 dark:bg-gray-950">
      <div class="w-full max-w-md overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <section class="px-6 py-10 sm:px-10">

          <div>
            <div class="mb-8">
              <span
                class="mb-4 flex size-12 items-center justify-center rounded-xl bg-brand-600 text-xl font-bold text-white"
              >
                养
              </span>
              <h1 class="text-title-sm font-bold text-gray-900 text-balance dark:text-white">
                登录智慧养老管理系统
              </h1>
              <p class="mt-3 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
                护理员、医生和管理员可在此进入监护工作台。
              </p>
            </div>

            <form class="space-y-5" @submit.prevent="handleSubmit">
              <div>
                <label for="account" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                  账号
                </label>
                <input
                  id="account"
                  v-model="account"
                  type="text"
                  placeholder="请输入账号"
                  autocomplete="username"
                  class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                />
              </div>

              <div>
                <label for="password" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                  密码
                </label>
                <div class="relative">
                  <input
                    id="password"
                    v-model="password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="请输入登录密码"
                    autocomplete="current-password"
                    class="h-11 w-full rounded-lg border border-gray-300 bg-transparent py-2.5 pl-4 pr-12 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 -translate-y-1/2 rounded-md px-2 py-1 text-theme-xs text-gray-500 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-800"
                    @click="showPassword = !showPassword"
                  >
                    {{ showPassword ? '隐藏' : '显示' }}
                  </button>
                </div>
              </div>

              <div class="flex items-center justify-between gap-4">
                <label class="flex items-center gap-3 text-theme-sm text-gray-600 dark:text-gray-400">
                  <input
                    v-model="rememberMe"
                    type="checkbox"
                    class="size-4 rounded border-gray-300 text-brand-600 focus:ring-brand-500"
                  />
                  记住登录状态
                </label>
                <router-link to="/" class="text-theme-sm font-medium text-brand-700 hover:text-brand-800 dark:text-brand-300">
                  忘记密码
                </router-link>
              </div>

              <p
                v-if="loginMessage"
                class="rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
              >
                {{ loginMessage }}
              </p>

              <button
                type="submit"
                :disabled="submitting"
                class="flex w-full items-center justify-center rounded-lg bg-brand-600 px-4 py-3 text-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-70"
              >
                {{ submitting ? '登录中...' : '登录系统' }}
              </button>

              <p class="text-center text-theme-sm text-gray-600 dark:text-gray-400">
                还没有账号？
                <router-link to="/signup" class="font-medium text-brand-700 hover:text-brand-800 dark:text-brand-300">
                  去注册
                </router-link>
              </p>
            </form>
          </div>
        </section>
      </div>
    </main>
  </FullScreenLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import FullScreenLayout from '@/components/layout/FullScreenLayout.vue'
import { login } from '@/api/auth'
import { ApiError, setAuthSession } from '@/api/http'
import { getRoleLandingPath, normalizeStaffRole } from '@/config/roles'

const router = useRouter()
const route = useRoute()
const account = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(true)
const submitting = ref(false)
const loginMessage = ref('')

if (route.query.expired === '1') {
  loginMessage.value = '登录已过期，请重新登录'
}

const handleSubmit = async () => {
  loginMessage.value = ''

  const username = account.value.trim()

  if (!username || !password.value) {
    loginMessage.value = '请输入账号和密码'
    return
  }

  submitting.value = true

  try {
    const result = await login({
      username,
      password: password.value,
    })
    const normalizedRole = normalizeStaffRole(result.userInfo.roleName!)
    setAuthSession(result.token, {
      ...result.userInfo,
      roleName: normalizedRole,
    })
    router.push(getRoleLandingPath(normalizedRole))
  } catch (error) {
    loginMessage.value =
      error instanceof ApiError ? error.message : '登录服务暂不可用，请稍后重试'
    return
  } finally {
    submitting.value = false
  }
}
</script>
