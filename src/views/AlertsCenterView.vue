<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Alert Center
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          告警中心
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          集中处置健康异常、设备离线、夜间离床和人工上报事件，形成确认、处理、归档的闭环记录。
        </p>
      </div>

      <button
        v-if="canCreateAlert"
        type="button"
        class="inline-flex w-fit items-center justify-center gap-2 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
        @click="addManualAlert"
      >
        <Plus class="size-4" />
        新增人工告警
      </button>
    </div>

    <p
      v-if="feedback"
      class="mb-6 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
    >
      {{ feedback }}
    </p>

    <p
      v-if="operations.loading || operations.error"
      class="mb-6 w-fit rounded-lg border px-3 py-2 text-theme-sm"
      :class="operations.error ? 'border-warning-200 bg-warning-50 text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300' : 'border-brand-200 bg-brand-50 text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300'"
    >
      {{ operations.loading ? '正在同步健康预警接口...' : `<span>健康预警接口暂不可用：</span>${operations.error}` }}
    </p>

    <section class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4 md:gap-6">
      <article
        v-for="stat in stats"
        :key="stat.label"
        class="rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <div class="flex items-start justify-between gap-4">
          <div>
            <p class="text-theme-sm text-gray-500 dark:text-gray-400">{{ stat.label }}</p>
            <p class="mt-2 text-title-sm font-bold text-gray-900 tabular-nums dark:text-white">
              {{ stat.value }}
            </p>
          </div>
          <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="stat.tone">
            {{ stat.badge }}
          </span>
        </div>
        <p class="mt-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
          {{ stat.note }}
        </p>
      </article>
    </section>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <section
        class="col-span-12 rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8"
      >
        <div class="flex flex-col gap-4 xl:flex-row xl:items-start xl:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              告警列表
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
              当前筛选 {{ filteredAlerts.length }} 条，待处理 {{ pendingCount }} 条
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-[1fr_140px_140px] xl:w-[560px]">
            <label class="sr-only" for="alert-search">搜索告警</label>
            <div class="relative">
              <Search class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-gray-400" />
              <input
                id="alert-search"
                v-model="keyword"
                type="search"
                placeholder="搜索老人 / 房间 / 来源"
                class="h-11 w-full rounded-lg border border-gray-300 bg-transparent py-2.5 pl-9 pr-4 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
              />
            </div>

            <label class="sr-only" for="alert-level">告警等级</label>
            <select
              id="alert-level"
              v-model="level"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部等级">全部等级</option>
              <option value="紧急">紧急</option>
              <option value="关注">关注</option>
              <option value="普通">普通</option>
            </select>

            <label class="sr-only" for="alert-status">处理状态</label>
            <select
              id="alert-status"
              v-model="status"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部状态">全部状态</option>
              <option value="待处理">待处理</option>
              <option value="处理中">处理中</option>
              <option value="已处理">已处理</option>
            </select>
          </div>
        </div>

        <div class="mt-5 flex flex-wrap gap-2">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            type="button"
            class="rounded-lg border px-3 py-2 text-theme-xs font-medium"
            :class="status === tab.value ? 'border-brand-200 bg-brand-50 text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300' : 'border-gray-200 bg-white text-gray-600 hover:bg-gray-50 dark:border-gray-800 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]'"
            @click="status = tab.value"
          >
            {{ tab.label }}
            <span class="ml-1 tabular-nums">{{ tab.count }}</span>
          </button>
        </div>

        <div class="mt-5 space-y-3">
          <article
            v-for="alert in filteredAlerts"
            :key="alert.id"
            class="cursor-pointer rounded-xl border p-4"
            :class="selectedAlert?.id === alert.id ? 'border-brand-200 bg-brand-50 dark:border-brand-500/30 dark:bg-brand-500/10' : 'border-gray-100 bg-gray-50 hover:bg-gray-100 dark:border-gray-800 dark:bg-gray-900 dark:hover:bg-white/[0.04]'"
            @click="selectAlert(alert.id)"
          >
            <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
              <div class="min-w-0">
                <div class="flex flex-wrap items-center gap-2">
                  <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="levelClassMap[alert.level]">
                    {{ alert.level }}
                  </span>
                  <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[alert.status]">
                    {{ alert.status }}
                  </span>
                  <span
                    v-if="alert.escalation"
                    class="rounded-full bg-error-50 px-2 py-0.5 text-theme-xs font-medium text-error-700 dark:bg-error-500/15 dark:text-error-400"
                  >
                    已升级
                  </span>
                  <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ alert.time }}</span>
                </div>
                <h3 class="mt-2 font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                  {{ alert.room }} {{ alert.elderName }} · {{ alert.type }}
                </h3>
                <p class="mt-1 line-clamp-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
                  {{ alert.content || `${alert.source} 触发，责任人：${alert.owner}` }}
                </p>
              </div>

              <div class="flex shrink-0 flex-col gap-2 text-left md:text-right">
                <span class="text-theme-xs text-gray-500 dark:text-gray-400">
                  {{ alert.source }}
                </span>
                <span class="font-medium text-theme-sm text-gray-800 dark:text-white/90">
                  {{ alert.owner }}
                </span>
                <span class="text-theme-xs text-gray-500 dark:text-gray-400">
                  {{ slaText(alert) }}
                </span>
              </div>
            </div>
          </article>

          <div
            v-if="filteredAlerts.length === 0"
            class="rounded-xl border border-dashed border-gray-300 p-8 text-center dark:border-gray-700"
          >
            <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">没有匹配的告警</p>
            <button
              type="button"
              class="mt-4 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white hover:bg-brand-700"
              @click="resetFilters"
            >
              清空筛选
            </button>
          </div>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <div class="flex items-start justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
                处置详情
              </h2>
              <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
                {{ selectedAlert ? `${selectedAlert.room} ${selectedAlert.elderName}` : '请选择一条告警' }}
              </p>
            </div>
            <AlertTriangle class="size-5 text-warning-500" />
          </div>

          <div v-if="selectedAlert" class="mt-5 space-y-5">
            <div class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
              <div class="flex flex-wrap items-center gap-2">
                <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="levelClassMap[selectedAlert.level]">
                  {{ selectedAlert.level }}
                </span>
                <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[selectedAlert.status]">
                  {{ selectedAlert.status }}
                </span>
              </div>
              <h3 class="mt-3 font-semibold text-theme-sm text-gray-800 dark:text-white/90">
                {{ selectedAlert.type }}
              </h3>
              <p class="mt-2 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">
                {{ selectedAlert.content || '暂无告警详情' }}
              </p>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div class="rounded-xl border border-gray-100 bg-gray-50 p-3 dark:border-gray-800 dark:bg-gray-900">
                <p class="text-theme-xs text-gray-500 dark:text-gray-400">责任人</p>
                <p class="mt-1 font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ selectedAlert.owner }}</p>
              </div>
              <div class="rounded-xl border border-gray-100 bg-gray-50 p-3 dark:border-gray-800 dark:bg-gray-900">
                <p class="text-theme-xs text-gray-500 dark:text-gray-400">联系电话</p>
                <p class="mt-1 font-medium text-theme-sm text-gray-800 tabular-nums dark:text-white/90">
                  {{ maskPhone(selectedAlert.contactPhone) }}
                </p>
              </div>
              <div class="rounded-xl border border-gray-100 bg-gray-50 p-3 dark:border-gray-800 dark:bg-gray-900">
                <p class="text-theme-xs text-gray-500 dark:text-gray-400">来源</p>
                <p class="mt-1 font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ selectedAlert.source }}</p>
              </div>
              <div class="rounded-xl border border-gray-100 bg-gray-50 p-3 dark:border-gray-800 dark:bg-gray-900">
                <p class="text-theme-xs text-gray-500 dark:text-gray-400">SLA</p>
                <p class="mt-1 font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ slaText(selectedAlert) }}</p>
              </div>
            </div>

            <label class="block">
              <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
                处置结果
              </span>
              <textarea
                v-model="handleResultDraft"
                rows="5"
                :disabled="!canHandleAlerts"
                class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
                placeholder="填写复测结果、现场情况或联系医生结果"
              ></textarea>
            </label>

            <p
              v-if="!canHandleAlerts"
              class="rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-theme-xs text-gray-600 dark:border-gray-800 dark:bg-gray-900 dark:text-gray-300"
            >
              当前角色只能查看告警详情，不能处理或归档。
            </p>

            <div v-else class="grid gap-3 sm:grid-cols-2">
              <button
                v-if="selectedAlert.status === '待处理'"
                type="button"
                class="inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
                @click="startSelectedAlert"
              >
                <Clock3 class="size-4" />
                开始处理
              </button>
              <button
                v-if="selectedAlert.status !== '已处理' && canEscalateAlert"
                type="button"
                class="inline-flex items-center justify-center gap-2 rounded-lg border border-error-200 bg-error-50 px-3 py-2.5 text-theme-sm font-medium text-error-700 shadow-theme-xs hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
                @click="escalateSelectedAlert"
              >
                <ArrowUpRight class="size-4" />
                升级告警
              </button>
              <button
                v-if="selectedAlert.status !== '已处理'"
                type="button"
                class="inline-flex items-center justify-center gap-2 rounded-lg bg-brand-600 px-3 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 sm:col-span-2"
                @click="resolveSelectedAlert"
              >
                <ClipboardCheck class="size-4" />
                保存并归档
              </button>
            </div>

            <div
              v-if="selectedAlert.status === '已处理'"
              class="rounded-xl border border-success-200 bg-success-50 p-4 dark:border-success-500/30 dark:bg-success-500/10"
            >
              <div class="flex items-start gap-3">
                <CheckCircle2 class="mt-0.5 size-5 text-success-600 dark:text-success-300" />
                <div>
                  <p class="font-medium text-theme-sm text-success-800 dark:text-success-200">已归档</p>
                  <p class="mt-1 text-theme-sm text-success-700 text-pretty dark:text-success-300">
                    {{ selectedAlert.handleResult || '该告警已完成处理。' }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div
            v-else
            class="mt-5 rounded-xl border border-dashed border-gray-300 p-8 text-center dark:border-gray-700"
          >
            <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">暂无详情</p>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">从左侧告警列表选择一条记录。</p>
          </div>
        </section>

        <section class="rounded-xl border border-error-100 bg-error-25 p-5 dark:border-error-900 dark:bg-error-500/[0.08]">
          <div class="flex items-start gap-3">
            <ShieldAlert class="mt-0.5 size-5 text-error-600 dark:text-error-300" />
            <div>
              <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">
                紧急升级
              </h2>
              <p class="mt-2 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">
                紧急告警超过 5 分钟未归档时，应同步通知护理管理员和当班医生。
              </p>
            </div>
          </div>
        </section>
      </aside>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import {
  AlertTriangle,
  ArrowUpRight,
  CheckCircle2,
  ClipboardCheck,
  Clock3,
  Plus,
  Search,
  ShieldAlert,
} from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import type { AlertRecord } from '@/stores/operations'
import { maskPhone } from '@/utils/privacy'

const operations = useOperationsStore()
const currentUser = getStoredUser()
const canCreateAlert = canUseAction(currentUser?.roleName, 'alerts:create')
const canHandleAlerts = canUseAction(currentUser?.roleName, 'alerts:handle')
const canEscalateAlert = canUseAction(currentUser?.roleName, 'alerts:escalate')
const keyword = ref('')
const level = ref<AlertRecord['level'] | '全部等级'>('全部等级')
const status = ref<AlertRecord['status'] | '全部状态'>('全部状态')
const selectedAlertId = ref<number | null>(null)
const handleResultDraft = ref('')
const feedback = ref('')

onMounted(() => {
  void operations.fetchAlerts()
})

const pendingCount = computed(() => operations.alerts.filter((alert) => alert.status === '待处理').length)
const processingCount = computed(() => operations.alerts.filter((alert) => alert.status === '处理中').length)
const resolvedCount = computed(() => operations.alerts.filter((alert) => alert.status === '已处理').length)
const criticalCount = computed(() => operations.alerts.filter((alert) => alert.level === '紧急').length)

const stats = computed(() => [
  {
    label: '今日告警',
    value: operations.alerts.length,
    badge: '总量',
    tone: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
    note: '含系统自动告警和人工记录',
  },
  {
    label: '待处理',
    value: pendingCount.value,
    badge: '确认',
    tone: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
    note: '需要护理人员确认和分派',
  },
  {
    label: '处理中',
    value: processingCount.value,
    badge: '跟进',
    tone: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
    note: '已确认但还未归档',
  },
  {
    label: '紧急告警',
    value: criticalCount.value,
    badge: '医生',
    tone: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
    note: '优先通知医生和护理管理员',
  },
])

const filteredAlerts = computed(() => {
  const searchText = keyword.value.trim().toLowerCase()

  return operations.alerts.filter((alert) => {
    const matchesKeyword =
      !searchText ||
      alert.elderName.toLowerCase().includes(searchText) ||
      alert.room.toLowerCase().includes(searchText) ||
      alert.type.toLowerCase().includes(searchText) ||
      alert.source.toLowerCase().includes(searchText) ||
      alert.owner.toLowerCase().includes(searchText)
    const matchesLevel = level.value === '全部等级' || alert.level === level.value
    const matchesStatus = status.value === '全部状态' || alert.status === status.value

    return matchesKeyword && matchesLevel && matchesStatus
  })
})

const statusTabs = computed(() => [
  { label: '全部', value: '全部状态' as const, count: operations.alerts.length },
  { label: '待处理', value: '待处理' as const, count: pendingCount.value },
  { label: '处理中', value: '处理中' as const, count: processingCount.value },
  { label: '已处理', value: '已处理' as const, count: resolvedCount.value },
])

const selectedAlert = computed(() =>
  operations.alerts.find((alert) => alert.id === selectedAlertId.value) || filteredAlerts.value[0],
)

watch(
  filteredAlerts,
  (alerts) => {
    if (!alerts.length) {
      selectedAlertId.value = null
      return
    }

    if (!alerts.some((alert) => alert.id === selectedAlertId.value)) {
      selectedAlertId.value = alerts[0].id
    }
  },
  { immediate: true },
)

watch(
  selectedAlert,
  (alert) => {
    handleResultDraft.value =
      alert?.handleResult ||
      (alert?.status === '已处理' ? '已完成处置。' : '已确认老人状态，记录复测和现场处置结果。')
  },
  { immediate: true },
)

const levelClassMap: Record<AlertRecord['level'], string> = {
  紧急: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  关注: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  普通: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
}

const statusClassMap: Record<AlertRecord['status'], string> = {
  待处理: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  处理中: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  已处理: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
}

const slaText = (alert: AlertRecord) => {
  if (alert.status === '已处理') return alert.handledAt ? `归档 ${alert.handledAt}` : '已归档'
  if (alert.level === '紧急') return `${alert.slaMinutes || 5} 分钟内处理`
  if (alert.level === '关注') return `${alert.slaMinutes || 15} 分钟内确认`
  return `${alert.slaMinutes || 30} 分钟内跟进`
}

const selectAlert = (id: number) => {
  selectedAlertId.value = id
}

const resetFilters = () => {
  keyword.value = ''
  level.value = '全部等级'
  status.value = '全部状态'
}

const addManualAlert = async () => {
  if (!canCreateAlert) {
    feedback.value = '当前角色没有新增人工告警权限'
    return
  }

  await operations.addManualAlert()
  resetFilters()
  selectedAlertId.value = operations.alerts[0]?.id ?? null
  feedback.value = '已新增一条人工巡房提醒'
}

const startSelectedAlert = async () => {
  if (!canHandleAlerts) {
    feedback.value = '当前角色没有处理告警权限'
    return
  }

  if (!selectedAlert.value) return
  await operations.startAlert(selectedAlert.value.id)
  feedback.value = `${selectedAlert.value.room} ${selectedAlert.value.elderName} 已进入处理中`
}

const escalateSelectedAlert = () => {
  if (!canEscalateAlert) {
    feedback.value = '当前角色没有升级告警权限'
    return
  }

  if (!selectedAlert.value) return
  operations.escalateAlert(selectedAlert.value.id)
  feedback.value = `${selectedAlert.value.room} ${selectedAlert.value.elderName} 的告警已升级`
}

const resolveSelectedAlert = async () => {
  if (!canHandleAlerts) {
    feedback.value = '当前角色没有归档告警权限'
    return
  }

  if (!selectedAlert.value) return
  const result = handleResultDraft.value.trim()

  if (!result) {
    feedback.value = '请先填写处置结果'
    return
  }

  await operations.resolveAlert(selectedAlert.value.id, result)
  feedback.value = `${selectedAlert.value.room} ${selectedAlert.value.elderName} 的告警已归档`
}
</script>
