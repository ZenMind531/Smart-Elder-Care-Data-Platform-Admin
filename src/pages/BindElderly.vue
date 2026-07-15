<template>
  <div class="pb-24">
    <header class="sticky top-0 z-10 bg-white/80 backdrop-blur-md border-b border-hairline">
      <div class="flex items-center h-14 px-4 gap-3">
        <button @click="$router.back()" class="w-9 h-9 flex items-center justify-center -ml-1">
          <PhCaretLeft :size="22" class="text-primary" />
        </button>
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">绑定老人</h1>
      </div>
    </header>

    <!-- Error / Success -->
    <div v-if="error" class="mx-5 mt-4 bg-error/10 text-error text-sm rounded-lg px-4 py-3">{{ error }}</div>
    <div v-if="success" class="mx-5 mt-4 bg-success/10 text-success text-sm rounded-lg px-4 py-3">{{ success }}</div>

    <!-- Already bound -->
    <section v-if="boundList.length > 0" class="px-5 mt-5">
      <p class="text-xs text-muted uppercase tracking-wider mb-3">已绑定 ({{ boundList.length }})</p>
      <div class="space-y-2">
        <div
          v-for="e in boundList"
          :key="e.id"
          class="flex items-center justify-between bg-white border border-hairline rounded-[18px] px-4 py-3"
        >
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-primary/15 flex items-center justify-center">
              <span class="font-display text-lg text-primary">{{ e.elderlyName?.charAt(0) }}</span>
            </div>
            <div>
              <p class="text-sm font-medium text-ink">{{ e.elderlyName }}</p>
              <p class="text-xs text-muted">{{ e.age }}岁 · {{ e.gender === 'male' ? '男' : '女' }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Bind by ID -->
    <section class="px-5 mt-6">
      <h2 class="text-sm font-medium text-muted uppercase tracking-wider mb-3">绑定已有老人</h2>
      <p class="text-xs text-muted-soft mb-3">输入老人 ID 绑定已有老人</p>
      <div class="flex gap-2">
        <input
          v-model="bindId"
          type="number"
          placeholder="输入老人 ID"
          class="flex-1 h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
        />
        <button
          @click="handleBind"
          :disabled="bindLoading"
          class="px-5 h-11 rounded-full bg-primary text-white text-[17px] font-medium active:bg-primary-active disabled:bg-primary-disabled disabled:text-muted transition-colors shrink-0"
        >
          {{ bindLoading ? '绑定中...' : '绑定' }}
        </button>
      </div>
    </section>

    <!-- Register new elderly -->
    <section class="px-5 mt-8">
      <h2 class="text-sm font-medium text-muted uppercase tracking-wider mb-3">注册新老人</h2>

      <form @submit.prevent="handleRegister" class="space-y-4">
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
          <label class="block text-sm font-medium text-ink mb-1.5">身份证号</label>
          <input
            v-model="form.idCard"
            type="text"
            placeholder="选填"
            class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors"
          />
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
          :disabled="registerLoading"
          class="w-full h-11 rounded-lg bg-primary text-white font-medium text-sm active:bg-primary-active disabled:bg-primary-disabled disabled:text-muted transition-colors"
        >
          {{ registerLoading ? '注册中...' : '注册并绑定' }}
        </button>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { elderly } from '../api.js'
import { PhCaretLeft } from '@phosphor-icons/vue'

const boundList = ref([])
const error = ref('')
const success = ref('')

// ── 绑定已有老人 ──
const bindId = ref('')
const bindLoading = ref(false)

async function handleBind() {
  const id = Number(bindId.value)
  if (!id) {
    error.value = '请输入有效的老人 ID'
    return
  }
  error.value = ''
  success.value = ''
  bindLoading.value = true
  try {
    await elderly.bind(id)
    success.value = '绑定成功'
    bindId.value = ''
    await loadBound()
  } catch (e) {
    error.value = e.message
  } finally {
    bindLoading.value = false
  }
}

// ── 注册新老人 ──
const registerLoading = ref(false)
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

async function handleRegister() {
  error.value = ''
  success.value = ''
  registerLoading.value = true
  try {
    await elderly.register({ ...form })
    success.value = '注册成功，已自动绑定'
    // Reset form
    Object.keys(form).forEach((k) => { form[k] = typeof form[k] === 'number' ? null : '' })
    await loadBound()
  } catch (e) {
    error.value = e.message
  } finally {
    registerLoading.value = false
  }
}

async function loadBound() {
  try {
    boundList.value = await elderly.list()
  } catch {}
}

onMounted(loadBound)
</script>
