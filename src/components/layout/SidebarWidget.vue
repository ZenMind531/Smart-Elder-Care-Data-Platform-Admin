<template>
  <div
    class="mx-auto mb-10 mt-auto w-full max-w-60 rounded-xl border border-brand-100 bg-brand-25 px-4 py-5 dark:border-brand-900 dark:bg-brand-500/[0.08]"
  >
    <div class="flex items-center gap-3">
      <span class="flex size-10 items-center justify-center rounded-xl bg-brand-600 text-white">
        职
      </span>
      <div class="min-w-0">
        <h3 class="truncate font-semibold text-gray-900 dark:text-white">{{ roleOption.dutyTitle }}</h3>
        <p class="truncate text-theme-xs text-gray-500 dark:text-gray-400">{{ roleOption.name }}</p>
      </div>
    </div>
    <p class="mt-4 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">
      {{ roleOption.dutyDetail }}
    </p>
    <router-link
      :to="roleOption.dutyPath"
      class="mt-4 flex items-center justify-center rounded-lg bg-brand-600 p-3 text-theme-sm font-medium text-white hover:bg-brand-700"
    >
      {{ roleOption.dutyAction }}
    </router-link>
  </div>
</template>

<script setup lang="ts">
import { getStoredUser } from '@/api/http'
import { getRoleOption } from '@/config/roles'

const roleOption = getRoleOption(getStoredUser()?.roleName)
</script>
