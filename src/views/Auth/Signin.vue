<template>
  <FullScreenLayout>
    <main class="min-h-dvh bg-gray-50 p-6 dark:bg-gray-950">
      <div class="mx-auto grid min-h-[calc(100dvh-48px)] max-w-6xl overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-theme-lg dark:border-gray-800 dark:bg-gray-900 lg:grid-cols-[0.95fr_1.05fr]">
        <section class="flex flex-col justify-center px-6 py-10 sm:px-10 lg:px-12">
          <router-link
            to="/"
            class="mb-10 inline-flex w-fit items-center text-theme-sm font-medium text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
          >
            返回监护总览
          </router-link>

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
                  placeholder="请输入手机号或工号"
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

              <button
                type="submit"
                class="flex w-full items-center justify-center rounded-lg bg-brand-600 px-4 py-3 text-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
              >
                登录系统
              </button>
            </form>
          </div>
        </section>

        <section class="hidden bg-brand-950 p-10 text-white lg:flex lg:flex-col lg:justify-between">
          <div>
            <p class="text-theme-sm text-brand-100">Smart Elder Care</p>
            <h2 class="mt-4 max-w-md text-title-sm font-bold text-balance">
              用一个工作台看清老人状态、设备在线和护理进度
            </h2>
          </div>
          <div class="grid gap-4">
            <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
              <p class="text-theme-sm text-brand-100">今日重点</p>
              <p class="mt-2 text-2xl font-semibold tabular-nums">12 条告警 · 27 项待办</p>
            </div>
            <div class="rounded-2xl border border-white/10 bg-white/5 p-5">
              <p class="text-theme-sm text-brand-100">设备状态</p>
              <p class="mt-2 text-2xl font-semibold tabular-nums">342 台在线 · 98.3%</p>
            </div>
          </div>
        </section>
      </div>
    </main>
  </FullScreenLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import FullScreenLayout from '@/components/layout/FullScreenLayout.vue'

const router = useRouter()
const account = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(true)

const handleSubmit = () => {
  router.push('/')
}
</script>
