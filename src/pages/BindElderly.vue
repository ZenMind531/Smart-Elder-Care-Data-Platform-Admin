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

    <!-- Empty hint -->
    <section v-else class="px-5 mt-12 text-center">
      <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
        <PhHeart :size="28" class="text-muted" />
      </div>
      <p class="text-muted text-[15px]">还没有绑定老人</p>
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

    <!-- Entry to register new elderly -->
    <section class="px-5 mt-8">
      <router-link
        to="/elderly/new"
        class="flex items-center justify-center gap-2 w-full h-11 rounded-full border-2 border-dashed border-hairline text-muted text-sm font-medium active:bg-surface-card transition-colors"
      >
        <PhPlusCircle :size="18" />
        注册新老人
      </router-link>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { elderly } from '../api.js'
import { PhCaretLeft, PhHeart, PhPlusCircle } from '@phosphor-icons/vue'

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

async function loadBound() {
  try {
    boundList.value = await elderly.list()
  } catch {}
}

onMounted(loadBound)
</script>
