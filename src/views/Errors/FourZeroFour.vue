<template>
  <main class="flex min-h-dvh flex-col items-center justify-center bg-gray-50 p-6 text-center dark:bg-gray-950">
    <div class="w-full max-w-md rounded-[18px] border border-gray-200 bg-white p-8 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
      <span
        class="mx-auto flex size-12 items-center justify-center rounded-xl bg-brand-50 text-lg font-bold text-brand-700 dark:bg-brand-500/15 dark:text-brand-300"
      >
        404
      </span>
      <h1 class="mt-6 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
        页面不存在
      </h1>
      <p class="mt-3 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
        你访问的页面暂时没有配置，请返回监护总览继续操作。
      </p>
      <router-link
        to="/"
        class="mt-6 inline-flex items-center justify-center rounded-lg bg-brand-600 px-5 py-3 text-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
      >
        返回首页总览
      </router-link>
    </div>
  </main>
</template>
