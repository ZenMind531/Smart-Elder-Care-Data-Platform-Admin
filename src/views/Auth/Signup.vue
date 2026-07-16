// eslint-disable-next-line vue/multi-word-component-names
<template>
  <FullScreenLayout>
    <main class="flex min-h-dvh items-center justify-center bg-gray-50 p-6 dark:bg-gray-950">
      <div class="w-full max-w-md overflow-hidden rounded-[18px] border border-gray-200 bg-white shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <section class="px-6 py-10 sm:px-10">

          <div>
            <div class="mb-8">
              <span
                class="mb-4 flex size-12 items-center justify-center rounded-xl bg-brand-600 text-xl font-bold text-white"
              >
                养
              </span>
              <h1 class="text-title-sm font-bold text-gray-900 text-balance dark:text-white">
                注册智慧养老平台账号
              </h1>
              <p class="mt-3 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
                创建系统管理员、护士、医生账号。
              </p>
            </div>

            <form class="space-y-5" @submit.prevent="handleSubmit">
              <div class="grid gap-4 sm:grid-cols-2">
                <div>
                  <label for="username" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                    登录账号
                  </label>
                  <input
                    id="username"
                    v-model="form.username"
                    type="text"
                    required
                    autocomplete="username"
                    placeholder="例如 doctor001"
                    class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                  />
                </div>

                <div>
                  <label for="realName" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                    真实姓名
                  </label>
                  <input
                    id="realName"
                    v-model="form.realName"
                    type="text"
                    required
                    autocomplete="name"
                    placeholder="例如 张医生"
                    class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                  />
                </div>
              </div>

              <div>
                <label for="phoneNumber" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                  联系电话
                </label>
                <input
                  id="phoneNumber"
                  v-model="form.phoneNumber"
                  type="tel"
                  required
                  autocomplete="tel"
                  placeholder="例如 13800000000"
                  class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                />
              </div>

              <div>
                <label for="register-gender" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                  性别
                </label>
                <select
                  id="register-gender"
                  v-model="form.gender"
                  class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
                >
                  <option value="male">男</option>
                  <option value="female">女</option>
                </select>
              </div>

              <div>
                <label for="roleId" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                  注册角色
                </label>
                <select
                  id="roleId"
                  v-model.number="form.roleId"
                  class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:focus:border-brand-800"
                >
                  <option v-for="option in roleOptions" :key="option.id" :value="option.id">
                    {{ option.name }}
                  </option>
                </select>
              </div>

              <div class="grid gap-4 sm:grid-cols-2">
                <div>
                  <label for="register-password" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                    登录密码
                  </label>
                  <div class="relative">
                    <input
                      id="register-password"
                      v-model="form.password"
                      :type="showPassword ? 'text' : 'password'"
                      required
                      autocomplete="new-password"
                      placeholder="8-20 位，含字母和数字"
                      class="h-11 w-full rounded-lg border border-gray-300 bg-transparent py-2.5 pl-4 pr-12 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                    />
                    <button
                      type="button"
                      class="absolute right-3 top-1/2 inline-flex -translate-y-1/2 items-center justify-center rounded-md p-1.5 text-gray-500 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-800"
                      :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                      @click="showPassword = !showPassword"
                    >
                      <EyeOff v-if="showPassword" class="size-4" />
                      <Eye v-else class="size-4" />
                    </button>
                  </div>
                </div>

                <div>
                  <label for="confirm-password" class="mb-1.5 block text-sm font-medium text-gray-700 dark:text-gray-400">
                    确认密码
                  </label>
                  <input
                    id="confirm-password"
                    v-model="confirmPassword"
                    :type="showPassword ? 'text' : 'password'"
                    required
                    autocomplete="new-password"
                    placeholder="再次输入密码"
                    class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800"
                  />
                </div>
              </div>

              <p
                v-if="message"
                class="rounded-lg border px-3 py-2 text-theme-sm"
                :class="messageTone === 'success' ? 'border-success-200 bg-success-50 text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300' : messageTone === 'warning' ? 'border-warning-200 bg-warning-50 text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300' : 'border-error-200 bg-error-50 text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300'"
              >
                {{ message }}
              </p>

              <button
                type="submit"
                :disabled="submitting"
                class="flex w-full items-center justify-center gap-2 rounded-lg bg-brand-600 px-4 py-3 text-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-70"
              >
                <UserPlus class="size-4" />
                {{ submitting ? '注册中...' : '注册账号' }}
              </button>

              <p class="text-center text-theme-sm text-gray-600 dark:text-gray-400">
                已有账号？
                <router-link to="/signin" class="font-medium text-brand-700 hover:text-brand-800 dark:text-brand-300">
                  去登录
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
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Eye, EyeOff, UserPlus } from 'lucide-vue-next'
import FullScreenLayout from '@/components/layout/FullScreenLayout.vue'
import { register } from '@/api/auth'
import { ApiError } from '@/api/http'
import { getRoleNameById, staffRoleOptions } from '@/config/roles'

type MessageTone = 'error' | 'success' | 'warning'

const router = useRouter()
const submitting = ref(false)
const showPassword = ref(false)
const confirmPassword = ref('')
const message = ref('')
const messageTone = ref<MessageTone>('error')

const roleOptions = staffRoleOptions.filter((r) => r.id !== 1)

const form = reactive({
  username: '',
  password: '',
  realName: '',
  phoneNumber: '',
  roleId: 2,
  gender: 'male' as 'male' | 'female',
})

const validateForm = () => {
  if (!form.username.trim()) return '请输入登录账号'
  if (!form.realName.trim()) return '请输入真实姓名'
  if (!form.phoneNumber.trim()) return '请输入联系电话'
  if (!/^(?=.*[a-zA-Z])(?=.*\d).{8,20}$/.test(form.password)) {
    return '密码必须包含字母和数字，长度 8-20 位'
  }
  if (form.password !== confirmPassword.value) return '两次输入的密码不一致'
  return ''
}

const handleSubmit = async () => {
  message.value = ''
  messageTone.value = 'error'

  const validationError = validateForm()
  if (validationError) {
    message.value = validationError
    return
  }

  submitting.value = true

  try {
    const payload = {
      username: form.username.trim(),
      password: form.password,
      realName: form.realName.trim(),
      phoneNumber: form.phoneNumber.trim(),
      roleId: form.roleId,
      gender: form.gender,
    }
    await register(payload)

    messageTone.value = 'success'
    message.value = `${getRoleNameById(form.roleId)}账号注册成功，请等待系统管理员审核后再登录`
    window.setTimeout(() => {
      void router.push('/signin')
    }, 1500)
  } catch (error) {
    const isRequestError = error instanceof ApiError

    if (isRequestError && error.status && error.status < 500) {
      message.value = error.message || '注册失败，请检查账号信息'
      return
    }

    messageTone.value = 'warning'
    message.value = '后端注册接口暂不可用，页面已完成注册流程，后端恢复后可直接联调'
  } finally {
    submitting.value = false
  }
}
</script>
