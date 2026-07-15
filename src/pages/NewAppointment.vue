<template>
  <div class="pb-24">
    <header class="sticky top-0 z-10 bg-[#f5f5f7]/80 backdrop-blur-md">
      <div class="flex items-center h-14 px-4 gap-3">
        <button @click="$router.back()" class="w-9 h-9 flex items-center justify-center -ml-1">
          <PhCaretLeft :size="22" class="text-primary" />
        </button>
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">新建预约</h1>
      </div>
    </header>

    <div v-if="error" class="mx-5 mt-4 bg-error/10 text-error text-sm rounded-lg px-4 py-3">{{ error }}</div>
    <div v-if="success" class="mx-5 mt-4 bg-success/10 text-success text-sm rounded-lg px-4 py-3">{{ success }}</div>

    <form @submit.prevent="handleSubmit" class="px-5 pt-5 space-y-5">
      <!-- Elderly Select -->
      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">选择老人</label>
        <select
          v-model="form.elderlyId"
          required
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink focus:outline-none focus:border-primary transition-colors appearance-none"
        >
          <option value="" disabled>请选择</option>
          <option v-for="e in elderlyList" :key="e.id" :value="e.id">
            {{ e.elderlyName }} ({{ e.age }}岁)
          </option>
        </select>
      </div>

      <!-- Service Type -->
      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">服务类型</label>
        <div class="grid grid-cols-2 gap-2">
          <button
            v-for="s in serviceTypes"
            :key="s.value"
            type="button"
            @click="form.serviceType = s.value"
            class="h-11 rounded-full border text-sm font-medium transition-colors"
            :class="form.serviceType === s.value
              ? 'border-primary bg-primary/10 text-primary'
              : 'border-hairline text-muted'"
          >{{ s.label }}</button>
        </div>
      </div>

      <!-- Date -->
      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">预约日期</label>
        <input
          v-model="form.serviceDate"
          type="date"
          required
          class="w-full h-11 px-4 rounded-lg border border-hairline bg-white text-ink focus:outline-none focus:border-primary transition-colors"
        />
      </div>

      <!-- Time -->
      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">时间段</label>
        <div class="flex gap-2">
          <button
            v-for="t in timeSlots"
            :key="t"
            type="button"
            @click="form.serviceTime = t"
            class="flex-1 h-11 rounded-full border text-sm font-medium transition-colors"
            :class="form.serviceTime === t
              ? 'border-primary bg-primary/10 text-primary'
              : 'border-hairline text-muted'"
          >{{ t }}</button>
        </div>
      </div>

      <!-- Remark -->
      <div>
        <label class="block text-sm font-medium text-ink mb-1.5">备注（选填）</label>
        <textarea
          v-model="form.remark"
          rows="3"
          placeholder="请描述预约需求..."
          class="w-full px-4 py-3 rounded-lg border border-hairline bg-white text-ink placeholder:text-muted focus:outline-none focus:border-primary transition-colors resize-none"
        ></textarea>
      </div>

      <!-- Submit -->
      <button
        type="submit"
        :disabled="submitting"
        class="w-full h-11 rounded-full bg-primary text-white font-medium text-sm active:bg-primary-active disabled:bg-primary-disabled disabled:text-muted transition-colors"
      >
        <span v-if="success" class="submit-done-label">已提交 ✓</span>
        <span v-else>{{ submitting ? '提交中...' : '提交预约' }}</span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { elderly, reservations } from '../api.js'
import { PhCaretLeft } from '@phosphor-icons/vue'

const route = useRoute()
const router = useRouter()
const elderlyList = ref([])
const submitting = ref(false)
const error = ref('')
const success = ref('')

const serviceTypes = [
  { label: '问诊', value: '问诊' },
  { label: '体检', value: '体检' },
  { label: '护理', value: '护理' },
  { label: '心理', value: '心理' },
]

const timeSlots = ['上午', '下午']

const form = reactive({
  elderlyId: route.query.elderlyId || '',
  serviceType: '体检',
  serviceDate: '',
  serviceTime: '上午',
  remark: '',
})

onMounted(async () => {
  try {
    elderlyList.value = await elderly.list()
  } catch {}
})

async function handleSubmit() {
  error.value = ''
  success.value = ''
  submitting.value = true
  try {
    await reservations.create({
      elderlyId: Number(form.elderlyId),
      serviceType: form.serviceType,
      serviceDate: form.serviceDate,
      serviceTime: form.serviceTime,
      remark: form.remark,
    })
    success.value = '预约已提交，等待确认'
    setTimeout(() => router.push('/appointments'), 1500)
  } catch (e) {
    error.value = e.message
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.submit-done-label {
  animation: done-pop 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}
@keyframes done-pop {
  from { opacity: 0; }
  to   { opacity: 1; }
}
</style>
