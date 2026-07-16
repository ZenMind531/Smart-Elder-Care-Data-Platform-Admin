<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">Care Records</span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">护理记录</h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">记录巡房、服药、康复训练、设备巡检和异常处置过程，形成可追溯的护理服务台账。</p>
      </div>
      <div class="flex items-center gap-2">
        <button v-if="canCreate" type="button" class="inline-flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700" @click="openModal()">
          <Plus class="size-4" />新增护理记录
        </button>
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
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">护理台账</h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">{{ records.length }} 条记录</p>
          </div>
          <div class="flex gap-2 flex-wrap">
            <input v-model="keyword" type="search" placeholder="搜索老人…" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs sm:w-[180px] dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
            <select v-model="careTypeFilter" class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option value="全部">全部类型</option>
              <option value="medication">服药</option>
              <option value="vital_signs">体征监测</option>
              <option value="cleaning">清洁护理</option>
              <option value="feeding">饮食护理</option>
              <option value="exercise">康复训练</option>
              <option value="other">其他</option>
            </select>
          </div>
        </div>

        <div v-if="!loading && filtered.length === 0" class="mt-8 py-12 text-center">
          <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-gray-100 dark:bg-gray-800">
            <ClipboardList class="size-8 text-gray-400" />
          </div>
          <p class="text-theme-sm text-gray-500 dark:text-gray-400">暂无护理记录</p>
        </div>

        <div class="mt-5 space-y-3">
          <article
            v-for="record in filtered"
            :key="record.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
              <div class="min-w-0 flex-1">
                <div class="flex flex-wrap items-center gap-2">
                  <span class="rounded-full bg-brand-50 px-2 py-0.5 text-theme-xs font-medium text-brand-700 dark:bg-brand-500/15 dark:text-brand-300">{{ record.type }}</span>
                  <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ record.time }}</span>
                </div>
                <h3 class="mt-2 font-semibold text-gray-800 text-theme-sm dark:text-white/90">{{ record.elderName }}</h3>
                <p class="mt-1 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">{{ record.content }}</p>
                <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400">记录人：{{ record.caregiver }}</p>
                <p v-if="record.remark" class="mt-1 text-theme-xs text-gray-400 dark:text-gray-500">备注：{{ record.remark }}</p>
              </div>
              <div class="flex items-center gap-2 shrink-0">
                <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="record.status === '已完成' ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400' : record.status === '进行中' ? 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300' : 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400'">{{ record.status }}</span>
                <button v-if="canUpdate" type="button" class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-theme-xs font-medium text-gray-700 hover:bg-gray-50" @click="openModal(record)">编辑</button>
                <button v-if="canDelete" type="button" class="rounded-lg border border-error-100 bg-error-50 px-2 py-1 text-theme-xs font-medium text-error-700 hover:bg-error-100" @click="handleDelete(record)">删除</button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">护理概览</h2>
          <div class="mt-5 grid grid-cols-2 gap-3">
            <div v-for="item in summary" :key="item.label" class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
              <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ item.label }}</p>
              <p class="mt-2 text-lg font-semibold text-gray-900 tabular-nums dark:text-white">{{ item.value }}</p>
            </div>
          </div>
        </section>

        <section class="rounded-[18px] border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]">
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">交班提醒</h2>
          <ul class="mt-4 space-y-3 text-theme-sm text-gray-600 dark:text-gray-300">
            <li>B-118 服药后需要复测血压。</li>
            <li>C-405 手环离线处置结果待补录。</li>
            <li>晚间巡房前确认 A 区夜间离床规则。</li>
          </ul>
        </section>
      </aside>
    </div>

    <!-- Modal -->
    <div v-if="modalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="modalOpen = false">
      <div class="w-full max-w-lg rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">{{ form.editing ? '编辑护理记录' : '新增护理记录' }}</h3>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="submitForm">
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">选择老人 *</span>
            <select v-model.number="form.elderlyId" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option :value="0" disabled>请选择老人</option>
              <option v-for="elder in elderlyOptions" :key="elder.id" :value="elder.id">{{ elderlyLabel(elder) }}</option>
              <option v-if="form.elderlyId && !elderlyOptions.some(e => e.id === form.elderlyId)" :value="form.elderlyId">老人ID {{ form.elderlyId }}</option>
            </select>
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">护理类型 *</span>
            <select v-model="form.careType" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option value="medication">服药</option>
              <option value="vital_signs">体征监测</option>
              <option value="cleaning">清洁护理</option>
              <option value="feeding">饮食护理</option>
              <option value="exercise">康复训练</option>
              <option value="other">其他</option>
            </select>
          </label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">护理内容 *</span>
            <textarea v-model="form.careContent" rows="3" required class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" placeholder="描述本次护理的具体内容…"></textarea>
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">护理时间 *</span>
            <input v-model="form.careTime" type="datetime-local" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">护理人员</span>
            <input v-model="form.caregiver" type="text" placeholder="选填" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">备注</span>
            <input v-model="form.remark" type="text" placeholder="选填" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
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
import { Plus, ClipboardList } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { ApiError, getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore, type CareRecord } from '@/stores/operations'
import { listElderly, type ElderlyApiRecord } from '@/api/elderly'
import type { CareType } from '@/api/care-records'

