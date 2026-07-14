<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">Assessment Reports</span>
      <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">评估报告</h1>
      <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">为老人定期出具健康评估，跟踪风险和干预建议。</p>
    </div>

    <div class="mb-4 flex items-center justify-end gap-2">
      <button v-if="canCreate" type="button" class="inline-flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700" @click="openModal()"><Plus class="size-4" />新增报告</button>
      <button type="button" :disabled="loading" class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="load">刷新</button>
    </div>

    <p v-if="error" class="mb-4 w-fit rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300">{{ error }}</p>

    <section class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
      <div class="flex flex-wrap items-center gap-3">
        <input v-model="keyword" type="search" placeholder="搜索标题..." class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs sm:w-[260px] dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
        <select v-model="riskFilter" class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="全部">全部等级</option><option value="low">低风险</option><option value="medium">中风险</option><option value="high">高风险</option></select>
      </div>
      <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
        <table class="min-w-full">
          <thead><tr class="border-t border-gray-100 dark:border-gray-800"><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">标题</th><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">风险</th><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">时间</th><th class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th></tr></thead>
          <tbody>
            <tr v-for="r in filtered" :key="r.id" class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]">
              <td class="py-3 pr-4 whitespace-nowrap font-medium text-gray-800 text-theme-sm dark:text-white/90">{{ r.reportTitle || '—' }}</td>
              <td class="py-3 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ r.elderlyName }}</td>
              <td class="py-3 pr-4"><span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="r.riskLevel === 'high' ? 'bg-error-50 text-error-700' : r.riskLevel === 'medium' ? 'bg-warning-50 text-warning-700' : 'bg-success-50 text-success-700'">{{ riskLabel(r.riskLevel) }}</span></td>
              <td class="py-3 pr-4 text-theme-sm text-gray-500 dark:text-gray-400">{{ r.reportTime || '—' }}</td>
              <td class="py-3"><div class="flex gap-2"><button v-if="canUpdate" type="button" class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-theme-xs font-medium text-gray-700 hover:bg-gray-50" @click="openModal(r)">编辑</button><button v-if="canDelete" type="button" class="rounded-lg border border-error-100 bg-error-50 px-2 py-1 text-theme-xs font-medium text-error-700 hover:bg-error-100" @click="handleDelete(r)">删除</button></div></td>
            </tr>
            <tr v-if="!loading && filtered.length === 0"><td colspan="5" class="py-8 text-center text-theme-sm text-gray-500">暂无评估报告</td></tr>
          </tbody>
        </table>
      </div>
    </section>

    <div v-if="modalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="modalOpen = false">
      <div class="w-full max-w-lg rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">{{ form.editing ? '编辑报告' : '新增报告' }}</h3>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="submitForm">
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">报告标题</span><input v-model="form.reportTitle" type="text" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">选择老人</span>
            <select v-model.number="form.elderlyId" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90">
              <option :value="0" disabled>请选择老人</option>
              <option v-for="elder in elderlyOptions" :key="elder.id" :value="elder.id">{{ elderlyLabel(elder) }}</option>
              <option v-if="form.elderlyId && !elderlyOptions.some(e => e.id === form.elderlyId)" :value="form.elderlyId">老人ID {{ form.elderlyId }}</option>
            </select>
            <p v-if="elderlyLoading" class="mt-1 text-theme-xs text-gray-400">加载老人列表中…</p>
            <p v-if="elderlyError" class="mt-1 text-theme-xs text-error-500">{{ elderlyError }}</p>
          </label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">风险等级</span><select v-model="form.riskLevel" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="low">低风险</option><option value="medium">中风险</option><option value="high">高风险</option></select></label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">评估摘要</span><textarea v-model="form.summary" rows="3" required class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"></textarea></label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">建议</span><textarea v-model="form.suggestion" rows="3" class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"></textarea></label>
          <p v-if="formError" class="text-theme-xs text-error-600 sm:col-span-2">{{ formError }}</p>
          <div class="flex justify-end gap-2 sm:col-span-2"><button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700" @click="modalOpen = false">取消</button><button type="submit" :disabled="submitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white disabled:opacity-60">{{ submitting ? '保存中…' : '保存' }}</button></div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { ApiError, getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { listReports, createReport, updateReport, deleteReport, type AssessmentReportApi, type ReportPayload, type RiskLevel } from '@/api/reports'
