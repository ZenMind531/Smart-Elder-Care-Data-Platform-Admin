<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
        System Settings
      </span>
      <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
        系统设置
      </h1>
      <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
        配置机构信息、工作人员角色权限、通知规则和基础字典，保证后台管理权限清晰。
      </p>
    </div>

    <p
      v-if="feedback"
      class="mb-4 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
    >
      {{ feedback }}
    </p>

    <p
      v-if="operations.loading || operations.error"
      class="mb-4 w-fit rounded-lg border px-3 py-2 text-theme-sm"
      :class="operations.error ? 'border-warning-200 bg-warning-50 text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300' : 'border-brand-200 bg-brand-50 text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300'"
    >
      {{ operations.loading ? '正在同步角色接口...' : `<span>角色接口暂不可用：</span>${operations.error}` }}
    </p>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-5">
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">机构资料</h2>
        <div class="mt-5 grid gap-4">
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">机构名称</span>
            <input
              v-model="organization.name"
              type="text"
              :disabled="!canUpdateSettings"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">联系电话</span>
            <input
              v-model="organization.phone"
              type="text"
              :disabled="!canUpdateSettings"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">机构地址</span>
            <textarea
              v-model="organization.address"
              rows="3"
              :disabled="!canUpdateSettings"
              class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            ></textarea>
          </label>
          <button
            v-if="canUpdateSettings"
            type="button"
            class="w-fit rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
            @click="saveOrganization"
          >
            保存机构资料
          </button>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-7">
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">角色权限</h2>
            <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              维护平台可用的角色及其启用状态。
            </p>
          </div>
          <button
            v-if="canManageRoles"
            type="button"
            class="inline-flex w-fit items-center justify-center gap-2 rounded-lg bg-brand-600 px-3 py-1.5 text-theme-xs font-medium text-white shadow-theme-xs hover:bg-brand-700"
            @click="openRoleModal()"
          >
            <Plus class="size-4" />
            新增角色
          </button>
        </div>
        <div class="mt-5 space-y-3">
          <article
            v-for="role in operations.roles"
            :key="role.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p class="font-semibold text-theme-sm text-gray-800 dark:text-white/90">{{ role.name }}</p>
                <p class="mt-1 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
                  {{ role.scope }}
                </p>
              </div>
              <div class="flex flex-wrap items-center gap-2">
                <span class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">{{ role.users }} 人</span>
                <span
                  class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                  :class="role.enabled ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400' : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300'"
                >
                  {{ role.enabled ? '启用' : '停用' }}
                </span>
                <button
                  v-if="canManageRoles"
                  type="button"
                  class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                  @click="openRoleModal(role)"
                >
                  编辑
                </button>
                <button
                  v-if="canManageRoles"
                  type="button"
                  class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                  @click="toggleRole(role)"
                >
                  {{ role.enabled ? '停用' : '启用' }}
                </button>
                <button
                  v-if="canManageRoles"
                  type="button"
                  class="rounded-lg border border-error-200 bg-error-50 px-3 py-1.5 text-theme-xs font-medium text-error-700 shadow-theme-xs hover:bg-error-100 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
                  @click="deleteRoleById(role)"
                >
                  删除
                </button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-7">
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">通知规则</h2>
        <div class="mt-5 space-y-3">
          <article
            v-for="rule in operations.notificationRules"
            :key="rule.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p class="font-semibold text-theme-sm text-gray-800 dark:text-white/90">{{ rule.name }}</p>
                <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
                  {{ rule.channel }} · {{ rule.target }}
                </p>
              </div>
              <div class="flex flex-wrap items-center gap-3">
                <span
                  class="w-fit rounded-full px-2 py-0.5 text-theme-xs font-medium"
                  :class="rule.enabled ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400' : 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300'"
                >
                  {{ rule.enabled ? '启用' : '停用' }}
                </span>
                <button
                  v-if="canUpdateSettings"
                  type="button"
                  class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                  @click="toggleNotificationRule(rule)"
                >
                  {{ rule.enabled ? '停用' : '启用' }}
                </button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section class="col-span-12 rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08] xl:col-span-5">
        <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">基础字典</h2>
        <div class="mt-5 grid gap-3">
          <div v-for="dict in dictionaries" :key="dict.name" class="rounded-xl bg-white p-4 dark:bg-white/[0.03]">
            <div class="flex items-center justify-between gap-3">
              <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ dict.name }}</p>
              <span class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">{{ dict.count }} 项</span>
            </div>
            <p class="mt-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">{{ dict.desc }}</p>
          </div>
        </div>
      </section>
    </div>

    <div
      v-if="roleModalOpen"
      class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4"
      @click.self="closeRoleModal"
    >
      <div class="w-full max-w-md rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">
          {{ roleModal.editing ? '编辑角色' : '新增角色' }}
        </h3>
        <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
          角色编码用于后端权限识别，创建后建议保持稳定。
        </p>
        <form class="mt-4 space-y-4" @submit.prevent="submitRoleModal">
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">角色名称</span>
            <input
              v-model="roleModal.roleName"
              type="text"
              required
              placeholder="例如 药剂师"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">角色编码</span>
            <input
              v-model="roleModal.roleCode"
              type="text"
              required
              :disabled="roleModal.editing"
              placeholder="例如 pharmacist"
              class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 disabled:opacity-60"
            />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">角色描述</span>
            <textarea
              v-model="roleModal.description"
              rows="3"
              placeholder="例如 负责药品发放和医嘱核对"
              class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            ></textarea>
          </label>
          <p
            v-if="roleModalError"
            class="rounded-lg border border-error-200 bg-error-50 px-3 py-2 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
          >
            {{ roleModalError }}
          </p>
          <div class="flex flex-wrap items-center justify-end gap-2">
            <button
              type="button"
              class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
              @click="closeRoleModal"
            >
              取消
            </button>
            <button
              type="submit"
              :disabled="roleModalSubmitting"
              class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {{ roleModalSubmitting ? '保存中…' : roleModal.editing ? '保存修改' : '创建角色' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div v-if="pwdModalOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="pwdModalOpen = false">
      <div class="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-lg dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">修改密码</h3>
        <form class="mt-4 space-y-3" @submit.prevent="submitPwd">
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">旧密码</span>
            <input v-model="pwd.oldPassword" type="password" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">新密码</span>
            <input v-model="pwd.newPassword" type="password" required placeholder="至少 6 位" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <label class="block">
            <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">确认新密码</span>
            <input v-model="pwd.confirmPassword" type="password" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
          </label>
          <p v-if="pwdError" class="text-theme-xs text-error-600 dark:text-error-400">{{ pwdError }}</p>
          <p v-if="pwdOk" class="text-theme-xs text-success-600 dark:text-success-400">{{ pwdOk }}</p>
          <div class="flex justify-end gap-2">
            <button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-theme-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300" @click="pwdModalOpen = false">取消</button>
            <button type="submit" :disabled="pwdSubmitting" class="rounded-lg bg-brand-600 px-4 py-2 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700 disabled:opacity-60">{{ pwdSubmitting ? '修改中…' : '确认修改' }}</button>
          </div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Plus } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getStoredUser, ApiError } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import type { NotificationRule, RoleRecord } from '@/stores/operations'
