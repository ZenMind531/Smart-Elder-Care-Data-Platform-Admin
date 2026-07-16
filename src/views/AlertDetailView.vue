<template>
  <AdminLayout>
    <button
      type="button"
      class="mb-6 flex items-center gap-1 text-theme-sm text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
      @click="$router.push('/alerts')"
    >
      ← 返回告警列表
    </button>

    <p
      v-if="detailFeedback"
      class="mb-5 w-fit rounded-xl bg-brand-50 px-4 py-2 text-sm font-medium text-brand-700 dark:bg-brand-500/15 dark:text-brand-400"
    >
      {{ detailFeedback }}
    </p>

    <div
      v-if="detailLoading"
      class="rounded-xl border border-dashed border-gray-300 p-12 text-center dark:border-gray-700"
    >
      <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">正在读取告警详情...</p>
    </div>

    <div
      v-else-if="!alert"
      class="rounded-xl border border-dashed border-gray-300 p-12 text-center dark:border-gray-700"
    >
      <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">告警不存在或已删除</p>
      <RouterLink
        to="/alerts"
        class="mt-3 inline-block text-theme-sm text-brand-600 hover:underline"
        >返回列表</RouterLink
      >
    </div>

    <template v-else>
      <div class="mb-6">
        <div class="flex flex-wrap items-center gap-2">
          <span
            class="rounded-full px-2.5 py-1 text-theme-xs font-semibold"
            :class="levelBadge(alert.level)"
            >{{ alert.level }}</span
          >
          <span
            class="rounded-full px-2.5 py-1 text-theme-xs font-semibold"
            :class="statusBadge(alert.status)"
            >{{ alert.status }}</span
          >
          <span
            v-if="alert.escalation"
            class="rounded-full bg-brand-50 px-2.5 py-1 text-theme-xs font-semibold text-brand-700 dark:bg-brand-500/15 dark:text-brand-400"
            >已升级</span
          >
        </div>
        <h1 class="mt-3 text-2xl font-bold text-gray-900 dark:text-white">
          {{ alert.room }} {{ alert.elderName }}
        </h1>
        <p class="mt-2 text-lg font-medium text-gray-600 dark:text-gray-300">{{ alert.type }}</p>
      </div>

      <div class="grid grid-cols-1 gap-6 xl:grid-cols-3">
        <div class="space-y-6 xl:col-span-2">
          <section
            class="rounded-[18px] border border-gray-200 bg-white p-6 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
          >
            <h2
              class="text-sm font-semibold uppercase tracking-wider text-gray-400 dark:text-gray-500"
            >
              告警详情
            </h2>
            <p class="mt-4 text-base leading-relaxed text-gray-700 dark:text-gray-300">
              {{ alert.content || '暂无告警详情' }}
            </p>

            <div class="mt-6 grid gap-4 sm:grid-cols-2">
              <div
                class="rounded-xl border border-gray-100 bg-gray-50/70 p-4 dark:border-gray-800 dark:bg-gray-900/50"
              >
                <span class="text-theme-xs font-medium uppercase tracking-wider text-gray-400"
                  >来源</span
                >
                <p class="mt-2 text-sm font-semibold text-gray-800 dark:text-white/90">
                  {{ alert.source }}
                </p>
              </div>
              <div
                class="rounded-xl border border-gray-100 bg-gray-50/70 p-4 dark:border-gray-800 dark:bg-gray-900/50"
              >
                <span class="text-theme-xs font-medium uppercase tracking-wider text-gray-400"
                  >SLA</span
                >
                <p class="mt-2 text-sm font-semibold text-gray-800 dark:text-white/90">
                  {{ slaText }}
                </p>
              </div>
              <div
                class="rounded-xl border border-gray-100 bg-gray-50/70 p-4 dark:border-gray-800 dark:bg-gray-900/50"
              >
                <span class="text-theme-xs font-medium uppercase tracking-wider text-gray-400"
                  >责任人</span
                >
                <div v-if="alert.status !== '已处理'" class="mt-2">
                  <select
                    v-model="ownerDraft"
                    class="h-10 w-full rounded-lg border border-gray-300 bg-white px-3 text-sm dark:border-gray-700 dark:bg-gray-800 dark:text-white/90 focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 focus:outline-none"
                    @change="assignOwner"
                  >
                    <option value="" disabled>
                      {{ alert.owner === '待分配' ? '选择责任人…' : alert.owner }}
                    </option>
                    <option v-for="u in staffOptions" :key="u.id" :value="u.label">
                      {{ u.label }}
                    </option>
                  </select>
                </div>
                <p v-else class="mt-2 text-sm font-semibold text-gray-800 dark:text-white/90">
                  {{ alert.owner }}
                </p>
              </div>
              <div
                class="rounded-xl border border-gray-100 bg-gray-50/70 p-4 dark:border-gray-800 dark:bg-gray-900/50"
              >
                <span class="text-theme-xs font-medium uppercase tracking-wider text-gray-400"
                  >联系电话</span
                >
                <p class="mt-2 text-sm font-semibold text-gray-800 tabular-nums dark:text-white/90">
                  {{ maskPhone(alert.contactPhone) }}
                </p>
              </div>
            </div>
          </section>

          <section
            class="rounded-[18px] border border-gray-200 bg-white p-6 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
          >
            <h2
              class="text-sm font-semibold uppercase tracking-wider text-gray-400 dark:text-gray-500"
            >
              处置结果
            </h2>
            <textarea
              v-model="resultDraft"
              rows="6"
              :disabled="!canHandleAlerts"
              class="mt-4 w-full rounded-xl border border-gray-200 bg-gray-50/50 px-4 py-3 text-sm text-gray-800 focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 focus:outline-none dark:border-gray-700 dark:bg-gray-900/50 dark:text-white/90"
              :placeholder="
                alert.status === '已处理'
                  ? '已归档'
                  : '填写复测结果、现场情况或联系医生后的处置结论…'
              "
            />

            <div
              v-if="alert.status === '已处理'"
              class="mt-4 flex items-start gap-3 rounded-xl bg-success-50 px-4 py-3 dark:bg-success-500/10"
            >
              <CheckCircle2 class="mt-0.5 size-5 shrink-0 text-success-600 dark:text-success-400" />
              <div>
                <p class="text-sm font-semibold text-success-800 dark:text-success-300">已归档</p>
                <p class="mt-1 text-sm text-success-700 dark:text-success-400">
                  {{ alert.handleResult || '该告警已完成处理。' }}
                </p>
              </div>
            </div>
          </section>

          <div v-if="canHandleAlerts && alert.status !== '已处理'" class="flex flex-wrap gap-3">
            <button
              v-if="alert.status === '待处理'"
              type="button"
              class="inline-flex items-center gap-2 rounded-xl border border-gray-200 bg-white px-5 py-3 text-sm font-semibold text-gray-700 shadow-sm hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 transition-colors"
              @click="startAlert"
            >
              <Clock3 class="size-4" />开始处理
            </button>
            <button
              v-if="canEscalateAlert"
              type="button"
              class="inline-flex items-center gap-2 rounded-xl border border-brand-200 bg-brand-50 px-5 py-3 text-sm font-semibold text-brand-700 shadow-sm hover:bg-brand-100 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-400 transition-colors"
              @click="escalateAlert"
            >
              <ArrowUpRight class="size-4" />升级告警
            </button>
            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-5 py-3 text-sm font-semibold text-white shadow-sm hover:bg-brand-700 transition-colors"
              @click="saveResolve"
            >
              <ClipboardCheck class="size-4" />保存并归档
            </button>
            <button
              v-if="canDeleteAlert"
              type="button"
              class="inline-flex items-center gap-2 rounded-xl border border-error-200 bg-error-50 px-5 py-3 text-sm font-semibold text-error-700 shadow-sm hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300 transition-colors"
              @click="confirmingDelete = true"
            >
              <Trash2 class="size-4" />删除告警
            </button>
          </div>
          <div v-else-if="canDeleteAlert" class="flex flex-wrap gap-3">
            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-xl border border-error-200 bg-error-50 px-5 py-3 text-sm font-semibold text-error-700 shadow-sm hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300 transition-colors"
              @click="confirmingDelete = true"
            >
              <Trash2 class="size-4" />删除告警
            </button>
          </div>
        </div>

        <aside class="space-y-6">
          <section
            class="rounded-[18px] border border-gray-200 bg-white p-6 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
          >
            <h2
              class="text-sm font-semibold uppercase tracking-wider text-gray-400 dark:text-gray-500"
            >
              时间线
            </h2>
            <div class="mt-4 space-y-4">
              <div class="flex gap-3">
                <div
                  class="mt-1 size-2.5 shrink-0 rounded-full bg-brand-500 ring-4 ring-brand-50 dark:ring-brand-500/15"
                />
                <div class="min-w-0">
                  <p class="text-sm font-medium text-gray-800 dark:text-white/90">告警生成</p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ alert.time }}</p>
                </div>
              </div>
              <div v-if="alert.owner !== '待分配'" class="flex gap-3">
                <div
                  class="mt-1 size-2.5 shrink-0 rounded-full bg-brand-400 ring-4 ring-brand-50 dark:ring-brand-500/15"
                />
                <div class="min-w-0">
                  <p class="text-sm font-medium text-gray-800 dark:text-white/90">分配责任人</p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ alert.owner }}</p>
                </div>
              </div>
              <div v-if="alert.status === '处理中' || alert.status === '已处理'" class="flex gap-3">
                <div
                  class="mt-1 size-2.5 shrink-0 rounded-full bg-brand-400 ring-4 ring-brand-50 dark:ring-brand-500/15"
                />
                <div class="min-w-0">
                  <p class="text-sm font-medium text-gray-800 dark:text-white/90">处理中</p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">事故响应与处置</p>
                </div>
              </div>
              <div v-if="alert.status === '已处理'" class="flex gap-3">
                <div
                  class="mt-1 size-2.5 shrink-0 rounded-full bg-brand-500 ring-4 ring-brand-50 dark:ring-brand-500/15"
                />
                <div class="min-w-0">
                  <p class="text-sm font-medium text-gray-800 dark:text-white/90">已归档</p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">
                    {{ alert.handledAt || '处置完成' }}
                  </p>
                </div>
              </div>
            </div>
          </section>

          <section class="rounded-[18px] bg-red-50 p-6 dark:bg-red-500/5">
            <div class="flex items-start gap-3">
              <ShieldAlert class="mt-0.5 size-5 shrink-0 text-red-600 dark:text-red-400" />
              <div>
                <h2 class="text-sm font-semibold text-red-800 dark:text-red-300">紧急升级</h2>
                <p class="mt-1 text-sm text-red-700 dark:text-red-400">
                  紧急告警超过 5 分钟未归档时，系统将同步通知护理管理员和当班医生。
                </p>
              </div>
            </div>
          </section>
        </aside>
      </div>
    </template>

    <div
      v-if="confirmingDelete && alert"
      class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900/45 p-4"
      role="alertdialog"
      aria-modal="true"
      aria-labelledby="delete-alert-detail-title"
    >
      <section
        class="w-full max-w-md rounded-xl border border-gray-200 bg-white p-5 shadow-lg dark:border-gray-800 dark:bg-gray-900"
      >
        <h2
          id="delete-alert-detail-title"
          class="text-lg font-semibold text-gray-900 text-balance dark:text-white"
        >
          删除健康预警
        </h2>
        <p class="mt-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          将删除 {{ alert.room }} {{ alert.elderName }} 的这条预警记录，删除后会返回告警列表。
        </p>
        <div class="mt-5 flex justify-end gap-3">
          <button
            type="button"
            class="rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
            @click="confirmingDelete = false"
          >
            取消
          </button>
          <button
            type="button"
            class="rounded-lg bg-error-600 px-4 py-2.5 text-theme-sm font-medium text-white hover:bg-error-700"
            @click="deleteCurrentAlert"
          >
            确认删除
          </button>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { RouterLink } from 'vue-router'
