<template>
  <div class="pb-24">
    <header class="sticky top-0 z-10 bg-[#f5f5f7]/80 backdrop-blur-md">
      <div class="flex items-center h-14 px-4 gap-3">
        <button @click="$router.back()" class="w-9 h-9 flex items-center justify-center -ml-1">
          <PhCaretLeft :size="22" class="text-primary" />
        </button>
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">{{ isEdit ? '编辑老人' : '注册新老人' }}</h1>
      </div>
    </header>

    <!-- Error / Success -->
    <div v-if="error" class="mx-5 mt-4 bg-error/10 text-error text-sm rounded-lg px-4 py-3">{{ error }}</div>
    <div v-if="success" class="mx-5 mt-4 bg-success/10 text-success text-sm rounded-lg px-4 py-3">{{ success }}</div>

    <!-- Loading state for edit mode -->
    <div v-if="editLoading" class="flex justify-center py-20">
      <LoadingSpinner />
    </div>

    <form v-else @submit.prevent="handleSubmit" class="px-5 mt-5 space-y-4">
      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">姓名 *</label>
        <input
          v-model="form.elderlyName"
          type="text"
          required
          placeholder="请输入老人姓名"
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
        />
      </div>

      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="block text-sm font-medium text-ink mb-1.5">性别 *</label>
          <select
            v-model="form.gender"
            required
            class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink focus:outline-none focus:border-primary transition-colors appearance-none"
          >
            <option value="" disabled>请选择</option>
            <option value="male">男</option>
            <option value="female">女</option>
            <option value="unknown">未知</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-ink mb-1.5">年龄 *</label>
          <input
            v-model.number="form.age"
            type="number"
            required
            min="1"
            max="150"
            placeholder="年龄"
            class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
          />
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">身份证号{{ isEdit ? '' : '' }}</label>
        <input
          v-model="form.idCard"
          type="text"
          :placeholder="isEdit ? '' : '选填'"
          :readonly="isEdit"
          :class="[
            'w-full h-11 px-4 rounded-lg border bg-white placeholder:text-muted focus:outline-none focus:border-primary transition-colors',
            isEdit ? 'text-muted-soft border-hairline cursor-not-allowed' : 'border-hairline text-ink',
          ]"
        />
        <p v-if="isEdit" class="text-[12px] text-muted-soft mt-1">身份证号不可修改</p>
      </div>

      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">手机号</label>
        <input
          v-model="form.phoneNumber"
          type="tel"
          placeholder="选填"
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">地址</label>
        <input
          v-model="form.address"
          type="text"
          placeholder="选填"
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
        />
      </div>

      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="block text-sm font-medium text-ink mb-1.5">紧急联系人</label>
          <input
            v-model="form.emergencyContact"
            type="text"
            placeholder="选填"
            class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-ink mb-1.5">紧急电话</label>
          <input
            v-model="form.emergencyPhone"
            type="tel"
            placeholder="选填"
            class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
          />
        </div>
      </div>

      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">既往病史</label>
        <input
          v-model="form.medicalHistory"
          type="text"
          placeholder="选填"
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">过敏史</label>
        <input
          v-model="form.allergyHistory"
          type="text"
          placeholder="选填"
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
        />
      </div>

      <button
        type="submit"
        :disabled="submitLoading"
        class="w-full h-11 rounded-full bg-primary text-white font-medium text-sm active:bg-primary-active disabled:bg-primary-disabled disabled:text-muted transition-colors"
      >
        {{ submitLoading ? (isEdit ? '保存中...' : '注册中...') : (isEdit ? '保存修改' : '注册并绑定') }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { elderly } from '../api.js'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import { PhCaretLeft } from '@phosphor-icons/vue'

const route = useRoute()
const router = useRouter()

const editId = computed(() => route.query.edit ? Number(route.query.edit) : null)
const isEdit = computed(() => !!editId.value)

const error = ref('')
const success = ref('')
const submitLoading = ref(false)
const editLoading = ref(false)

const form = reactive({
  elderlyName: '',
  gender: '',
  age: null,
  idCard: '',
  phoneNumber: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  medicalHistory: '',
  allergyHistory: '',
})

function resetForm() {
  Object.keys(form).forEach((k) => { form[k] = typeof form[k] === 'number' ? null : '' })
}

function populateForm(data) {
  form.elderlyName = data.elderlyName || ''
  form.gender = data.gender || ''
  form.age = data.age ?? null
  form.idCard = data.idCard || ''
  form.phoneNumber = data.phoneNumber || ''
  form.address = data.address || ''
  form.emergencyContact = data.emergencyContact || ''
  form.emergencyPhone = data.emergencyPhone || ''
  form.medicalHistory = data.medicalHistory || ''
  form.allergyHistory = data.allergyHistory || ''
}

async function handleSubmit() {
  error.value = ''
  success.value = ''
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await elderly.update({ ...form })
      success.value = '保存成功'
    } else {
      await elderly.register({ ...form })
      success.value = '注册成功，已自动绑定'
      resetForm()
    }
    setTimeout(() => router.back(), 800)
  } catch (e) {
    error.value = e.message
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  if (!isEdit.value) return

  editLoading.value = true
  try {
    const list = await elderly.list()
    const found = list.find((e) => e.id === editId.value)
    if (!found) {
      error.value = '未找到该老人信息'
      return
    }
    populateForm(found)
  } catch (e) {
    error.value = e.message
  } finally {
    editLoading.value = false
  }
})
</script>