import { listElderly, type ElderlyApiRecord } from '@/api/elderly'

const roleName = getStoredUser()?.roleName ?? null
const canCreate = canUseAction(roleName, 'reports:create')
const canUpdate = canUseAction(roleName, 'reports:update')
const canDelete = canUseAction(roleName, 'reports:delete')

const records = ref<AssessmentReportApi[]>([])
const loading = ref(false)
const error = ref('')
const keyword = ref('')
const riskFilter = ref<string>('全部')

const elderlyOptions = ref<ElderlyApiRecord[]>([])
const elderlyLoading = ref(false)
const elderlyError = ref('')

const fetchElderly = async () => {
  elderlyLoading.value = true; elderlyError.value = ''
  try {
    const r = await listElderly({ page: 1, size: 200 })
    elderlyOptions.value = r.records
  } catch (e) {
    elderlyError.value = '老人档案加载失败，请刷新后重试'
  } finally { elderlyLoading.value = false }
}

const elderlyLabel = (e: ElderlyApiRecord) => {
  const parts = [`${e.elderlyName || '未命名'} / ID ${e.id}`]
  if (e.age != null) parts.push(`${e.age}岁`)
  if (e.riskLevel) parts.push(e.riskLevel === 'high' ? '高风险' : e.riskLevel === 'medium' ? '中风险' : '低风险')
  return parts.join(' / ')
}

const load = async () => { loading.value = true; error.value = ''; try { const r = await listReports({ page:1,size:100 }); records.value = r.records } catch (e) { error.value = e instanceof ApiError ? e.message : '加载失败' } finally { loading.value = false } }
onMounted(() => { void load(); void fetchElderly() })

const filtered = computed(() => records.value.filter(r => {
  const kw = keyword.value.trim().toLowerCase()
  return (!kw || r.reportTitle?.toLowerCase().includes(kw)) && (riskFilter.value === '全部' || r.riskLevel === riskFilter.value)
}))

const riskLabel = (l?: RiskLevel) => l === 'high' ? '高风险' : l === 'medium' ? '中风险' : '低风险'

const modalOpen = ref(false)
const submitting = ref(false)
const formError = ref('')
const form = reactive<ReportPayload & { editing: boolean; id?: number }>({ editing: false, elderlyId: 0, reportTitle: '', riskLevel: 'medium', summary: '', suggestion: '', reportTime: new Date().toISOString().slice(0,10) })

const openModal = (r?: AssessmentReportApi) => {
  formError.value = ''
  if (r) { form.editing = true; form.id = r.id; form.elderlyId = r.elderlyId || 0; form.reportTitle = r.reportTitle || ''; form.riskLevel = r.riskLevel || 'medium'; form.summary = r.summary || ''; form.suggestion = r.suggestion || ''; form.reportTime = r.reportTime || '' }
  else { form.editing = false; form.id = undefined; form.elderlyId = 0; form.reportTitle = ''; form.riskLevel = 'medium'; form.summary = ''; form.suggestion = ''; form.reportTime = new Date().toISOString().slice(0,10) }
  modalOpen.value = true
}

const submitForm = async () => {
  if (!form.elderlyId) { formError.value = '请选择老人'; return }
  submitting.value = true; formError.value = ''
  try {
    if (form.editing && form.id) await updateReport(form.id, { elderlyId: form.elderlyId, reportTitle: form.reportTitle, riskLevel: form.riskLevel, summary: form.summary, suggestion: form.suggestion, reportTime: form.reportTime })
    else await createReport({ elderlyId: form.elderlyId, reportTitle: form.reportTitle, riskLevel: form.riskLevel, summary: form.summary, suggestion: form.suggestion, reportTime: form.reportTime })
    modalOpen.value = false; await load()
  } catch (e) { formError.value = e instanceof ApiError ? e.message : '保存失败' }
  finally { submitting.value = false }
}

const handleDelete = async (r: AssessmentReportApi) => {
  if (!window.confirm('确定删除？')) return
  try { await deleteReport(r.id); await load() } catch (e) { error.value = e instanceof ApiError ? e.message : '删除失败' }
}
</script>