import {
  ArrowUpRight,
  CheckCircle2,
  ClipboardCheck,
  Clock3,
  ShieldAlert,
  Trash2,
} from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import { listUsers } from '@/api/users'
import type { AlertRecord } from '@/stores/operations'
import { maskPhone } from '@/utils/privacy'

const route = useRoute()
const router = useRouter()
const operations = useOperationsStore()
const currentUser = getStoredUser()
const canHandleAlerts = canUseAction(currentUser?.roleName, 'alerts:handle')
const canEscalateAlert = canUseAction(currentUser?.roleName, 'alerts:escalate')
const canDeleteAlert = canUseAction(currentUser?.roleName, 'alerts:delete')
const ownerDraft = ref('')
const resultDraft = ref('')
const staffOptions = ref<{ id: number; label: string }[]>([])
const detailLoading = ref(false)
const detailFeedback = ref('')
const confirmingDelete = ref(false)

const alertId = computed(() => Number(route.params.id))
const alert = computed(() => operations.alerts.find((a) => a.id === alertId.value) || null)

const levelBadge = (l: AlertRecord['level']) =>
  l === '紧急'
    ? 'bg-red-50 text-red-700 dark:bg-red-500/15 dark:text-red-400'
    : l === '关注'
      ? 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-400'
      : 'bg-blue-50 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400'
