<template>
  <section
    class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
  >
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          账号审核
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          审核医生、护士的注册申请，通过后可正常登录。
        </p>
      </div>
      <div class="flex flex-wrap items-center gap-2">
        <span
          v-if="pendingCount > 0"
          class="rounded-full bg-warning-50 px-2.5 py-1 text-theme-xs font-medium text-warning-700 dark:bg-warning-500/15 dark:text-warning-400"
        >
          {{ pendingCount }} 个待处理
        </span>
        <button
          type="button"
          class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
          :disabled="loading"
          @click="loadPending"
        >
          {{ loading ? '加载中…' : '刷新' }}
        </button>
      </div>
    </div>

    <p
      v-if="error"
      class="mt-4 w-fit rounded-lg border border-warning-200 bg-warning-50 px-3 py-2 text-theme-sm text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300"
    >
      {{ error }}
    </p>

    <p
      v-if="actionMessage"
      class="mt-4 w-fit rounded-lg border px-3 py-2 text-theme-sm"
      :class="actionTone === 'success' ? 'border-success-200 bg-success-50 text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300' : 'border-error-200 bg-error-50 text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300'"
    >
      {{ actionMessage }}
    </p>

    <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
      <table class="min-w-full">
        <thead>
          <tr class="border-t border-gray-100 dark:border-gray-800">
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">申请人</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">联系电话</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">申请角色</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">注册时间</th>
            <th class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="user in pendingUsers"
            :key="user.id"
            class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
          >
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
                {{ user.realName || user.username }}
              </p>
              <p class="text-theme-xs text-gray-500 dark:text-gray-400">@{{ user.username }}</p>
            </td>
            <td class="py-3 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
              {{ user.phoneNumber || '—' }}
            </td>
            <td class="py-3 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">
              {{ user.roleName || `角色 ${user.roleId ?? '?'}` }}
            </td>
            <td class="py-3 pr-4 text-theme-sm text-gray-500 dark:text-gray-400">
              {{ user.createTime || '—' }}
            </td>
            <td class="py-3 whitespace-nowrap">
              <div class="flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  class="rounded-lg bg-brand-600 px-3 py-1.5 text-theme-xs font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-60"
                  :disabled="actingId === user.id"
                  @click="approve(user.id)"
                >
                  通过
                </button>
                <button
                  type="button"
                  class="rounded-lg border border-error-200 bg-error-50 px-3 py-1.5 text-theme-xs font-medium text-error-700 shadow-theme-xs hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300 disabled:cursor-not-allowed disabled:opacity-60"
                  :disabled="actingId === user.id"
                  @click="reject(user.id)"
                >
                  拒绝
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="!loading && pendingUsers.length === 0">
            <td colspan="5" class="py-8 text-center text-theme-sm text-gray-500 dark:text-gray-400">
              暂无待审核的注册申请
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listUsers, updateUserStatus, type UserApiRecord } from '@/api/users'
import { ApiError } from '@/api/http'

const pendingUsers = ref<UserApiRecord[]>([])
const loading = ref(false)
const actingId = ref<number | null>(null)
const error = ref('')
const actionMessage = ref('')
const actionTone = ref<'success' | 'error'>('success')

const pendingCount = computed(() => pendingUsers.value.length)

const loadPending = async () => {
  loading.value = true
  error.value = ''
  try {
    const result = await listUsers({ status: 'pending', page: 1, size: 100 })
    pendingUsers.value = result.records
  } catch (err) {
    pendingUsers.value = []
    error.value =
      err instanceof ApiError
        ? err.message
        : '账号审核接口暂不可用'
  } finally {
    loading.value = false
  }
}

const decide = async (id: number, status: 'enabled' | 'disabled') => {
  actingId.value = id
  actionMessage.value = ''
  try {
    await updateUserStatus(id, status)
    pendingUsers.value = pendingUsers.value.filter((user) => user.id !== id)
    actionTone.value = 'success'
    actionMessage.value =
      status === 'enabled'
        ? '已通过该账号的注册申请'
        : '已拒绝该账号的注册申请'
  } catch (err) {
    actionTone.value = 'error'
    actionMessage.value =
      err instanceof ApiError
        ? err.message
        : '状态更新失败，请稍后重试'
  } finally {
    actingId.value = null
  }
}

const approve = (id: number) => decide(id, 'enabled')
const reject = (id: number) => decide(id, 'disabled')

onMounted(() => {
  void loadPending()
})
</script>
