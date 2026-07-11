<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Care Records
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          护理记录
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          记录巡房、服药、康复训练、设备巡检和异常处置过程，形成可追溯的护理服务台账。
        </p>
      </div>

      <button
        v-if="canCreateCareRecord"
        type="button"
        class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
        @click="addCareRecord"
      >
        新增护理记录
      </button>
    </div>

    <p
      v-if="feedback"
      class="mb-4 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
    >
      {{ feedback }}
    </p>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">护理台账</h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">今日已完成 {{ operations.todayCareDone }} 条</p>
          </div>
          <label class="sr-only" for="care-status">护理状态</label>
          <select
            id="care-status"
            v-model="status"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 sm:w-[150px]"
          >
            <option value="全部状态">全部状态</option>
            <option value="已完成">已完成</option>
            <option value="进行中">进行中</option>
            <option value="待补录">待补录</option>
          </select>
        </div>

        <div class="mt-5 space-y-3">
          <article
            v-for="record in filteredRecords"
            :key="record.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
              <div class="min-w-0">
                <div class="flex flex-wrap items-center gap-2">
                  <span class="rounded-full bg-brand-50 px-2 py-0.5 text-theme-xs font-medium text-brand-700 dark:bg-brand-500/15 dark:text-brand-300">
                    {{ record.type }}
                  </span>
                  <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ record.time }}</span>
                </div>
                <h3 class="mt-2 font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                  {{ record.room }} {{ record.elderName }}
                </h3>
                <p class="mt-1 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">
                  {{ record.content }}
                </p>
                <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400">记录人：{{ record.caregiver }}</p>
              </div>
              <span class="w-fit rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[record.status]">
                {{ record.status }}
              </span>
            </div>
          </article>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">今日护理概览</h2>
          <div class="mt-5 grid grid-cols-2 gap-3">
            <div v-for="item in summary" :key="item.label" class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
              <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ item.label }}</p>
              <p class="mt-2 text-lg font-semibold text-gray-900 tabular-nums dark:text-white">{{ item.value }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]">
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">交班提醒</h2>
          <ul class="mt-4 space-y-3 text-theme-sm text-gray-600 dark:text-gray-300">
            <li>B-118 服药后需要复测血压。</li>
            <li>C-405 手环离线处置结果待补录。</li>
            <li>晚间巡房前确认 A 区夜间离床规则。</li>
          </ul>
        </section>
      </aside>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import type { CareRecord } from '@/stores/operations'

const operations = useOperationsStore()
const currentUser = getStoredUser()
const canCreateCareRecord = canUseAction(currentUser?.roleName, 'care:create')
const status = ref<CareRecord['status'] | '全部状态'>('全部状态')
const feedback = ref('')

const filteredRecords = computed(() =>
  operations.careRecords.filter((record) => status.value === '全部状态' || record.status === status.value),
)

const summary = computed(() => [
  { label: '总记录', value: operations.careRecords.length },
  { label: '已完成', value: operations.todayCareDone },
  { label: '进行中', value: operations.careRecords.filter((item) => item.status === '进行中').length },
  { label: '待补录', value: operations.careRecords.filter((item) => item.status === '待补录').length },
])

const statusClassMap: Record<CareRecord['status'], string> = {
  已完成: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  进行中: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  待补录: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
}

const addCareRecord = () => {
  if (!canCreateCareRecord) {
    feedback.value = '当前角色没有新增护理记录权限'
    return
  }

  operations.addCareRecord()
  status.value = '全部状态'
  feedback.value = '已新增一条临时巡房护理记录'
}
</script>