const statusBadge = (s: AlertRecord['status']) =>
  s === '待处理'
    ? 'bg-red-50 text-red-700 dark:bg-red-500/15 dark:text-red-400'
    : s === '处理中'
      ? 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-400'
      : 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-400'

const slaText = computed(() => {
  if (!alert.value) return ''
  if (alert.value.status === '已处理')
    return alert.value.handledAt ? `归档 ${alert.value.handledAt}` : '已归档'
  if (alert.value.level === '紧急') return `${alert.value.slaMinutes || 5} 分钟内处理`
  if (alert.value.level === '关注') return `${alert.value.slaMinutes || 15} 分钟内确认`
  return `${alert.value.slaMinutes || 30} 分钟内跟进`
})

const loadAlertDetail = async () => {
  if (!Number.isFinite(alertId.value)) return

  detailLoading.value = true
  const detail = await operations.fetchAlertDetail(alertId.value)
  if (!detail && operations.error) {
    detailFeedback.value = `告警详情加载失败：${operations.error}`
  }
  detailLoading.value = false
}

onMounted(async () => {
  await loadAlertDetail()
  try {
    const users = await listUsers({ status: 'enabled' })
    staffOptions.value = users.records.map((u) => ({
      id: u.id,
      label: u.realName ? `${u.realName}（${u.username}）` : u.username || `用户${u.id}`,
    }))
  } catch {
    /* 用户列表加载失败不影响告警功能 */
  }
})

