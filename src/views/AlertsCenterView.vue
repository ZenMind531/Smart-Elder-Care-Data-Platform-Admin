<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span
          class="text-theme-sm font-semibold uppercase tracking-wider text-brand-600 dark:text-brand-400"
          >Alert Center</span
        >
        <h1 class="mt-2 text-2xl font-bold text-gray-900 dark:text-white">告警中心</h1>
      </div>
      <button
        v-if="canCreateAlert"
        type="button"
        class="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-5 py-3 text-sm font-semibold text-white shadow-sm hover:bg-brand-700 transition-colors"
        @click="showCreateForm = !showCreateForm"
      >
        <Plus class="size-4" /> {{ showCreateForm ? '收起新增' : '新增人工告警' }}
      </button>
    </div>

    <p
      v-if="feedback"
      class="mb-5 w-fit rounded-xl bg-green-50 px-4 py-2 text-sm font-medium text-green-700 dark:bg-green-500/15 dark:text-green-400"
    >
      {{ feedback }}
    </p>

    <p
      v-if="operations.loading || operations.error"
      class="mb-5 w-fit rounded-xl border px-4 py-2 text-sm font-medium"
      :class="
        operations.error
          ? 'border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-500/30 dark:bg-amber-500/10 dark:text-amber-400'
          : 'border-blue-200 bg-blue-50 text-blue-700 dark:border-blue-500/30 dark:bg-blue-500/10 dark:text-blue-400'
      "
    >
      {{ operations.loading ? '正在同步告警数据…' : `服务器错误：${operations.error}` }}
    </p>

    <section
      v-if="showCreateForm && canCreateAlert"
      class="mb-6 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
    >
      <div class="flex items-start justify-between gap-4">
        <div>
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">
            新增健康预警
          </h2>
          <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            补录人工发现的异常体征，提交后进入待处理队列。
          </p>
        </div>
        <button
          type="button"
          class="rounded-lg border border-gray-200 p-2 text-gray-500 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-white/[0.03]"
          aria-label="关闭新增告警表单"
          @click="showCreateForm = false"
        >
          <X class="size-4" />
        </button>
      </div>

      <form class="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-4" @submit.prevent="addManualAlert">
        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300"
            >老人 ID</span
          >
          <input
            v-model.number="createForm.elderlyId"
            type="number"
            min="1"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>
        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300"
            >老人姓名</span
          >
          <input
            v-model.trim="createForm.elderName"
            type="text"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>
        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300"
            >房间</span
          >
          <input
            v-model.trim="createForm.room"
            type="text"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>
        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300"
            >预警等级</span
          >
          <select
            v-model="createForm.warningLevel"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option v-for="option in warningLevelOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </label>
        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300"
            >预警类型</span
          >
          <select
            v-model="createForm.warningType"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option v-for="option in warningTypeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </label>
        <label class="block md:col-span-2 xl:col-span-3">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300"
            >预警内容</span
          >
          <input
            v-model.trim="createForm.warningContent"
            type="text"
            required
            placeholder="例如：血压超过正常范围，已通知护理员复测"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>
        <div class="flex flex-wrap items-center gap-3 md:col-span-2 xl:col-span-4">
          <button
            type="submit"
            class="inline-flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
          >
            <Plus class="size-4" />
            保存告警
          </button>
          <button
            type="button"
            class="rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
            @click="resetCreateForm"
          >
            重置
          </button>
        </div>
      </form>
    </section>

    <div class="grid grid-cols-2 gap-3 sm:grid-cols-4 lg:gap-4">
      <div
        v-for="s in stats"
        :key="s.label"
        class="rounded-2xl border border-gray-200 bg-white p-4 dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <p class="text-theme-xs font-medium uppercase tracking-wider text-gray-400">
          {{ s.label }}
        </p>
        <p class="mt-2 text-3xl font-bold tabular-nums" :class="s.color">{{ s.value }}</p>
        <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">{{ s.note }}</p>
      </div>
    </div>

    <div class="mt-6">
      <div class="flex flex-wrap items-center gap-3">
        <div class="relative flex-1 min-w-[200px]">
          <Search
            class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-gray-400"
          />
          <input
            v-model="keyword"
            type="search"
            placeholder="搜索老人姓名 / 房间号 / 来源…"
            class="h-11 w-full rounded-xl border border-gray-200 bg-white py-2.5 pl-10 pr-4 text-sm shadow-sm placeholder:text-gray-400 focus:border-brand-400 focus:ring-2 focus:ring-brand-500/15 focus:outline-none dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          />
        </div>
        <select
          v-model="level"
          class="h-11 rounded-xl border border-gray-200 bg-white px-4 text-sm shadow-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
        >
          <option value="全部等级">全部等级</option>
          <option value="紧急">紧急</option>
          <option value="关注">关注</option>
          <option value="普通">普通</option>
        </select>
        <select
          v-model="status"
          class="h-11 rounded-xl border border-gray-200 bg-white px-4 text-sm shadow-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
        >
          <option value="全部状态">全部状态</option>
          <option value="待处理">待处理</option>
          <option value="处理中">处理中</option>
          <option value="已处理">已处理</option>
        </select>
      </div>

      <div class="mt-4 flex flex-wrap gap-2">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          type="button"
          class="rounded-xl border px-4 py-2 text-sm font-medium transition-all"
          :class="
            status === tab.value
              ? 'border-transparent bg-brand-600 text-white shadow-md'
              : 'border-gray-200 bg-white text-gray-600 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300'
          "
          @click="status = tab.value"
        >
          {{ tab.label }} <span class="ml-1.5 tabular-nums opacity-75">{{ tab.count }}</span>
        </button>
      </div>
    </div>

    <div class="mt-5 flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
      <span>共 {{ filteredAlerts.length }} 条告警</span>
      <button
        v-if="keyword || level !== '全部等级' || status !== '全部状态'"
        type="button"
        class="text-brand-600 hover:underline dark:text-brand-400"
        @click="resetFilters"
      >
        清空筛选
      </button>
    </div>

    <div class="mt-3 grid gap-3">
      <article
        v-for="alert in filteredAlerts"
        :key="alert.id"
        class="group relative overflow-hidden rounded-2xl border border-gray-200 bg-white p-5 transition-all hover:shadow-lg hover:-translate-y-0.5 dark:border-gray-800 dark:bg-white/[0.03]"
        :class="
          'border-l-[5px] ' +
          (alert.level === '紧急'
            ? 'border-l-red-500'
            : alert.level === '关注'
              ? 'border-l-amber-500'
              : 'border-l-blue-500')
        "
      >
        <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
          <div class="min-w-0 flex-1">
            <div class="flex flex-wrap items-center gap-2">
              <span
                class="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                :class="levelBadge(alert.level)"
                >{{ alert.level }}</span
              >
              <span
                class="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                :class="statusBadge(alert.status)"
                >{{ alert.status }}</span
              >
              <span
                v-if="alert.escalation"
                class="rounded-full bg-orange-50 px-2.5 py-0.5 text-xs font-semibold text-orange-700 dark:bg-orange-500/15 dark:text-orange-400"
                >已升级</span
              >
              <span class="ml-auto text-xs text-gray-400 tabular-nums dark:text-gray-500">{{
                alert.time
              }}</span>
            </div>
            <h3 class="mt-2 text-base font-bold text-gray-900 dark:text-white">
              {{ alert.room }} · {{ alert.elderName }}
              <span class="ml-2 font-normal text-gray-500 dark:text-gray-400">{{
                alert.type
              }}</span>
            </h3>
            <p class="mt-1.5 line-clamp-2 text-sm leading-relaxed text-gray-500 dark:text-gray-400">
              {{ alert.content || `${alert.source} 触发，责任人：${alert.owner}` }}
            </p>
            <div
              class="mt-3 flex flex-wrap items-center gap-3 text-xs text-gray-400 dark:text-gray-500"
            >
              <span>{{ alert.source }}</span>
              <span class="size-1 rounded-full bg-gray-300 dark:bg-gray-600"></span>
              <span>{{ alert.owner }}</span>
              <span class="size-1 rounded-full bg-gray-300 dark:bg-gray-600"></span>
              <span>{{ slaText(alert) }}</span>
            </div>
          </div>
          <div class="flex shrink-0 flex-wrap gap-2">
            <RouterLink
              :to="`/alerts/${alert.id}`"
              class="inline-flex items-center gap-1.5 rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm font-semibold text-gray-700 shadow-sm transition-all hover:bg-gray-50 hover:border-gray-300 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-gray-800 group-hover:border-brand-300 group-hover:text-brand-700 dark:group-hover:border-brand-600 dark:group-hover:text-brand-400"
            >
              详情/处置
              <svg class="size-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 7l5 5m0 0l-5 5m5-5H6"
                />
              </svg>
            </RouterLink>
            <button
              v-if="canDeleteAlert"
              type="button"
              class="inline-flex items-center gap-1.5 rounded-xl border border-error-200 bg-error-50 px-4 py-2.5 text-sm font-semibold text-error-700 shadow-sm transition-all hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
              @click="confirmDeleteAlert(alert)"
            >
              <Trash2 class="size-4" />
              删除
            </button>
          </div>
        </div>
      </article>

      <div
        v-if="filteredAlerts.length === 0 && !operations.loading"
        class="rounded-2xl border border-dashed border-gray-300 py-16 text-center dark:border-gray-700"
      >
        <Search class="mx-auto size-10 text-gray-300 dark:text-gray-600" />
        <p class="mt-4 text-base font-semibold text-gray-800 dark:text-white/90">没有匹配的告警</p>
        <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">尝试调整筛选条件或清空筛选</p>
        <button
          type="button"
          class="mt-4 rounded-xl bg-brand-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-brand-700 transition-colors"
          @click="resetFilters"
        >
          清空筛选
        </button>
      </div>
    </div>

    <div
      v-if="deletingAlert"
      class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/45 p-4"
      role="alertdialog"
      aria-modal="true"
      aria-labelledby="delete-alert-title"
    >
      <section
        class="w-full max-w-md rounded-xl border border-gray-200 bg-white p-5 shadow-lg dark:border-gray-800 dark:bg-gray-900"
      >
        <h2
          id="delete-alert-title"
          class="text-lg font-semibold text-gray-900 text-balance dark:text-white"
        >
          删除健康预警
        </h2>
        <p class="mt-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          将删除 {{ deletingAlert.room }}
          {{ deletingAlert.elderName }} 的这条预警记录，删除后会同步后端。
        </p>
        <div class="mt-5 flex justify-end gap-3">
          <button
            type="button"
            class="rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
            @click="deletingAlert = null"
          >
            取消
          </button>
          <button
            type="button"
            class="rounded-lg bg-error-600 px-4 py-2.5 text-theme-sm font-medium text-white hover:bg-error-700"
            @click="deleteAlert"
          >
            确认删除
          </button>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Plus, Search, Trash2, X } from 'lucide-vue-next'
