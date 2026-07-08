<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Service Appointments
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          服务预约
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          处理家属和老人提交的健康咨询、康复训练、陪诊服务和上门护理预约，跟踪排班与服务进度。
        </p>
      </div>

      <button type="button" class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700">
        新建预约
      </button>
    </div>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">预约列表</h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">待确认 {{ operations.pendingServices }} 条</p>
          </div>
          <label class="sr-only" for="service-status">预约状态</label>
          <select
            id="service-status"
            v-model="status"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 sm:w-[150px]"
          >
            <option value="全部状态">全部状态</option>
            <option value="待确认">待确认</option>
            <option value="已预约">已预约</option>
            <option value="服务中">服务中</option>
            <option value="已完成">已完成</option>
          </select>
        </div>

        <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
          <table class="min-w-full">
            <thead>
              <tr class="border-t border-gray-100 dark:border-gray-800">
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">服务</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">预约时间</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">申请人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">负责人</th>
                <th class="py-3 text-left text-theme-xs font-medium text-gray-500">状态</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="service in filteredServices"
                :key="service.id"
                class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
              >
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">{{ service.room }} {{ service.elderName }}</p>
                  <p class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">#{{ service.id }}</p>
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ service.service }}</td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ service.scheduledAt }}</td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ service.requester }}</td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ service.assignee }}</td>
                <td class="py-4 whitespace-nowrap">
                  <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[service.status]">
                    {{ service.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">服务目录</h2>
          <div class="mt-5 grid gap-3">
            <div v-for="item in catalog" :key="item.name" class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
              <div class="flex items-center justify-between gap-3">
                <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ item.name }}</p>
                <span class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">{{ item.count }} 单</span>
              </div>
              <p class="mt-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">{{ item.desc }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]">
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">家属端联动</h2>
          <p class="mt-3 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">
            后期个人用户端可以复用这些预约数据，让家属查看进度、取消预约或补充服务备注。
          </p>
        </section>
      </aside>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useOperationsStore } from '@/stores/operations'
import type { ServiceAppointment } from '@/stores/operations'

const operations = useOperationsStore()
const status = ref<ServiceAppointment['status'] | '全部状态'>('全部状态')

const filteredServices = computed(() =>
  operations.serviceAppointments.filter(
    (service) => status.value === '全部状态' || service.status === status.value,
  ),
)

const catalog = computed(() => [
  { name: '健康咨询', count: operations.serviceAppointments.filter((item) => item.service === '健康咨询').length, desc: '医生线上或线下评估近期健康状态。' },
  { name: '康复训练', count: operations.serviceAppointments.filter((item) => item.service === '康复训练').length, desc: '康复师按计划安排训练项目。' },
  { name: '陪诊服务', count: operations.serviceAppointments.filter((item) => item.service === '陪诊服务').length, desc: '协助老人外出就医和结果记录。' },
  { name: '上门护理', count: operations.serviceAppointments.filter((item) => item.service === '上门护理').length, desc: '护理员按预约提供上门护理。' },
])

const statusClassMap: Record<ServiceAppointment['status'], string> = {
  待确认: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  已预约: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  服务中: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  已完成: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
}
</script>
