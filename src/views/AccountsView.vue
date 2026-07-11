<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Account Management
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          账号管理
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          审核医生、护士的注册申请，维护现有账号的启用状态，必要时清理异常账号。
        </p>
      </div>
      <button
        type="button"
        class="inline-flex w-fit items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
        :disabled="loading"
        @click="loadUsers"
      >
        <RotateCw :class="['size-4', loading ? 'animate-spin' : '']" />
        刷新
      </button>
    </div>

    <p
      v-if="feedback"
      class="mb-4 w-fit rounded-lg border px-3 py-2 text-theme-sm"
      :class="feedbackTone === 'success' ? 'border-success-200 bg-success-50 text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300' : 'border-error-200 bg-error-50 text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300'"
    >
      {{ feedback }}
    </p>

    <div class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4 md:gap-6">
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
    </div>

    <div class="mt-6 flex flex-wrap items-center justify-end gap-2">
      <button
        type="button"
        class="inline-flex items-center justify-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
        @click="openAccountModal()"
      >
        <Plus class="size-4" />
        新增账号
      </button>
    </div>

    <section class="mt-6 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
      <div class="flex flex-col gap-4 xl:flex-row xl:items-end xl:justify-between">
        <div>
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
            账号列表
          </h2>
          <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            当前筛选 {{ filteredUsers.length }} 个账号，共 {{ users.length }} 个
          </p>
        </div>
        <div class="grid gap-3 sm:grid-cols-[1fr_140px_140px] xl:w-[560px]">
          <label class="sr-only" for="account-search">搜索账号</label>
          <input
            id="account-search"
            v-model="keyword"
            type="search"
            placeholder="搜索用户名 / 姓名 / 联系电话"
            class="h-11 rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 dark:placeholder:text-white/30"
          />
          <label class="sr-only" for="account-role">角色</label>
          <select
            id="account-role"
            v-model="roleFilter"
            class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option value="全部角色">全部角色</option>
            <option v-for="r in roleOptions" :key="r.id" :value="r.name">{{ r.name }}</option>
          </select>
          <label class="sr-only" for="account-status">状态</label>
          <select
            id="account-status"
            v-model="statusFilter"
            class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option value="全部状态">全部状态</option>
            <option value="pending">待审核</option>
            <option value="enabled">已启用</option>
            <option value="disabled">已禁用</option>
          </select>
        </div>
      </div>

      <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
        <table class="min-w-full">
          <thead>
            <tr class="border-t border-gray-100 dark:border-gray-800">
              <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">账号</th>
              <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">角色</th>
              <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">状态</th>
              <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">联系电话</th>
              <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">创建时间</th>
              <th class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="user in filteredUsers"
              :key="user.id"
              class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
            >
              <td class="py-4 pr-4 whitespace-nowrap">
                <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
                  {{ user.realName || '—' }}
                </p>
                <p class="text-theme-xs text-gray-500 dark:text-gray-400">@{{ user.username }}</p>
              </td>
              <td class="py-4 pr-4 text-theme-sm text-gray-700 dark:text-gray-300">
                {{ user.roleName || `角色 ${user.roleId ?? '?'}` }}
              </td>
              <td class="py-4 pr-4 whitespace-nowrap">
                <span
                  class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                  :class="statusClassMap[user.status || 'pending']"
                >
                  {{ statusLabel[user.status || 'pending'] }}
                </span>
              </td>
              <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                {{ user.phoneNumber || '—' }}
              </td>
              <td class="py-4 pr-4 text-theme-sm text-gray-500 dark:text-gray-400">
                {{ user.createTime || '—' }}
              </td>
              <td class="py-4">
                <div class="flex flex-wrap items-center gap-2">
                  <button
                    type="button"
                    class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                    @click="openAccountModal(user, 'view')"
                  >
                    详情
                  </button>
                  <button
                    type="button"
                    class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                    :disabled="actingId === user.id"
                    @click="openAccountModal(user, 'edit')"
                  >
                    编辑
                  </button>
                  <button
                    v-if="user.status === 'pending'"
                    type="button"
                    class="rounded-lg bg-brand-600 px-3 py-1.5 text-theme-xs font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-60"
                    :disabled="actingId === user.id"
                    @click="approve(user.id)"
                  >
                    通过
                  </button>
                  <button
                    v-if="user.status === 'pending'"
                    type="button"
                    class="rounded-lg border border-error-200 bg-error-50 px-3 py-1.5 text-theme-xs font-medium text-error-700 shadow-theme-xs hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300 disabled:cursor-not-allowed disabled:opacity-60"
                    :disabled="actingId === user.id"
                    @click="reject(user.id)"
                  >
                    拒绝
                  </button>
                  <button
                    v-if="user.status === 'enabled'"
                    type="button"
                    class="rounded-lg border border-warning-200 bg-warning-50 px-3 py-1.5 text-theme-xs font-medium text-warning-700 shadow-theme-xs hover:bg-warning-100 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300 disabled:cursor-not-allowed disabled:opacity-60"
                    :disabled="actingId === user.id"
                    @click="disable(user.id)"
                  >
                    禁用
                  </button>
                  <button
                    v-if="user.status === 'disabled'"
                    type="button"
                    class="rounded-lg border border-success-200 bg-success-50 px-3 py-1.5 text-theme-xs font-medium text-success-700 shadow-theme-xs hover:bg-success-100 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300 disabled:cursor-not-allowed disabled:opacity-60"
                    :disabled="actingId === user.id"
                    @click="enable(user.id)"
                  >
                    启用
                  </button>
                  <button
                    type="button"
                    class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-error-600 shadow-theme-xs hover:bg-error-50 dark:border-gray-700 dark:bg-gray-900 dark:text-error-400 dark:hover:bg-error-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                    :disabled="actingId === user.id"
                    @click="confirmDelete(user)"
                  >
                    删除
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && filteredUsers.length === 0">
              <td colspan="6" class="py-8 text-center text-theme-sm text-gray-500 dark:text-gray-400">
                没有匹配的账号
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <div
      v-if="accountModalOpen"
      class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4"
      @click.self="closeAccountModal"
    >
      <div class="w-full max-w-md rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">
          {{ accountModalTitle }}
        </h3>
        <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
          {{ accountModal.mode === 'view' ? '只读查看账号信息。' : accountModal.mode === 'edit' ? '修改账号信息，密码留空表示不修改。' : '创建新的工作人员账号。' }}
        </p>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="submitAccountModal">
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">登录账号</span>
            <input
              v-model="accountModal.username"
              type="text"
              required
              :disabled="accountModal.mode !== 'create'"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 disabled:opacity-60"
            />
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">真实姓名</span>
            <input
              v-model="accountModal.realName"
              type="text"
              required
              :disabled="accountModal.mode === 'view'"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 disabled:opacity-60"
            />
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">联系电话</span>
            <input
              v-model="accountModal.phoneNumber"
              type="text"
              :disabled="accountModal.mode === 'view'"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 disabled:opacity-60"
            />
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
              密码
              <span v-if="accountModal.mode === 'edit'" class="ml-1 text-theme-xs text-gray-500">（留空不修改）</span>
            </span>
            <input
              v-model="accountModal.password"
              type="password"
              :required="accountModal.mode === 'create'"
              :disabled="accountModal.mode === 'view'"
              placeholder="至少 6 位"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 disabled:opacity-60"
            />
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">角色</span>
            <select
              v-model.number="accountModal.roleId"
              :disabled="accountModal.mode === 'view'"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 disabled:opacity-60"
            >
              <option v-for="r in roleOptions" :key="r.id" :value="r.id">{{ r.name }}</option>
            </select>
          </label>
          <label class="block sm:col-span-1">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">状态</span>
            <select
              v-model="accountModal.status"
              :disabled="accountModal.mode === 'view'"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90 disabled:opacity-60"
            >
              <option value="pending">待审核</option>
              <option value="enabled">已启用</option>
              <option value="disabled">已禁用</option>
            </select>
          </label>
          <p
            v-if="accountModalError"
            class="rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300 sm:col-span-2"
          >
            {{ accountModalError }}
          </p>
          <div class="flex flex-wrap items-center justify-end gap-2 sm:col-span-2">
            <button
              type="button"
              class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
              @click="closeAccountModal"
            >
              {{ accountModal.mode === 'view' ? '关闭' : '取消' }}
            </button>
            <button
              v-if="accountModal.mode !== 'view'"
              type="submit"
              :disabled="accountModalSubmitting"
              class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {{ accountModalSubmitting ? '保存中…' : accountModal.mode === 'edit' ? '保存修改' : '创建账号' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { RotateCw, Plus } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { deleteUser, listUsers, updateUserStatus, createUser, updateUser, getUser, type UserApiRecord, type UserStatus } from '@/api/users'
import { ApiError } from '@/api/http'
import { staffRoleOptions } from '@/config/roles'

const users = ref<UserApiRecord[]>([])
const loading = ref(false)
const actingId = ref<number | null>(null)
const keyword = ref('')
const roleFilter = ref<string>('全部角色')
const statusFilter = ref<UserStatus | '全部状态'>('全部状态')
const feedback = ref('')
const feedbackTone = ref<'success' | 'error'>('success')

const roleOptions = staffRoleOptions

const statusLabel: Record<UserStatus, string> = {
  pending: '待审核',
  enabled: '已启用',
  disabled: '已禁用',
}

const statusClassMap: Record<UserStatus, string> = {
  pending: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  enabled: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  disabled: 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300',
}

const filteredUsers = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  return users.value.filter((u) => {
    const matchKeyword =
      !term ||
      u.username?.toLowerCase().includes(term) ||
      u.realName?.toLowerCase().includes(term) ||
      u.phoneNumber?.toLowerCase().includes(term)
    const matchRole =
      roleFilter.value === '全部角色' || u.roleName === roleFilter.value
    const matchStatus =
      statusFilter.value === '全部状态' || u.status === statusFilter.value
    return matchKeyword && matchRole && matchStatus
  })
})

