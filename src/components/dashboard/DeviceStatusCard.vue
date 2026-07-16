<template>
  <section
    class="h-full rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div>
      <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
        设备状态分布
      </h2>
      <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
        设备接入和告警情况
      </p>
    </div>

    <div class="mt-6 space-y-5">
      <div v-if="loading" class="text-theme-sm text-gray-500 dark:text-gray-400">
        正在同步设备数据...
      </div>
      <div v-else-if="devices.length === 0" class="rounded-xl border border-dashed border-gray-300 p-6 text-center text-theme-sm text-gray-500 dark:border-gray-700 dark:text-gray-400">
        暂无设备数据
      </div>
      <div v-for="device in devices" v-else :key="device.id">
        <div class="mb-2 flex items-center justify-between gap-4">
          <div>
            <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
              {{ device.name }}
            </p>
            <p class="mt-0.5 text-theme-xs text-gray-500 dark:text-gray-400">
              {{ device.status }} · {{ device.room }}
            </p>
          </div>
          <span class="text-theme-sm font-semibold text-gray-800 tabular-nums dark:text-white/90">
            {{ device.battery }}%
          </span>
        </div>
        <div class="h-2 overflow-hidden rounded-full bg-gray-100 dark:bg-gray-800">
          <div
            class="h-full rounded-full"
            :class="barClass(device.battery)"
            :style="{ width: `${device.battery}%` }"
          ></div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useOperationsStore } from '@/stores/operations'

const operations = useOperationsStore()

onMounted(() => {
  if (operations.devices.length === 0) void operations.fetchDevices()
})

const loading = computed(() => operations.loading && operations.devices.length === 0)

const devices = computed(() => operations.devices.slice(0, 4))

const barClass = (battery: number) => {
  if (battery <= 20) return 'bg-error-600'
  if (battery <= 45) return 'bg-warning-500'
  return 'bg-success-600'
}
</script>
