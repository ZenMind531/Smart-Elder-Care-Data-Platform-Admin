<template>
  <AdminLayout>
    <section
      class="rounded-2xl border border-gray-200 bg-white p-6 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] lg:p-8"
    >
      <div class="max-w-3xl">
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          功能模块
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          {{ title }}
        </h1>
        <p class="mt-3 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          {{ subtitle }}
        </p>
      </div>

      <div class="mt-8 grid gap-4 md:grid-cols-3">
        <div
          v-for="item in cards"
          :key="item.title"
          class="rounded-xl border border-gray-100 bg-gray-50 p-5 dark:border-gray-800 dark:bg-gray-900"
        >
          <p class="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
            {{ item.title }}
          </p>
          <p class="mt-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            {{ item.description }}
          </p>
        </div>
      </div>

      <RouterLink
        to="/"
        class="mt-8 inline-flex rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
      >
        返回首页总览
      </RouterLink>
    </section>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'

const route = useRoute()

const title = computed(() => String(route.meta.title || '功能建设中'))
const subtitle = computed(() =>
  String(route.meta.subtitle || '该模块已建立路由入口，后续可以继续补充列表、详情、表单和筛选功能。'),
)

const cards = [
  {
    title: '列表管理',
    description: '后续可放筛选、搜索、分页和批量操作。',
  },
  {
    title: '详情查看',
    description: '用于查看单个老人、设备、告警或护理记录的完整信息。',
  },
  {
    title: '新增编辑',
    description: '用于新增资料、更新状态、记录处理结果和保存业务数据。',
  },
]
</script>