import { createRole, deleteRole as deleteRoleApi, updateRole as updateRoleApi } from '@/api/roles'
import { updatePassword } from '@/api/auth'

const operations = useOperationsStore()
const currentUser = getStoredUser()
const canUpdateSettings = canUseAction(currentUser?.roleName, 'settings:update')
const canManageRoles = canUseAction(currentUser?.roleName, 'roles:manage')
const feedback = ref('')

// 角色弹窗状态
const roleModalOpen = ref(false)
const roleModalSubmitting = ref(false)
const roleModalError = ref('')
const roleModal = reactive({
  editing: false,
  editingId: null as number | null,
  roleName: '',
  roleCode: '',
  description: '',
})

const openRoleModal = (role?: RoleRecord) => {
  roleModalError.value = ''
  if (role) {
    roleModal.editing = true
    roleModal.editingId = role.id
    roleModal.roleName = role.name
    roleModal.roleCode = '' // 后端没回 roleCode 时不允许编辑编码
    roleModal.description = role.scope
  } else {
    roleModal.editing = false
    roleModal.editingId = null
    roleModal.roleName = ''
    roleModal.roleCode = ''
    roleModal.description = ''
  }
  roleModalOpen.value = true
}

const closeRoleModal = () => {
  if (roleModalSubmitting.value) return
  roleModalOpen.value = false
}

