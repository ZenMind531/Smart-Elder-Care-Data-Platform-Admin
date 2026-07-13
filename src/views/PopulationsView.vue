<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">Key Populations</span>
      <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">重点人群</h1>
      <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">管理慢性病、残疾、独居和空巢老人的重点关注名单。</p>
    </div>

    <div class="mb-4 flex items-center justify-end gap-2">
      <button v-if="canCreate" type="button" class="inline-flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700" @click="openModal()"><Plus class="size-4" />新增</button>
      <button type="button" :disabled="loading" class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="load">刷新</button>
    </div>

    <p v-if="error" class="mb-4 w-fit rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300">{{ error }}</p>

    <section class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
      <div class="flex flex-wrap items-center gap-3">
        <select v-model="typeFilter" class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="全部">全部类型</option><option value="chronic">慢性病</option><option value="disability">残疾</option><option value="solitary">独居</option><option value="empty_nesters">空巢</option></select>
        <select v-model="followFilter" class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="全部">全部状态</option><option value="pending">待跟进</option><option value="completed">已完成</option></select>
      </div>
      <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
        <table class="min-w-full">
          <thead><tr class="border-t border-gray-100 dark:border-gray-800"><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">类型</th><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">原因</th><th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">跟进</th><th class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th></tr></thead>
          <tbody>
            <tr v-for="p in filtered" :key="p.id" class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]">
              <td class="py-3 pr-4 font-medium text-gray-800 text-theme-sm dark:text-white/90">{{ p.elderlyName }}</td>
              <td class="py-3 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">{{ typeLabel(p.populationType) }}</td>
              <td class="py-3 pr-4 text-theme-sm text-gray-700 max-w-[260px]"><p class="truncate">{{ p.reason || '—' }}</p></td>
              <td class="py-3 pr-4"><span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="p.followStatus === 'completed' ? 'bg-success-50 text-success-700' : 'bg-warning-50 text-warning-700'">{{ p.followStatus === 'completed' ? '已完成' : '待跟进' }}</span></td>
              <td class="py-3"><div class="flex gap-2">
                <button v-if="canFollow && p.followStatus !== 'completed'" type="button" class="rounded-lg border border-success-200 bg-success-50 px-2 py-1 text-theme-xs font-medium text-success-700 hover:bg-success-100" :disabled="actingId === p.id" @click="complete(p.id)">完成跟进</button>
                <button v-if="canUpdate" type="button" class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-theme-xs font-medium text-gray-700 hover:bg-gray-50" @click="openModal(p)">编辑</button>
                <button v-if="canDelete" type="button" class="rounded-lg border border-error-100 bg-error-50 px-2 py-1 text-theme-xs font-medium text-error-700 hover:bg-error-100" @click="handleDelete(p)">删除</button>
              </div></td>
            </tr>
            <tr v-if="!loading && filtered.length === 0"><td colspan="5" class="py-8 text-center text-theme-sm text-gray-500">暂无重点人群记录</td></tr>
          </tbody>
        </table>
      </div>
    </section>

    <div v-if="modalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="modalOpen = false">
      <div class="w-full max-w-md rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold">{{ form.editing ? '编辑' : '新增' }}</h3>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="submitForm">
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">老人ID</span><input v-model.number="form.elderlyId" type="number" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">类型</span><select v-model="form.populationType" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="chronic">慢性病</option><option value="disability">残疾</option><option value="solitary">独居</option><option value="empty_nesters">空巢</option></select></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">跟进状态</span><select v-model="form.followStatus" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="pending">待跟进</option><option value="completed">已完成</option></select></label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium">原因</span><input v-model="form.reason" type="text" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium">备注</span><input v-model="form.remark" type="text" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <p v-if="formError" class="text-theme-xs text-error-600 sm:col-span-2">{{ formError }}</p>
          <div class="flex justify-end gap-2 sm:col-span-2"><button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm" @click="modalOpen = false">取消</button><button type="submit" :disabled="submitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white disabled:opacity-60">{{ submitting ? '保存中…' : '保存' }}</button></div>
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
import { listPopulations, createPopulation, updatePopulation, deletePopulation, updateFollowStatus, type KeyPopulationApi, type PopulationPayload, type PopulationType, type FollowStatus } from '@/api/populations'

const roleName = getStoredUser()?.roleName ?? null
const canCreate = canUseAction(roleName, 'populations:create')
const canUpdate = canUseAction(roleName, 'populations:update')
const canDelete = canUseAction(roleName, 'populations:delete')
const canFollow = canUseAction(roleName, 'populations:follow')

const records = ref<KeyPopulationApi[]>([])
const loading = ref(false); const error = ref(''); const typeFilter = ref<string>('全部'); const followFilter = ref<string>('全部'); const actingId = ref<number | null>(null)

const load = async () => { loading.value = true; error.value = ''; try { const r = await listPopulations({ page:1,size:100 }); records.value = r.records } catch (e) { error.value = e instanceof ApiError ? e.message : '加载失败' } finally { loading.value = false } }
onMounted(() => { void load() })

const filtered = computed(() => records.value.filter(p => (typeFilter.value === '全部' || p.populationType === typeFilter.value) && (followFilter.value === '全部' || p.followStatus === followFilter.value)))
const typeLabel = (t?: PopulationType) => t === 'chronic' ? '慢性病' : t === 'disability' ? '残疾' : t === 'solitary' ? '独居' : t === 'empty_nesters' ? '空巢' : '—'

const modalOpen = ref(false); const submitting = ref(false); const formError = ref('')
const form = reactive<PopulationPayload & { editing: boolean; id?: number }>({ editing: false, elderlyId: 0, populationType: 'chronic', reason: '', followStatus: 'pending', remark: '' })

const openModal = (p?: KeyPopulationApi) => {
  formError.value = ''
  if (p) { form.editing = true; form.id = p.id; form.elderlyId = p.elderlyId || 0; form.populationType = p.populationType || 'chronic'; form.reason = p.reason || ''; form.followStatus = p.followStatus || 'pending'; form.remark = p.remark || '' }
  else { form.editing = false; form.id = undefined; form.elderlyId = 0; form.populationType = 'chronic'; form.reason = ''; form.followStatus = 'pending'; form.remark = '' }
  modalOpen.value = true
}

const submitForm = async () => {
  submitting.value = true; formError.value = ''
  try {
    if (form.editing && form.id) await updatePopulation(form.id, { elderlyId: form.elderlyId, populationType: form.populationType, reason: form.reason, followStatus: form.followStatus, remark: form.remark })
    else await createPopulation({ elderlyId: form.elderlyId, populationType: form.populationType, reason: form.reason, followStatus: form.followStatus, remark: form.remark })
    modalOpen.value = false; await load()
  } catch (e) { formError.value = e instanceof ApiError ? e.message : '保存失败' } finally { submitting.value = false }
}

const complete = async (id: number) => { actingId.value = id; try { await updateFollowStatus(id, 'completed'); const p = records.value.find(x => x.id === id); if (p) p.followStatus = 'completed' } catch (e) { error.value = e instanceof ApiError ? e.message : '操作失败' } finally { actingId.value = null } }
const handleDelete = async (p: KeyPopulationApi) => { if (!window.confirm('确定删除？')) return; try { await deletePopulation(p.id); await load() } catch (e) { error.value = e instanceof ApiError ? e.message : '删除失败' } }
</script>