const stats = computed(() => {
  const total = users.value.length
  const pending = users.value.filter((u) => u.status === 'pending').length
  const enabled = users.value.filter((u) => u.status === 'enabled').length
  const disabled = users.value.filter((u) => u.status === 'disabled').length
  return [
    { label: '账号总数', value: total, note: '平台所有工作人员' },
    { label: '待审核', value: pending, note: '需要管理员处理' },
    { label: '已启用', value: enabled, note: '可正常登录使用' },
    { label: '已禁用', value: disabled, note: '暂停登录权限' },
  ]
})

const loadUsers = async () => {
  loading.value = true
  try {
    const result = await listUsers({ page: 1, size: 100 })
    users.value = result.records
  } catch (err) {
    users.value = []
    feedback.value = err instanceof ApiError ? err.message : '账号列表接口暂不可用'
    feedbackTone.value = 'error'
  } finally {
    loading.value = false
  }
}

const setStatus = async (id: number, status: UserStatus, successText: string) => {
  actingId.value = id
  feedback.value = ''
  try {
    await updateUserStatus(id, status)
    const target = users.value.find((u) => u.id === id)
    if (target) target.status = status
    feedbackTone.value = 'success'
    feedback.value = successText
  } catch (err) {
    feedbackTone.value = 'error'
    feedback.value = err instanceof ApiError ? err.message : '状态更新失败，请稍后重试'
  } finally {
    actingId.value = null
  }
}