import { RouterLink } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import type { AlertRecord, ManualAlertInput } from '@/stores/operations'

const operations = useOperationsStore()
const currentUser = getStoredUser()
const canCreateAlert = canUseAction(currentUser?.roleName, 'alerts:create')
const canHandleAlerts = canUseAction(currentUser?.roleName, 'alerts:handle')
const canDeleteAlert = canUseAction(currentUser?.roleName, 'alerts:delete')
const keyword = ref('')
const level = ref<AlertRecord['level'] | '全部等级'>('全部等级')
const status = ref<AlertRecord['status'] | '全部状态'>('待处理')
const feedback = ref('')
const showCreateForm = ref(false)
const deletingAlert = ref<AlertRecord | null>(null)

const createEmptyAlert = (): ManualAlertInput => ({
  elderlyId: 1,
  elderName: '刘玉梅',
  room: 'C-212',
  warningType: 'blood_pressure',
  warningLevel: 'medium',
  warningContent: '血压超过正常范围，已通知护理员复测',
})

const createForm = ref<ManualAlertInput>(createEmptyAlert())

const warningTypeOptions: { value: ManualAlertInput['warningType']; label: string }[] = [
  { value: 'blood_pressure', label: '血压异常' },
  { value: 'blood_sugar', label: '血糖异常' },
  { value: 'heart_rate', label: '心率异常' },
  { value: 'temperature', label: '体温异常' },
]

