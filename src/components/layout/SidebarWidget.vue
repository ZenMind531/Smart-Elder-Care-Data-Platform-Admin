<template>
  <div
    class="mx-auto mb-10 mt-auto w-full max-w-60 rounded-2xl border border-brand-100 bg-brand-25 px-4 py-5 dark:border-brand-900 dark:bg-brand-500/[0.08]"
  >
    <div class="flex items-center gap-3">
      <span class="flex size-10 items-center justify-center rounded-xl bg-brand-600 text-white">
        值
      </span>
      <div>
        <h3 class="font-semibold text-gray-900 dark:text-white">今日值班</h3>
        <p class="text-theme-xs text-gray-500 dark:text-gray-400">A 区护理组</p>
      </div>
    </div>
    <div class="mt-4 space-y-2 text-theme-sm text-gray-600 dark:text-gray-300">
      <p>负责人：刘护士</p>
      <p>巡房频次：每 2 小时</p>
    </div>
    <router-link
      to="/care-records"
      class="mt-4 flex items-center justify-center rounded-lg bg-brand-600 p-3 text-theme-sm font-medium text-white hover:bg-brand-700"
    >
      查看护理记录
    </router-link>
  </div>
</template>