const approve = (id: number) => setStatus(id, 'enabled', '已通过该账号的注册申请')
const reject = (id: number) => setStatus(id, 'disabled', '已拒绝该账号的注册申请')
const enable = (id: number) => setStatus(id, 'enabled', '已重新启用该账号')
const disable = (id: number) => setStatus(id, 'disabled', '已禁用该账号')

const confirmDelete = async (user: UserApiRecord) => {
  const label = user.realName || user.username || `#${user.id}`
  // 简单确认，避免误删
  if (!window.confirm(`确定要删除账号「${label}」吗？该操作不可恢复。`)) return
  actingId.value = user.id
  feedback.value = ''
  try {
    await deleteUser(user.id)
    users.value = users.value.filter((u) => u.id !== user.id)
    feedbackTone.value = 'success'
    feedback.value = `已删除账号「${label}」`
  } catch (err) {
    feedbackTone.value = 'error'
    feedback.value = err instanceof ApiError ? err.message : '删除失败，请稍后重试'
  } finally {
    actingId.value = null
  }
}

// 账号详情/新增/编辑 弹窗
const accountModalOpen = ref(false)
const accountModalSubmitting = ref(false)
const accountModalError = ref('')
const accountModal = reactive({
  mode: 'create' as 'create' | 'edit' | 'view',
  editingId: null as number | null,
  username: '',
  realName: '',
  phoneNumber: '',
  password: '',
  roleId: 1,
  status: 'pending' as UserStatus,
})