const warningLevelOptions: { value: ManualAlertInput['warningLevel']; label: string }[] = [
  { value: 'high', label: '紧急' },
  { value: 'medium', label: '关注' },
  { value: 'low', label: '普通' },
]

onMounted(() => {
  void operations.fetchAlerts()
})

const pendingCount = computed(() => operations.alerts.filter((a) => a.status === '待处理').length)
const processingCount = computed(
  () => operations.alerts.filter((a) => a.status === '处理中').length,
)
const resolvedCount = computed(() => operations.alerts.filter((a) => a.status === '已处理').length)
const criticalCount = computed(() => operations.alerts.filter((a) => a.level === '紧急').length)

const stats = computed(() => [
  {
    label: '全部告警',
    value: operations.alerts.length,
    note: '含系统自动和人工记录',
    color: 'text-gray-900 dark:text-white',
  },
  {
    label: '待处理',
    value: pendingCount.value,
    note: '需确认分派',
    color: 'text-red-600 dark:text-red-400',
  },
  {
    label: '处理中',
    value: processingCount.value,
    note: '已确认未归档',
    color: 'text-amber-600 dark:text-amber-400',
  },
  {
    label: '紧急',
    value: criticalCount.value,
    note: '优先通知医生',
    color: 'text-red-600 dark:text-red-400',
  },
])