const roleName = getStoredUser()?.roleName ?? null
const canCreate = canUseAction(roleName, 'care:create')
const canUpdate = canUseAction(roleName, 'care:update')
const canDelete = canUseAction(roleName, 'care:delete')

const operations = useOperationsStore()
const loading = ref(false)
const error = ref('')
const feedback = ref('')
const keyword = ref('')
const careTypeFilter = ref<string>('全部')

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

const records = computed(() => operations.careRecords)

const filtered = computed(() =>
  records.value.filter((r) => {
    const kw = keyword.value.trim().toLowerCase()
    const matchKeyword = !kw || r.elderName.toLowerCase().includes(kw) || r.content.toLowerCase().includes(kw)
    const matchType = careTypeFilter.value === '全部' || r.type === careTypeLabelReverse(careTypeFilter.value)
    return matchKeyword && matchType
  }),
)

const careTypeLabelReverse = (t: string) => {
  const map: Record<string, string> = { 'medication': '服药', 'vital_signs': '体征监测', 'cleaning': '清洁护理', 'feeding': '饮食护理', 'exercise': '康复训练', 'other': '其他护理' }
  return map[t] || t
}

const summary = computed(() => [
  { label: '总记录', value: records.value.length },
  { label: '已完成', value: records.value.filter((r) => r.status === '已完成').length },
  { label: '进行中', value: records.value.filter((r) => r.status === '进行中').length },
  { label: '待补录', value: records.value.filter((r) => r.status === '待补录').length },
])

const load = async () => {
  loading.value = true; error.value = ''
  try { await operations.fetchCareRecords() } catch (e) { error.value = e instanceof ApiError ? e.message : '加载失败' }
  finally { loading.value = false }
}

onMounted(() => { void load(); void fetchElderly() })

// ── 表单 ──
const modalOpen = ref(false)
const submitting = ref(false)
const formError = ref('')

const emptyForm = () => ({
  editing: false as boolean,
  id: undefined as number | undefined,
  elderlyId: 0,
  careType: 'vital_signs' as CareType,
  careContent: '',
  careTime: '',
  caregiver: '',
  remark: '',
})

const form = reactive({ ...emptyForm() })

const pad = (v: number) => String(v).padStart(2, '0')
const formatLocalDateTime = (d = new Date()) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`

const careTypeToApi = (label: string): CareType => {
  const map: Record<string, CareType> = { '服药': 'medication', '体征监测': 'vital_signs', '清洁护理': 'cleaning', '饮食护理': 'feeding', '康复训练': 'exercise' }
  return map[label] || 'other'
}

const openModal = (r?: CareRecord) => {
  formError.value = ''
  if (r) {
    form.editing = true; form.id = r.id
    form.elderlyId = r.elderlyId || 0
    form.careType = careTypeToApi(r.type)
    form.careContent = r.content
    form.careTime = r.time && r.time !== '接口同步' ? r.time.replace(' ', 'T').slice(0, 16) : formatLocalDateTime()
    form.caregiver = r.caregiver !== '—' ? r.caregiver : ''
    form.remark = r.remark || ''
  } else {
    Object.assign(form, emptyForm())
    form.careTime = formatLocalDateTime()
  }
  modalOpen.value = true
}

const submitForm = async () => {
  if (!form.elderlyId) { formError.value = '请选择老人'; return }
  if (!form.careContent.trim()) { formError.value = '请输入护理内容'; return }
  if (!form.careTime) { formError.value = '请选择护理时间'; return }
  submitting.value = true; formError.value = ''
  const careTime = form.careTime.replace('T', ' ') + ':00'
  const payload = {
    elderlyId: form.elderlyId,
    careType: form.careType,
    careContent: form.careContent.trim(),
    careTime,
    caregiver: form.caregiver || undefined,
    remark: form.remark || undefined,
  }
  try {
    if (form.editing && form.id) {
      await operations.updateCareRecordById(form.id, payload)
    } else {
      await operations.createCareRecord(payload)
    }
    modalOpen.value = false
    feedback.value = form.editing ? '护理记录已更新' : '护理记录已保存'
    setTimeout(() => { feedback.value = '' }, 3000)
  } catch (e) {
    formError.value = e instanceof ApiError ? e.message : '保存失败'
  } finally { submitting.value = false }
}

const handleDelete = async (r: CareRecord) => {
  if (!window.confirm('确定删除该护理记录？')) return
  try {
    await operations.deleteCareRecordById(r.id)
    feedback.value = '已删除'
    setTimeout(() => { feedback.value = '' }, 3000)
  } catch (e) {
    error.value = e instanceof ApiError ? e.message : '删除失败'
  }
}
</script>