const accountModalTitle = computed(() => {
  if (accountModal.mode === 'view') return '账号详情'
  if (accountModal.mode === 'edit') return '编辑账号'
  return '新增账号'
})

const openAccountModal = async (user?: UserApiRecord, mode: 'create' | 'edit' | 'view' = 'create') => {
  accountModalError.value = ''
  if (!user) {
    accountModal.mode = 'create'
    accountModal.editingId = null
    accountModal.username = ''
    accountModal.realName = ''
    accountModal.phoneNumber = ''
    accountModal.password = ''
    accountModal.roleId = roleOptions[0]?.id ?? 1
    accountModal.status = 'pending'
  } else {
    accountModal.mode = mode
    accountModal.editingId = user.id
    accountModal.username = user.username ?? ''
    accountModal.realName = user.realName ?? ''
    accountModal.phoneNumber = user.phoneNumber ?? ''
    accountModal.password = ''
    // 用 roleName 反查 id
    const matched = roleOptions.find((r) => r.name === user.roleName)
    accountModal.roleId = matched?.id ?? user.roleId ?? 1
    accountModal.status = user.status ?? 'pending'
    // 编辑/详情模式再请求详情拿最新字段
    if (mode === 'edit' || mode === 'view') {
      try {
        const detail = await getUser(user.id)
        accountModal.username = detail.username ?? accountModal.username
        accountModal.realName = detail.realName ?? accountModal.realName
        accountModal.phoneNumber = detail.phoneNumber ?? accountModal.phoneNumber
        const matchedDetail = roleOptions.find((r) => r.name === detail.roleName)
        accountModal.roleId = matchedDetail?.id ?? detail.roleId ?? accountModal.roleId
        accountModal.status = detail.status ?? accountModal.status
      } catch {
        // 详情失败就保持列表字段
      }
    }
  }
  accountModalOpen.value = true
}

const closeAccountModal = () => {
  if (accountModalSubmitting.value) return
  accountModalOpen.value = false
}

const submitAccountModal = async () => {
  accountModalError.value = ''
  if (accountModal.password && accountModal.password.length < 6) {
    accountModalError.value = '密码至少需要 6 位'
    return
  }
  accountModalSubmitting.value = true
  try {
    if (accountModal.mode === 'edit' && accountModal.editingId !== null) {
      const payload = {
        username: accountModal.username.trim(),
        realName: accountModal.realName.trim(),
        phoneNumber: accountModal.phoneNumber.trim(),
        roleId: accountModal.roleId,
        status: accountModal.status,
        ...(accountModal.password ? { password: accountModal.password } : {}),
      }
      await updateUser(accountModal.editingId, payload as Parameters<typeof updateUser>[1])
      feedback.value = `已更新账号：${accountModal.realName}`
    } else {
      await createUser({
        username: accountModal.username.trim(),
        password: accountModal.password,
        realName: accountModal.realName.trim(),
        phoneNumber: accountModal.phoneNumber.trim(),
        roleId: accountModal.roleId,
        status: accountModal.status,
      })
      feedback.value = `已创建账号：${accountModal.realName}`
    }
    await loadUsers()
    accountModalOpen.value = false
  } catch (err) {
    accountModalError.value =
      err instanceof ApiError ? err.message : '保存失败，请稍后重试'
  } finally {
    accountModalSubmitting.value = false
  }
}

onMounted(() => {
  void loadUsers()
})
</script>
