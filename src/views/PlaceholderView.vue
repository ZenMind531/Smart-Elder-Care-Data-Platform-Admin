<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">Profile</span>
      <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">个人中心</h1>
    </div>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-6">
        <h2 class="text-lg font-semibold text-gray-800 dark:text-white/90">基本信息</h2>
        <div class="mt-5 grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">用户名</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">{{ user.username || '—' }}</p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">姓名</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">{{ user.realName || '—' }}</p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">角色</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">{{ user.roleName || '—' }}</p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">手机号</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">{{ user.phone || '—' }}</p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">性别</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">{{ genderLabel }}</p>
          </div>
          <div>
            <span class="text-theme-xs text-gray-500 dark:text-gray-400">科室</span>
            <p class="mt-1 text-theme-sm font-medium text-gray-800 dark:text-white/90">{{ user.department || '—' }}</p>
          </div>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-6">
        <h2 class="text-lg font-semibold text-gray-800 dark:text-white/90">安全设置</h2>
        <div class="mt-5 space-y-4">
          <div class="flex items-center justify-between rounded-xl border border-gray-100 p-4 dark:border-gray-800">
            <div>
              <p class="text-theme-sm font-medium text-gray-800 dark:text-white/90">登录密码</p>
              <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">定期更换密码以提高账号安全性。</p>
            </div>
            <button type="button" class="rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="pwdModalOpen = true">修改</button>
          </div>
        </div>
      </section>
    </div>

    <div v-if="pwdModalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="pwdModalOpen = false">
      <div class="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">修改密码</h3>
        <form class="mt-4 space-y-3" @submit.prevent="submitPwd">
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">旧密码</span><input v-model="pwd.oldPassword" type="password" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">新密码</span><input v-model="pwd.newPassword" type="password" required placeholder="至少 6 位" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">确认新密码</span><input v-model="pwd.confirmPassword" type="password" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <p v-if="pwdError" class="text-theme-xs text-error-600 dark:text-error-400">{{ pwdError }}</p>
          <p v-if="pwdOk" class="text-theme-xs text-success-600 dark:text-success-400">{{ pwdOk }}</p>
          <div class="flex justify-end gap-2"><button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="pwdModalOpen = false">取消</button><button type="submit" :disabled="pwdSubmitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:opacity-60">{{ pwdSubmitting ? '修改中…' : '确认修改' }}</button></div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser, ApiError } from '@/api/http'
import { updatePassword } from '@/api/auth'

const stored = getStoredUser()
const user = reactive({
  username: stored?.username || '',
  realName: stored?.realName || '',
  roleName: stored?.roleName || '',
  phone: '',
  department: '',
  title: '',
  gender: '',
})

const genderLabel = computed(() => user.gender === 'male' ? '男' : user.gender === 'female' ? '女' : '—')

const pwdModalOpen = ref(false)
const pwdSubmitting = ref(false)
const pwdError = ref('')
const pwdOk = ref('')
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const submitPwd = async () => {
  pwdError.value = ''; pwdOk.value = ''
  if (pwd.newPassword.length < 6) { pwdError.value = '新密码至少 6 位'; return }
  if (pwd.newPassword !== pwd.confirmPassword) { pwdError.value = '两次密码不一致'; return }
  pwdSubmitting.value = true
  try {
    await updatePassword({ oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    pwdOk.value = '密码修改成功'
    pwd.oldPassword = ''; pwd.newPassword = ''; pwd.confirmPassword = ''
    setTimeout(() => { pwdModalOpen.value = false; pwdOk.value = '' }, 1200)
  } catch (err) {
    pwdError.value = err instanceof ApiError ? err.message : '修改失败'
  } finally { pwdSubmitting.value = false }
}
</script>