const filteredAlerts = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  return operations.alerts.filter((a) => {
    const matchKw =
      !kw ||
      a.elderName.toLowerCase().includes(kw) ||
      a.room.toLowerCase().includes(kw) ||
      a.type.toLowerCase().includes(kw) ||
      a.source.toLowerCase().includes(kw) ||
      a.owner.toLowerCase().includes(kw)
    const matchLv = level.value === '全部等级' || a.level === level.value
    const matchSt = status.value === '全部状态' || a.status === status.value
    return matchKw && matchLv && matchSt
  })
})

const statusTabs = computed(() => [
  { label: '全部', value: '全部状态' as const, count: operations.alerts.length },
  { label: '待处理', value: '待处理' as const, count: pendingCount.value },
  { label: '处理中', value: '处理中' as const, count: processingCount.value },
  { label: '已归档', value: '已处理' as const, count: resolvedCount.value },
])

const levelBadge = (l: AlertRecord['level']) =>
  l === '紧急'
    ? 'bg-red-50 text-red-700 dark:bg-red-500/15 dark:text-red-400'
    : l === '关注'
      ? 'bg-amber-50 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400'
      : 'bg-blue-50 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400'
const statusBadge = (s: AlertRecord['status']) =>
  s === '待处理'
    ? 'bg-red-50 text-red-700 dark:bg-red-500/15 dark:text-red-400'
    : s === '处理中'
      ? 'bg-amber-50 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400'
      : 'bg-green-50 text-green-700 dark:bg-green-500/15 dark:text-green-400'

const slaText = (alert: AlertRecord) => {
  if (alert.status === '已处理') return alert.handledAt ? `归档 ${alert.handledAt}` : '已归档'
  if (alert.level === '紧急') return `${alert.slaMinutes || 5} 分钟内处理`
  return `${alert.slaMinutes || 15} 分钟内确认`
}

const resetFilters = () => {
  keyword.value = ''
  level.value = '全部等级'
  status.value = '全部状态'
}

const resetCreateForm = () => {
  createForm.value = createEmptyAlert()
}

const addManualAlert = async () => {
  if (!canCreateAlert) {
    feedback.value = '当前角色没有新增人工告警权限'
    return
  }
  await operations.addManualAlert({ ...createForm.value })
  resetFilters()
  resetCreateForm()
  showCreateForm.value = false
  feedback.value = '已新增一条健康预警'
}

const confirmDeleteAlert = (alert: AlertRecord) => {
  deletingAlert.value = alert
}

const deleteAlert = async () => {
  if (!deletingAlert.value) return

  const alert = deletingAlert.value
  try {
    await operations.removeAlert(alert.id)
    deletingAlert.value = null
    feedback.value = `${alert.room} ${alert.elderName} 的预警已删除`
  } catch (error) {
    feedback.value = error instanceof Error ? `删除失败：${error.message}` : '删除失败，请稍后重试'
  }
}
</script>
