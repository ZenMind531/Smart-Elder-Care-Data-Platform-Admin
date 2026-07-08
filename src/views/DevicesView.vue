<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Device Management
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          设备管理
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          管理智能手环、睡眠监测垫、门磁传感器和紧急呼叫按钮，跟踪绑定老人、在线状态和电量。
        </p>
      </div>

      <button
        type="button"
        class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
      >
        绑定新设备
      </button>
    </div>

    <section class="grid grid-cols-1 gap-4 md:grid-cols-4 md:gap-6">
      <article
        v-for="stat in stats"
        :key="stat.label"
        class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <p class="text-theme-sm text-gray-500 dark:text-gray-400">{{ stat.label }}</p>
        <p class="mt-2 text-title-sm font-bold text-gray-900 tabular-nums dark:text-white">
          {{ stat.value }}
        </p>
        <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400">{{ stat.note }}</p>
      </article>
    </section>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              设备列表
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
              当前筛选 {{ filteredDevices.length }} 台设备
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-[180px_140px]">
            <label class="sr-only" for="device-search">搜索设备</label>
            <input
              id="device-search"
              v-model="search"
              type="search"
              placeholder="搜索设备 / 房间"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
            <label class="sr-only" for="device-status">设备状态</label>
            <select
              id="device-status"
              v-model="status"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部状态">全部状态</option>
              <option value="在线">在线</option>
              <option value="低电量">低电量</option>
              <option value="离线">离线</option>
              <option value="维护中">维护中</option>
            </select>
          </div>
        </div>

        <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
          <table class="min-w-full">
            <thead>
              <tr class="border-t border-gray-100 dark:border-gray-800">
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">设备</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">类型</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">绑定老人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">电量</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">同步</th>
                <th class="py-3 text-left text-theme-xs font-medium text-gray-500">状态</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="device in filteredDevices"
                :key="device.id"
                class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
              >
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">{{ device.name }}</p>
                  <p class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">{{ device.id }}</p>
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ device.type }}</td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="text-theme-sm text-gray-700 dark:text-gray-300">{{ device.room }} · {{ device.boundElder }}</p>
                </td>
                <td class="py-4 pr-4">
                  <div class="flex min-w-[120px] items-center gap-3">
                    <div class="h-2 flex-1 overflow-hidden rounded-full bg-gray-100 dark:bg-gray-800">
                      <div class="h-full rounded-full" :class="batteryClass(device.battery)" :style="{ width: `${device.battery}%` }"></div>
                    </div>
                    <span class="text-theme-xs text-gray-600 tabular-nums dark:text-gray-300">{{ device.battery }}%</span>
                  </div>
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-500 dark:text-gray-400">{{ device.lastSync }}</td>
                <td class="py-4 whitespace-nowrap">
                  <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[device.status]">
                    {{ device.status }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">维护提醒</h2>
          <div class="mt-5 space-y-3">
            <article
              v-for="device in attentionDevices"
              :key="device.id"
              class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="font-semibold text-gray-800 text-theme-sm dark:text-white/90">{{ device.name }}</p>
                  <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
                    {{ device.room }} · {{ device.boundElder }} · {{ device.lastSync }}
                  </p>
                </div>
                <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[device.status]">
                  {{ device.status }}
                </span>
              </div>
            </article>
          </div>
        </section>

        <section class="rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]">
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">设备巡检策略</h2>
          <ul class="mt-4 space-y-3 text-theme-sm text-gray-600 dark:text-gray-300">
            <li>低电量低于 20% 时自动生成更换电池任务。</li>
            <li>离线超过 10 分钟时通知设备管理员。</li>
            <li>维护中设备不参与健康告警计算。</li>
          </ul>
        </section>
      </aside>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useOperationsStore } from '@/stores/operations'
import type { DeviceRecord } from '@/stores/operations'

const operations = useOperationsStore()
const search = ref('')
const status = ref<DeviceRecord['status'] | '全部状态'>('全部状态')

const stats = computed(() => [
  { label: '设备总数', value: operations.devices.length, note: '已绑定设备' },
  { label: '在线设备', value: operations.onlineDevices, note: '实时同步中' },
  { label: '异常设备', value: attentionDevices.value.length, note: '需巡检或复联' },
  { label: '平均电量', value: `${Math.round(operations.devices.reduce((sum, item) => sum + item.battery, 0) / operations.devices.length)}%`, note: '低于 20% 自动提醒' },
])

const filteredDevices = computed(() => {
  const keyword = search.value.trim().toLowerCase()

  return operations.devices.filter((device) => {
    const matchesKeyword =
      !keyword ||
      device.name.toLowerCase().includes(keyword) ||
      device.room.toLowerCase().includes(keyword) ||
      device.boundElder.toLowerCase().includes(keyword)
    const matchesStatus = status.value === '全部状态' || device.status === status.value

    return matchesKeyword && matchesStatus
  })
})

const attentionDevices = computed(() =>
  operations.devices.filter((device) => device.status !== '在线'),
)

const statusClassMap: Record<DeviceRecord['status'], string> = {
  在线: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  低电量: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  离线: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  维护中: 'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
}

const batteryClass = (battery: number) => {
  if (battery <= 20) return 'bg-error-600'
  if (battery <= 45) return 'bg-warning-500'
  return 'bg-success-600'
}
</script>
