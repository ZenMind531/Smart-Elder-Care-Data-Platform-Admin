<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
        System Settings
      </span>
      <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
        系统设置
      </h1>
      <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
        配置机构信息、角色权限、通知规则和基础字典，为后续后台管理和个人用户端打基础。
      </p>
    </div>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-5">
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">机构资料</h2>
        <div class="mt-5 grid gap-4">
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">机构名称</span>
            <input
              v-model="organization.name"
              type="text"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">联系电话</span>
            <input
              v-model="organization.phone"
              type="text"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">机构地址</span>
            <textarea
              v-model="organization.address"
              rows="3"
              class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            ></textarea>
          </label>
          <button type="button" class="w-fit rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700">
            保存机构资料
          </button>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-7">
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">角色权限</h2>
        <div class="mt-5 space-y-3">
          <article
            v-for="role in operations.roles"
            :key="role.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p class="font-semibold text-theme-sm text-gray-800 dark:text-white/90">{{ role.name }}</p>
                <p class="mt-1 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
                  {{ role.scope }}
                </p>
              </div>
              <div class="flex items-center gap-3">
                <span class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">{{ role.users }} 人</span>
                <span
                  class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                  :class="role.enabled ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400' : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300'"
                >
                  {{ role.enabled ? '启用' : '停用' }}
                </span>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-7">
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">通知规则</h2>
        <div class="mt-5 space-y-3">
          <article
            v-for="rule in operations.notificationRules"
            :key="rule.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p class="font-semibold text-theme-sm text-gray-800 dark:text-white/90">{{ rule.name }}</p>
                <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
                  {{ rule.channel }} · {{ rule.target }}
                </p>
              </div>
              <span
                class="w-fit rounded-full px-2 py-0.5 text-theme-xs font-medium"
                :class="rule.enabled ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400' : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300'"
              >
                {{ rule.enabled ? '启用' : '停用' }}
              </span>
            </div>
          </article>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08] xl:col-span-5">
        <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">基础字典</h2>
        <div class="mt-5 grid gap-3">
          <div v-for="dict in dictionaries" :key="dict.name" class="rounded-xl bg-white p-4 dark:bg-white/[0.03]">
            <div class="flex items-center justify-between gap-3">
              <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ dict.name }}</p>
              <span class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">{{ dict.count }} 项</span>
            </div>
            <p class="mt-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">{{ dict.desc }}</p>
          </div>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useOperationsStore } from '@/stores/operations'

const operations = useOperationsStore()

const organization = reactive({
  name: '青松智慧养老服务中心',
  phone: '0571-8888 6620',
  address: '杭州市西湖区文三路 188 号',
})

const dictionaries = [
  { name: '照护等级', count: 4, desc: '一级照护、二级照护、三级照护、特级照护。' },
  { name: '设备类型', count: 4, desc: '智能手环、睡眠监测垫、门磁传感器、紧急呼叫按钮。' },
  { name: '告警等级', count: 3, desc: '紧急、关注、普通，对应不同通知策略。' },
  { name: '服务项目', count: 4, desc: '健康咨询、康复训练、陪诊服务、上门护理。' },
]
</script>
