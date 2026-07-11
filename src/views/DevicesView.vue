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

      <div class="flex flex-wrap items-center gap-2">
        <button
          v-if="canUpdateDevices"
          type="button"
          class="inline-flex items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
          @click="openAddModal"
        >
          添加设备
        </button>
        <button
          v-if="canUpdateDevices"
          type="button"
          class="inline-flex w-fit items-center justify-center rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
          @click="recoverAllAttentionDevices"
        >
          一键复联异常设备
        </button>
      </div>
    </div>

    <p
      v-if="feedback"
      class="mb-6 w-fit rounded-full bg-success-50 px-3 py-1 text-theme-xs font-medium text-success-700 dark:bg-success-500/15 dark:text-success-400"
    >
      {{ feedback }}
    </p>

    <p
      v-if="operations.loading || operations.error"
      class="mb-6 w-fit rounded-lg border px-3 py-2 text-theme-sm"
      :class="operations.error ? 'border-warning-200 bg-warning-50 text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300' : 'border-brand-200 bg-brand-50 text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300'"
    >
      {{ operations.loading ? '正在同步设备接口...' : `<span>设备接口暂不可用：</span>${operations.error}` }}
    </p>

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
                  <div class="flex flex-wrap items-center gap-2">
                    <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[device.status]">
                      {{ device.status }}
                    </span>
                    <button
                      v-if="device.boundElder === '未绑定' && canUpdateDevices"
                      type="button"
                      class="rounded-lg bg-brand-600 px-3 py-1.5 text-theme-xs font-medium text-white shadow-theme-xs hover:bg-brand-700"
                      @click="openBindModal(device)"
                    >
                      绑定老人
                    </button>
                    <button
                      v-if="device.boundElder !== '未绑定' && canUpdateDevices"
                      type="button"
                      class="rounded-lg border border-error-200 bg-error-50 px-3 py-1.5 text-theme-xs font-medium text-error-700 hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
                      @click="handleUnbind(device)"
                    >
                      解绑
                    </button>
                    <button
                      v-if="device.status !== '在线' && canUpdateDevices"
                      type="button"
                      class="rounded-lg border border-gray-300 bg-white px-3 py-2 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
                      @click="recoverDevice(device.id)"
                    >
                      {{ device.status === '低电量' ? '更换电池' : '恢复在线' }}
                    </button>
                  </div>
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
                <button
                  v-if="canUpdateDevices"
                  type="button"
                  class="rounded-lg bg-brand-600 px-3 py-2 text-theme-xs font-medium text-white shadow-theme-xs hover:bg-brand-700"
                  @click="recoverDevice(device.id)"
                >
                  处理
                </button>
              </div>
            </article>
          </div>
        </section>

        <section class="rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]">
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">设备巡检策略</h2>
          <ul class="mt-4 space-y-3 text-theme-sm text-gray-600 dark:text-gray-300">
            <li>低电量低于 20% 时自动生成更换电池任务。</li>
            <li>离线超过 10 分钟时通知系统管理员。</li>
            <li>维护中设备不参与健康告警计算。</li>
          </ul>
        </section>
      </aside>
    </div>

    <!-- 绑定弹窗 -->
    <div
      v-if="bindModalOpen"
      class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4"
      @click.self="bindModalOpen = false"
    >
      <div class="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">绑定设备到老人</h3>
        <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
          为「{{ bindTarget?.name }}」选择要绑定的老人。
        </p>
        <div class="mt-4 space-y-3">
          <label class="sr-only" for="bind-elderly">选择老人</label>
          <select
            id="bind-elderly"
            v-model="bindElderlyId"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option :value="null" disabled>请选择老人</option>
            <option v-for="elder in elderlyOptions" :key="elder.id" :value="elder.id">
              {{ elder.room || '—' }} · {{ elder.name }}
            </option>
          </select>
          <p v-if="bindError" class="text-theme-xs text-error-600 dark:text-error-400">{{ bindError }}</p>
          <div class="flex flex-wrap items-center justify-end gap-2">
            <button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="bindModalOpen = false">取消</button>
            <button type="button" :disabled="!bindElderlyId || bindSubmitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:opacity-60" @click="confirmBind">
              {{ bindSubmitting ? '绑定中…' : '确认绑定' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加设备弹窗 -->
    <div
      v-if="addModalOpen"
      class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4"
      @click.self="addModalOpen = false"
    >
      <div class="w-full max-w-md rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">添加设备</h3>
        <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">录入新设备信息，创建后可在列表中绑定老人。</p>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="confirmAdd">
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">设备编号</span>
            <input v-model="addForm.deviceCode" type="text" required placeholder="例如 DEV001" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">设备名称</span>
            <input v-model="addForm.deviceName" type="text" required placeholder="例如 智能手环A302" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">设备类型</span>
            <select v-model="addForm.deviceType" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option value="watch">智能手环</option>
              <option value="bp_meter">血压计</option>
              <option value="glucometer">血糖仪</option>
            </select>
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">状态</span>
            <select v-model="addForm.status" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option value="normal">正常</option>
              <option value="abnormal">异常</option>
              <option value="disabled">停用</option>
            </select>
          </label>
          <label class="block sm:col-span-2">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">备注</span>
            <input v-model="addForm.remark" type="text" placeholder="例如 用于采集心率数据" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <p v-if="addError" class="rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300 sm:col-span-2">{{ addError }}</p>
          <div class="flex flex-wrap items-center justify-end gap-2 sm:col-span-2">
            <button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="addModalOpen = false">取消</button>
            <button type="submit" :disabled="addSubmitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:opacity-60">{{ addSubmitting ? '创建中…' : '创建设备' }}</button>
          </div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser, ApiError } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import { useElderlyStore } from '@/stores/elderly'
import { bindDevice, unbindDevice, createDevice } from '@/api/devices'
import type { DeviceRecord } from '@/stores/operations'

const operations = useOperationsStore()
const elderlyStore = useElderlyStore()
const currentUser = getStoredUser()
const canUpdateDevices = canUseAction(currentUser?.roleName, 'devices:update')
const search = ref('')
const status = ref<DeviceRecord['status'] | '全部状态'>('全部状态')
const feedback = ref('')

// 绑定弹窗
const bindModalOpen = ref(false)
const bindTarget = ref<DeviceRecord | null>(null)
const bindElderlyId = ref<number | null>(null)
const bindSubmitting = ref(false)
const bindError = ref('')

onMounted(() => {
  void operations.fetchDevices()
  if (elderlyStore.records.length === 0) void elderlyStore.fetchRecords()
})

const elderlyOptions = computed(() =>
  elderlyStore.records.map((r) => ({ id: r.id, name: r.name, room: r.room })),
)

const openBindModal = (device: DeviceRecord) => {
  bindTarget.value = device
  bindElderlyId.value = null
  bindError.value = ''
  bindModalOpen.value = true
}

const confirmBind = async () => {
  if (!bindTarget.value || !bindElderlyId.value) return
  bindSubmitting.value = true
  bindError.value = ''
  try {
    await bindDevice(bindTarget.value.apiId || bindTarget.value.id, bindElderlyId.value)
    bindTarget.value.boundElder = elderlyOptions.value.find((e) => e.id === bindElderlyId.value)?.name || '已绑定'
    feedback.value = `已绑定设备：${bindTarget.value.name}`
    bindModalOpen.value = false
  } catch (err) {
    bindError.value = err instanceof ApiError ? err.message : '绑定失败，请稍后重试'
  } finally {
    bindSubmitting.value = false
  }
}

const handleUnbind = async (device: DeviceRecord) => {
  if (!window.confirm(`确定要解绑「${device.name}」与「${device.boundElder}」的绑定吗？`)) return
  try {
    await unbindDevice(device.apiId || device.id)
    device.boundElder = '未绑定'
    device.room = '—'
    feedback.value = `已解绑设备：${device.name}`
  } catch (err) {
    feedback.value = err instanceof ApiError ? err.message : '解绑失败，请稍后重试'
  }
}

// 添加设备
const addModalOpen = ref(false)
const addSubmitting = ref(false)
const addError = ref('')
const addForm = reactive({
  deviceCode: '',
  deviceName: '',
  deviceType: 'watch' as 'watch' | 'bp_meter' | 'glucometer',
  status: 'normal' as 'normal' | 'abnormal' | 'disabled',
  remark: '',
})

const openAddModal = () => {
  addForm.deviceCode = ''
  addForm.deviceName = ''
  addForm.deviceType = 'watch'
  addForm.status = 'normal'
  addForm.remark = ''
  addError.value = ''
  addModalOpen.value = true
}

const confirmAdd = async () => {
  if (!addForm.deviceCode.trim() || !addForm.deviceName.trim()) {
    addError.value = '请填写设备编号和名称'
    return
  }
  addSubmitting.value = true
  addError.value = ''
  try {
    await createDevice({
      deviceCode: addForm.deviceCode.trim(),
      deviceName: addForm.deviceName.trim(),
      deviceType: addForm.deviceType,
      elderlyId: 0,
      status: addForm.status,
      remark: addForm.remark.trim() || undefined,
    } as any)
    feedback.value = `已创建设备：${addForm.deviceName}`
    addModalOpen.value = false
    await operations.fetchDevices()
  } catch (err) {
    addError.value = err instanceof ApiError ? err.message : '创建失败，请稍后重试'
  } finally {
    addSubmitting.value = false
  }
}

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

const recoverDevice = (id: string) => {
  if (!canUpdateDevices) {
    feedback.value = '当前角色没有处理设备状态权限'
    return
  }

  operations.recoverDevice(id)
  feedback.value = '设备已恢复在线并刷新同步时间'
}

const recoverAllAttentionDevices = () => {
  if (!canUpdateDevices) {
    feedback.value = '当前角色没有批量复联设备权限'
    return
  }

  attentionDevices.value.forEach((device) => operations.recoverDevice(device.id))
  status.value = '全部状态'
  feedback.value = '异常设备已批量恢复在线'
}
</script>
