<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">Profile</span>
      <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
        个人中心
      </h1>
    </div>

    <p
      v-if="profileOk"
      class="mb-4 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
      role="status"
    >
      {{ profileOk }}
    </p>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section
        class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-7"
        :aria-busy="profileLoading || profileSaving"
      >
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              基本信息
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              维护当前账号的姓名和联系电话，用户名与角色由管理员统一管理。
            </p>
          </div>
          <button
            v-if="!profileEditing"
            type="button"
            class="inline-flex w-fit items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
            @click="startProfileEdit"
          >
            <Pencil class="size-4" aria-hidden="true" />
            编辑资料
          </button>
        </div>

        <p
          v-if="profileLoading"
          class="mt-5 rounded-lg border border-brand-200 bg-brand-50 px-3 py-2 text-theme-sm text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300"
          role="status"
        >
          正在同步账号资料...
        </p>

        <p
          v-if="profileError"
          id="profile-error"
          class="mt-5 rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
          role="alert"
        >
          {{ profileError }}
        </p>

        <form
          v-if="profileEditing"
          class="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2"
          :aria-describedby="profileError ? 'profile-error' : undefined"
          @submit.prevent="submitProfile"
        >
          <div>
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              用户名
            </span>
            <p
              class="flex h-11 items-center rounded-lg border border-gray-200 bg-gray-50 px-4 text-theme-sm text-gray-600 dark:border-gray-800 dark:bg-gray-900 dark:text-gray-300"
            >
              {{ user.username || '—' }}
            </p>
          </div>
          <div>
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              角色
            </span>
            <p
              class="flex h-11 items-center rounded-lg border border-gray-200 bg-gray-50 px-4 text-theme-sm text-gray-600 dark:border-gray-800 dark:bg-gray-900 dark:text-gray-300"
            >
              {{ roleLabel }}
            </p>
          </div>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              姓名
            </span>
            <input
              v-model="profileForm.realName"
              type="text"
              required
              autocomplete="name"
              :aria-invalid="Boolean(profileError)"
              :aria-describedby="profileError ? 'profile-error' : undefined"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              手机号
            </span>
            <input
              v-model="profileForm.phoneNumber"
              type="tel"
              required
              autocomplete="tel"
              :aria-invalid="Boolean(profileError)"
              :aria-describedby="profileError ? 'profile-error' : undefined"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <div class="flex flex-wrap items-center justify-end gap-2 sm:col-span-2">
            <button
              type="button"
              class="inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
              @click="cancelProfileEdit"
            >
              <X class="size-4" aria-hidden="true" />
              取消
            </button>
            <button
              type="submit"
              :disabled="profileSaving"
              class="inline-flex items-center justify-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 focus:outline-none focus:ring-3 focus:ring-brand-500/20 disabled:cursor-not-allowed disabled:opacity-60"
            >
              <Save class="size-4" aria-hidden="true" />
              {{ profileSaving ? '保存中...' : '保存资料' }}
            </button>
          </div>
        </form>

        <div v-else class="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">用户名</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">
              {{ user.username || '—' }}
            </p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">姓名</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">
              {{ user.realName || '—' }}
            </p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">角色</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">
              {{ roleLabel }}
            </p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">手机号</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">
              {{ user.phoneNumber || '—' }}
            </p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">账号状态</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">
              {{ statusLabel }}
            </p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">账号 ID</span>
            <p class="mt-1 text-theme-sm font-medium tabular-nums text-gray-800 dark:text-white/90">
              {{ user.id || '—' }}
            </p>
          </div>
        </div>
      </section>

      <section
        class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-5"
      >
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          安全设置
        </h2>
        <div class="mt-5 space-y-4">
          <div class="flex items-center justify-between gap-4 rounded-xl border border-gray-100 p-4 dark:border-gray-800">
            <div>
              <p class="text-theme-sm font-medium text-gray-800 dark:text-white/90">登录密码</p>
              <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
                定期更换密码以提高账号安全性。
              </p>
            </div>
            <button
              type="button"
              class="inline-flex shrink-0 items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
              @click="openPwdModal"
            >
              <LockKeyhole class="size-4" aria-hidden="true" />
              修改
            </button>
          </div>
        </div>
      </section>
    </div>

    <div
      v-if="pwdModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="password-dialog-title"
      @click.self="closePwdModal"
      @keydown.esc="closePwdModal"
    >
      <div class="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3
          id="password-dialog-title"
          class="text-lg font-semibold text-gray-800 dark:text-white/90"
        >
          修改密码
        </h3>
        <form
          class="mt-4 space-y-3"
          :aria-describedby="pwdError ? 'password-error' : undefined"
          @submit.prevent="submitPwd"
        >
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              旧密码
            </span>
            <input
              v-model="pwd.oldPassword"
              type="password"
              required
              autocomplete="current-password"
              :aria-invalid="Boolean(pwdError)"
              :aria-describedby="pwdError ? 'password-error' : undefined"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              新密码
            </span>
            <input
              v-model="pwd.newPassword"
              type="password"
              required
              autocomplete="new-password"
              placeholder="至少 6 位"
              :aria-invalid="Boolean(pwdError)"
              :aria-describedby="pwdError ? 'password-error' : undefined"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              确认新密码
            </span>
            <input
              v-model="pwd.confirmPassword"
              type="password"
              required
              autocomplete="new-password"
              :aria-invalid="Boolean(pwdError)"
              :aria-describedby="pwdError ? 'password-error' : undefined"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <p
            v-if="pwdError"
            id="password-error"
            class="text-theme-xs text-error-600 dark:text-error-400"
            role="alert"
          >
            {{ pwdError }}
          </p>
          <p
            v-if="pwdOk"
            class="text-theme-xs text-success-600 dark:text-success-400"
            role="status"
          >
            {{ pwdOk }}
          </p>
          <div class="flex justify-end gap-2">
            <button
              type="button"
              class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
              @click="closePwdModal"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="pwdSubmitting"
              class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 focus:outline-none focus:ring-3 focus:ring-brand-500/20 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {{ pwdSubmitting ? '修改中...' : '确认修改' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { LockKeyhole, Pencil, Save, X } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import {
  ApiError,
  getAuthToken,
  getStoredUser,
  setAuthSession,
  type StoredUserInfo,
} from '@/api/http'
import { getCurrentUser, updatePassword } from '@/api/auth'
import { updateUser } from '@/api/users'
import { getRoleNameById, getRoleOption } from '@/config/roles'

const stored = getStoredUser()
const user = reactive<StoredUserInfo>({
  id: stored?.id,
  username: stored?.username || '',
  realName: stored?.realName || '',
  phoneNumber: stored?.phoneNumber || '',
  roleName: stored?.roleName || '',
  roleId: stored?.roleId,
  status: stored?.status,
})

const profileForm = reactive({
  realName: user.realName || '',
  phoneNumber: user.phoneNumber || '',
})

const profileLoading = ref(false)
const profileSaving = ref(false)
const profileEditing = ref(false)
const profileError = ref('')
const profileOk = ref('')

const roleLabel = computed(() => {
  if (user.roleName) return getRoleOption(user.roleName).name
  if (user.roleId) return getRoleNameById(user.roleId)
  return '—'
})

const statusLabel = computed(() => {
  const labels: Record<string, string> = {
    enabled: '已启用',
    pending: '待审核',
    disabled: '已禁用',
  }
  return user.status ? labels[user.status] || user.status : '—'
})

const applyUserInfo = (info: StoredUserInfo) => {
  user.id = info.id ?? user.id
  user.username = info.username ?? user.username
  user.realName = info.realName ?? user.realName
  user.phoneNumber = info.phoneNumber ?? user.phoneNumber
  user.roleId = info.roleId ?? user.roleId
  user.status = info.status ?? user.status
  user.roleName = info.roleName || user.roleName || stored?.roleName || ''
}

const resetProfileForm = () => {
  profileForm.realName = user.realName || ''
  profileForm.phoneNumber = user.phoneNumber || ''
}

const persistUserInfo = () => {
  const token = getAuthToken()
  if (!token) return

  setAuthSession(token, {
    ...(getStoredUser() || {}),
    id: user.id,
    username: user.username,
    realName: user.realName,
    phoneNumber: user.phoneNumber,
    roleName: user.roleName,
    roleId: user.roleId,
    status: user.status,
  })
}

onMounted(async () => {
  profileLoading.value = true
  profileError.value = ''

  try {
    const latestUser = await getCurrentUser()
    applyUserInfo(latestUser)
    resetProfileForm()
    persistUserInfo()
  } catch (err) {
    profileError.value =
      err instanceof ApiError ? err.message : '账号资料同步失败，请稍后刷新重试'
  } finally {
    profileLoading.value = false
  }
})

const startProfileEdit = () => {
  profileError.value = ''
  profileOk.value = ''
  resetProfileForm()
  profileEditing.value = true
}

const cancelProfileEdit = () => {
  if (profileSaving.value) return
  profileError.value = ''
  resetProfileForm()
  profileEditing.value = false
}

const submitProfile = async () => {
  profileError.value = ''
  profileOk.value = ''

  const realName = profileForm.realName.trim()
  const phoneNumber = profileForm.phoneNumber.trim()

  if (!user.id) {
    profileError.value = '当前登录信息缺少用户 ID，请重新登录后再试'
    return
  }

  if (!realName) {
    profileError.value = '请输入姓名'
    return
  }

  if (!/^1[3-9]\d{9}$/.test(phoneNumber)) {
    profileError.value = '请输入 11 位有效手机号'
    return
  }

  profileSaving.value = true

  try {
    await updateUser(user.id, { realName, phoneNumber })
    user.realName = realName
    user.phoneNumber = phoneNumber
    persistUserInfo()
    profileEditing.value = false
    profileOk.value = '个人资料已更新'
  } catch (err) {
    profileError.value =
      err instanceof ApiError ? err.message : '资料保存失败，请稍后重试'
  } finally {
    profileSaving.value = false
  }
}

const pwdModalOpen = ref(false)
const pwdSubmitting = ref(false)
const pwdError = ref('')
const pwdOk = ref('')
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const openPwdModal = () => {
  pwdError.value = ''
  pwdOk.value = ''
  pwdModalOpen.value = true
}

const closePwdModal = () => {
  if (pwdSubmitting.value) return
  pwdModalOpen.value = false
  pwdError.value = ''
  pwdOk.value = ''
}

const submitPwd = async () => {
  pwdError.value = ''
  pwdOk.value = ''
  if (pwd.newPassword.length < 6) {
    pwdError.value = '新密码至少 6 位'
    return
  }
  if (pwd.newPassword !== pwd.confirmPassword) {
    pwdError.value = '两次密码不一致'
    return
  }
  pwdSubmitting.value = true
  try {
    await updatePassword({
      oldPassword: pwd.oldPassword,
      newPassword: pwd.newPassword,
      confirmPassword: pwd.confirmPassword,
    })
    pwdOk.value = '密码修改成功'
    pwd.oldPassword = ''
    pwd.newPassword = ''
    pwd.confirmPassword = ''
    setTimeout(() => {
      closePwdModal()
    }, 1200)
  } catch (err) {
    pwdError.value = err instanceof ApiError ? err.message : '修改失败'
  } finally {
    pwdSubmitting.value = false
  }
}
</script>
