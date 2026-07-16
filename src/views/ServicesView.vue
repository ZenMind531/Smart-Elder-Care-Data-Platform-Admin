<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">Service Appointments</span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">服务预约</h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">统一登记健康检查、上门护理、康复训练和健康咨询服务，跟踪排班与服务进度。</p>
      </div>
      <div class="flex items-center gap-2">
        <button v-if="canCreate" type="button" class="inline-flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700" @click="openModal()"><Plus class="size-4" />新建预约</button>
        <button type="button" :disabled="loading" class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="load">刷新</button>
      </div>
    </div>

    <p v-if="loading" class="mb-4 w-fit rounded-lg border border-brand-200 bg-brand-50 px-3 py-2 text-theme-sm text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300">加载中…</p>
    <p v-if="error" class="mb-4 w-fit rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300">{{ error }}</p>
    <p v-if="feedback" class="mb-4 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300">{{ feedback }}</p>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">预约列表</h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">未完成 {{ records.filter(r => r.status === '未完成').length }} 条</p>
          </div>
          <div class="flex gap-2 flex-wrap">
            <input v-model="keyword" type="search" placeholder="搜索老人…" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs sm:w-[180px] dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
            <select v-model="statusFilter" class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option value="全部">全部状态</option>
              <option value="未完成">未完成</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
            </select>
          </div>
        </div>

        <div v-if="!loading && filtered.length === 0" class="mt-8 py-12 text-center">
          <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-gray-100 dark:bg-gray-800">
            <Calendar class="size-8 text-gray-400" />
          </div>
          <p class="text-theme-sm text-gray-500 dark:text-gray-400">暂无预约记录</p>
        </div>

        <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
          <table class="min-w-full">
            <thead>
              <tr class="border-t border-gray-100 dark:border-gray-800">
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">服务</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">预约时间</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">负责人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">状态</th>
                <th class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in filtered" :key="s.id" class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]">
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">{{ s.elderName }}</p>
                  <p class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">#{{ s.id }}</p>
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ s.service }}</td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ s.scheduledAt }}</td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ s.assignee }}</td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[s.status]">{{ s.status }}</span>
                </td>
                <td class="py-4 whitespace-nowrap">
                  <div class="flex gap-1.5 flex-wrap">
                    <template v-if="s.status === '未完成'">
                      <button v-if="canUpdate" type="button" class="rounded-lg border border-success-200 bg-success-50 px-2 py-1 text-theme-xs font-medium text-success-700 hover:bg-success-100" @click="doComplete(s)">完成</button>
                      <button v-if="canUpdate" type="button" class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-theme-xs font-medium text-gray-700 hover:bg-gray-50" @click="doCancel(s)">取消</button>
                    </template>
                    <button v-if="canUpdate" type="button" class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-theme-xs font-medium text-gray-700 hover:bg-gray-50" @click="openModal(s)">编辑</button>
                    <button v-if="canDelete" type="button" class="rounded-lg border border-error-100 bg-error-50 px-2 py-1 text-theme-xs font-medium text-error-700 hover:bg-error-100" @click="handleDelete(s)">删除</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">服务概览</h2>
          <div class="mt-5 grid grid-cols-2 gap-3">
            <div v-for="item in summary" :key="item.label" class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
              <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ item.label }}</p>
              <p class="mt-2 text-lg font-semibold text-gray-900 tabular-nums dark:text-white">{{ item.value }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
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
      </aside>
    </div>

    <!-- Modal -->
    <div v-if="modalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="modalOpen = false">
      <div class="w-full max-w-lg rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">{{ form.editing ? '编辑预约' : '新建预约' }}</h3>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="submitForm">
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">选择老人 *</span>
            <select v-model.number="form.elderlyId" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option :value="0" disabled>请选择老人</option>
              <option v-for="elder in elderlyOptions" :key="elder.id" :value="elder.id">{{ elderlyLabel(elder) }}</option>
              <option v-if="form.elderlyId && !elderlyOptions.some(e => e.id === form.elderlyId)" :value="form.elderlyId">老人ID {{ form.elderlyId }}</option>
            </select>
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">服务类型 *</span>
            <select v-model="form.serviceType" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option value="health_check">健康检查</option>
              <option value="home_care">上门护理</option>
              <option value="rehabilitation">康复训练</option>
              <option value="consultation">健康咨询</option>
              <option value="other">其他</option>
            </select>
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">预约时间 *</span>
            <input v-model="form.appointmentTime" type="datetime-local" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">服务人员</span>
            <input v-model="form.doctorName" type="text" placeholder="选填" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">预约描述</span>
            <textarea v-model="form.description" rows="3" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" placeholder="描述本次预约的目的和具体要求…"></textarea>
          </label>
          <p v-if="formError" class="text-theme-xs text-error-600 sm:col-span-2">{{ formError }}</p>
          <div class="flex justify-end gap-2 sm:col-span-2">
            <button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700" @click="modalOpen = false">取消</button>
            <button type="submit" :disabled="submitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white disabled:opacity-60">{{ submitting ? '保存中…' : '保存' }}</button>
          </div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { Plus, Calendar } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { ApiError, getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore, type ServiceAppointment } from '@/stores/operations'
import { listElderly, type ElderlyApiRecord } from '@/api/elderly'
import type { ServiceType } from '@/api/appointments'

const roleName = getStoredUser()?.roleName ?? null
const canCreate = canUseAction(roleName, 'services:create')
const canUpdate = canUseAction(roleName, 'services:update')
const canDelete = canUseAction(roleName, 'services:delete')

const operations = useOperationsStore()
const loading = ref(false)
const error = ref('')
const feedback = ref('')
const keyword = ref('')
const statusFilter = ref<string>('全部')

const elderlyOptions = ref<ElderlyApiRecord[]>([])
const fetchElderly = async () => {
  try {
    const r = await listElderly({ page: 1, size: 200 })
    elderlyOptions.value = r.records
  } catch { /* 静默失败 */ }
}

const elderlyLabel = (e: ElderlyApiRecord) => {
  const parts = [`${e.elderlyName || '未命名'} / ID ${e.id}`]
  if (e.age != null) parts.push(`${e.age}岁`)
  return parts.join(' / ')
}

const records = computed(() => operations.serviceAppointments)

const filtered = computed(() =>
  records.value.filter((s) => {
    const kw = keyword.value.trim().toLowerCase()
    const matchKeyword = !kw || s.elderName.toLowerCase().includes(kw)
    const matchStatus = statusFilter.value === '全部' || s.status === statusFilter.value
    return matchKeyword && matchStatus
  }),
)

const summary = computed(() => [
  { label: '总预约', value: records.value.length },
  { label: '未完成', value: records.value.filter((s) => s.status === '未完成').length },
  { label: '已完成', value: records.value.filter((s) => s.status === '已完成').length },
  { label: '已取消', value: records.value.filter((s) => s.status === '已取消').length },
])

const catalog = computed(() => [
  { name: '健康检查', count: records.value.filter((s) => s.service === '健康检查').length, desc: '定期健康体检和指标评估。' },
  { name: '上门护理', count: records.value.filter((s) => s.service === '上门护理').length, desc: '护理员按预约提供上门护理服务。' },
  { name: '康复训练', count: records.value.filter((s) => s.service === '康复训练').length, desc: '康复师按计划安排训练项目。' },
  { name: '健康咨询', count: records.value.filter((s) => s.service === '健康咨询').length, desc: '医生线上或线下评估近期健康状态。' },
])

const statusClassMap: Record<ServiceAppointment['status'], string> = {
  '未完成': 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  '已完成': 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  '已取消': 'bg-gray-100 text-gray-500 dark:bg-gray-800 dark:text-gray-400',
}

const load = async () => {
  loading.value = true; error.value = ''
  try { await operations.fetchServiceAppointments() } catch (e) { error.value = e instanceof ApiError ? e.message : '加载失败' }
  finally { loading.value = false }
}

onMounted(() => { void load(); void fetchElderly() })

// ── 表单 ──
const modalOpen = ref(false)
const submitting = ref(false)
const formError = ref('')

const pad = (v: number) => String(v).padStart(2, '0')
const formatLocalDateTime = (d = new Date()) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`

const serviceLabelToType = (label: string): ServiceType => {
  const map: Record<string, ServiceType> = { '健康检查': 'health_check', '上门护理': 'home_care', '康复训练': 'rehabilitation', '健康咨询': 'consultation' }
  return map[label] || 'other'
}

const emptyForm = () => ({
  editing: false as boolean,
  id: undefined as number | undefined,
  elderlyId: 0,
  serviceType: 'health_check' as ServiceType,
  appointmentTime: '',
  doctorName: '',
  description: '',
})

const form = reactive({ ...emptyForm() })

const openModal = (s?: ServiceAppointment) => {
  formError.value = ''
  if (s) {
    form.editing = true; form.id = s.id
    form.elderlyId = s.elderlyId || 0
    form.serviceType = serviceLabelToType(s.service)
    form.appointmentTime = s.scheduledAt && s.scheduledAt !== '接口同步' ? s.scheduledAt.replace(' ', 'T').slice(0, 16) : formatLocalDateTime()
    form.doctorName = s.assignee !== '待分配' ? s.assignee : ''
    form.description = s.description || ''
  } else {
    Object.assign(form, emptyForm())
    form.appointmentTime = formatLocalDateTime()
  }
  modalOpen.value = true
}

const submitForm = async () => {
  if (!form.elderlyId) { formError.value = '请选择老人'; return }
  if (!form.appointmentTime) { formError.value = '请选择预约时间'; return }
  submitting.value = true; formError.value = ''
  const appointmentTime = form.appointmentTime.replace('T', ' ') + ':00'
  const payload = {
    elderlyId: form.elderlyId,
    serviceType: form.serviceType,
    appointmentTime,
    doctorName: form.doctorName || undefined,
    description: form.description || undefined,
  }
  try {
    if (form.editing && form.id) {
      await operations.updateAppointmentById(form.id, payload)
    } else {
      await operations.createAppointment(payload)
    }
    modalOpen.value = false
    feedback.value = form.editing ? '预约已更新' : '预约已创建'
    setTimeout(() => { feedback.value = '' }, 3000)
  } catch (e) {
    formError.value = e instanceof ApiError ? e.message : '保存失败'
  } finally { submitting.value = false }
}

const doComplete = async (s: ServiceAppointment) => {
  try {
    await operations.updateAppointmentStatus(s.id, 'completed')
    feedback.value = `"${s.elderName}"的服务已完成`
    setTimeout(() => { feedback.value = '' }, 3000)
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '操作失败'
  }
}

const doCancel = async (s: ServiceAppointment) => {
  const reason = window.prompt('取消原因（可选）：')
  if (reason === null) return // user hit cancel on prompt
  try {
    await operations.updateAppointmentStatus(s.id, 'cancelled', reason || undefined)
    feedback.value = '预约已取消'
    setTimeout(() => { feedback.value = '' }, 3000)
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '操作失败'
  }
}

const handleDelete = async (s: ServiceAppointment) => {
  if (!window.confirm('确定删除该预约？')) return
  try {
    await operations.deleteAppointmentById(s.id)
    feedback.value = '已删除'
    setTimeout(() => { feedback.value = '' }, 3000)
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '删除失败'
  }
}
</script>