const submitRoleModal = async () => {
  roleModalError.value = ''
  roleModalSubmitting.value = true
  try {
    if (roleModal.editing && roleModal.editingId !== null) {
      await updateRoleApi(roleModal.editingId, {
        roleName: roleModal.roleName.trim(),
        roleCode: roleModal.roleCode || '',
        description: roleModal.description.trim(),
      })
      feedback.value = `已更新角色：${roleModal.roleName}`
    } else {
      await createRole({
        roleName: roleModal.roleName.trim(),
        roleCode: roleModal.roleCode.trim(),
        description: roleModal.description.trim(),
      })
      feedback.value = `已创建角色：${roleModal.roleName}`
    }
    await operations.fetchRoles()
    roleModalOpen.value = false
  } catch (err) {
    roleModalError.value =
      err instanceof ApiError ? err.message : '保存失败，请稍后重试'
  } finally {
    roleModalSubmitting.value = false
  }
}

const deleteRoleById = async (role: RoleRecord) => {
  if (!window.confirm(`确定要删除角色「${role.name}」吗？`)) return
  try {
    await deleteRoleApi(role.id)
    feedback.value = `已删除角色：${role.name}`
    await operations.fetchRoles()
  } catch (err) {
    feedback.value =
      err instanceof ApiError ? err.message : '删除失败，请稍后重试'
  }
}

onMounted(() => {
  void operations.fetchRoles()
})

const organization = reactive({
  name: '青松智慧养老服务中心',
  phone: '0571-8888 6620',
  address: '杭州市西湖区文三路 188 号',
})

const dictionaries = [
  { name: '照护等级', count: 4, desc: '一级照护、二级照护、三级照护、特级照护。' },
  { name: '设备类型', count: 4, desc: '智能手环、睡眠监测垫、门磁传感器、紧急呼叫按钮。' },
  { name: '告警等级', count: 3, desc: '紧急、关注、普通，对应不同通知策略。' },
  { name: '服务项目', count: 4, desc: '健康咨询、康复训练、陪诊服务、上门护理。' },
]

const saveOrganization = () => {
  if (!canUpdateSettings) {
    feedback.value = '当前角色没有保存机构资料权限'
    return
  }

  feedback.value = `机构资料已保存：${organization.name}`
}

const toggleRole = (role: RoleRecord) => {
  if (!canUpdateSettings) {
    feedback.value = '当前角色没有维护角色权限'
    return
  }

  operations.toggleRole(role.id)
  feedback.value = `${role.name} 已${role.enabled ? '启用' : '停用'}`
}

const toggleNotificationRule = (rule: NotificationRule) => {
  if (!canUpdateSettings) {
    feedback.value = '当前角色没有维护通知规则权限'
    return
  }

  operations.toggleNotificationRule(rule.id)
  feedback.value = `${rule.name} 已${rule.enabled ? '启用' : '停用'}`
}

// 修改密码
const pwdModalOpen = ref(false)
const pwdSubmitting = ref(false)
const pwdError = ref('')
const pwdOk = ref('')
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const submitPwd = async () => {
  pwdError.value = ''
  pwdOk.value = ''
  if (pwd.newPassword.length < 6) { pwdError.value = '新密码至少 6 位'; return }
  if (pwd.newPassword !== pwd.confirmPassword) { pwdError.value = '两次密码不一致'; return }
  pwdSubmitting.value = true
  try {
    await updatePassword({
      oldPassword: pwd.oldPassword,
      newPassword: pwd.newPassword,
      confirmPassword: pwd.confirmPassword,
    })
    pwdOk.value = '密码修改成功'
    pwd.oldPassword = ''
    pwd.newPassword = ''
    pwd.confirmPassword = ''
    setTimeout(() => { pwdModalOpen.value = false; pwdOk.value = '' }, 1200)
  } catch (err) {
    pwdError.value = err instanceof ApiError ? err.message : '修改失败'
  } finally {
    pwdSubmitting.value = false
  }
}
</script>
