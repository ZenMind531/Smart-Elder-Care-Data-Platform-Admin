<template>
  <section
    class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div>
      <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
        角色与机构概览
      </h2>
      <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
        平台账号角色分布，便于评估人手和启用范围
      </p>
    </div>

    <div class="mt-5 space-y-3">
      <article
        v-for="role in operations.roles"
        :key="role.id"
        class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
      >
        <div class="flex items-center justify-between gap-3">
          <div>
            <p class="font-semibold text-theme-sm text-gray-800 dark:text-white/90">
              {{ role.name }}
            </p>
            <p class="mt-1 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
              {{ role.scope }}
            </p>
          </div>
          <span
            class="rounded-full px-2 py-0.5 text-theme-xs font-medium tabular-nums"
            :class="role.enabled ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400' : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300'"
          >
            {{ role.users }} 人
          </span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useOperationsStore } from '@/stores/operations'

const operations = useOperationsStore()

onMounted(() => {
  void operations.fetchRoles()
})
</script>