watch(alertId, () => {
  void loadAlertDetail()
})

watch(
  alert,
  (currentAlert) => {
    ownerDraft.value =
      currentAlert?.owner && currentAlert.owner !== '待分配' ? currentAlert.owner : ''
    resultDraft.value = currentAlert?.handleResult || ''
  },
  { immediate: true },
)

const assignOwner = () => {
  if (!alert.value || !ownerDraft.value) return
  alert.value.owner = ownerDraft.value
}

const startAlert = async () => {
  if (!alert.value) return
  await operations.startAlert(alert.value.id)
}

const escalateAlert = () => {
  if (!alert.value) return
  operations.escalateAlert(alert.value.id)
}

const saveResolve = async () => {
  if (!alert.value) return
  const result = resultDraft.value.trim() || `已确认老人状态，记录复测和现场处置结果。`
  await operations.resolveAlert(alert.value.id, result)
  detailFeedback.value = '告警已归档'
}

const deleteCurrentAlert = async () => {
  if (!alert.value) return

  try {
    await operations.removeAlert(alert.value.id)
    confirmingDelete.value = false
    await router.push('/alerts')
  } catch (error) {
    detailFeedback.value =
      error instanceof Error ? `删除失败：${error.message}` : '删除失败，请稍后重试'
  }
}
</script>
