<template>
  <section
    class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          待审核账号
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          医生、护士的注册申请需要管理员审核后开通
        </p>
      </div>
      <RouterLink
        to="/settings"
        class="rounded-lg border border-gray-200 px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
      >
        去审核
      </RouterLink>
    </div>

    <div class="mt-5 space-y-3">
      <article
        v-for="user in pendingUsers"
        :key="user.id"
        class="flex items-center justify-between gap-3 rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
      >
        <div class="min-w-0">
          <p class="truncate text-theme-sm font-semibold text-gray-800 dark:text-white/90">
            {{ user.realName || user.username }}
          </p>
          <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
            申请角色：{{ user.roleName || `角色 ${user.roleId ?? '?'}` }} · 提交于 {{ user.createTime || '—' }}
          </p>
        </div>
        <span
          class="shrink-0 rounded-full bg-warning-50 px-2 py-0.5 text-theme-xs font-medium text-warning-700 dark:bg-warning-500/15 dark:text-warning-400"
        >
          待审核
        </span>
      </article>
      <p
        v-if="!loading && pendingUsers.length === 0"
        class="rounded-xl border border-dashed border-gray-300 p-6 text-center text-theme-sm text-gray-500 dark:border-gray-700 dark:text-gray-400"
      >
        暂无待审核申请
      </p>
      <p
        v-if="error"
        class="rounded-lg border border-warning-200 bg-warning-50 px-3 py-2 text-theme-sm text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300"
      >
        {{ error }}
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { listUsers, type UserApiRecord } from '@/api/users'
import { ApiError } from '@/api/http'

const pendingUsers = ref<UserApiRecord[]>([])
const loading = ref(false)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const result = await listUsers({ status: 'pending', page: 1, size: 5 })
    pendingUsers.value = result.records
  } catch (err) {
    pendingUsers.value = []
    error.value = err instanceof ApiError ? err.message : '账号审核接口暂不可用'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void load()
})
</script>
