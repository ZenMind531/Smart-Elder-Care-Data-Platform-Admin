<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-semibold uppercase tracking-wider text-brand-600 dark:text-brand-400">AI Decision Assistant</span>
      <h1 class="mt-2 text-2xl font-bold text-gray-900 dark:text-white">AI 决策助手</h1>
      <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 dark:text-gray-400">基于平台数据，用自然语言提问，AI 分析并给出决策建议。</p>
    </div>

    <div class="mx-auto flex h-[calc(100vh-260px)] min-h-[500px] flex-col rounded-2xl border border-gray-200 bg-white shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
      <div ref="msgContainer" class="flex-1 overflow-y-auto px-4 py-4">
        <div v-if="messages.length === 0" class="flex h-full flex-col items-center justify-center text-center">
          <div class="flex size-16 items-center justify-center rounded-2xl bg-brand-50 dark:bg-brand-500/10">
            <svg class="size-8 text-brand-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.455 2.456L21.75 6l-1.036.259a3.375 3.375 0 00-2.455 2.456z" />
            </svg>
          </div>
          <h2 class="mt-4 text-lg font-semibold text-gray-800 dark:text-white">有什么可以帮你的？</h2>
          <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">试试下面的问题，或直接输入你想了解的</p>
          <div class="mt-5 flex flex-wrap justify-center gap-2">
            <button v-for="q in quickQuestions" :key="q" type="button" class="rounded-xl border border-gray-200 bg-white px-3 py-2 text-sm text-gray-600 transition-all hover:border-brand-300 hover:text-brand-700 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-400 dark:hover:border-brand-600 dark:hover:text-brand-400" @click="ask(q)">{{ q }}</button>
          </div>
        </div>

        <div v-for="(msg, i) in messages" :key="i" class="mb-4" :class="msg.role === 'user' ? 'flex justify-end' : 'flex justify-start'">
          <div class="max-w-[80%]">
            <div v-if="msg.role === 'assistant'" class="rounded-2xl rounded-tl-md border border-gray-100 bg-gray-50 px-4 py-3 dark:border-gray-800 dark:bg-gray-900">
              <!-- eslint-disable-next-line vue/no-v-html -->
              <div class="prose prose-sm max-w-none text-sm text-gray-700 dark:text-gray-300" v-html="renderMarkdown(msg.content)" />
            </div>
            <div v-else class="rounded-2xl rounded-tr-md bg-brand-600 px-4 py-3 text-sm text-white shadow-sm">{{ msg.content }}</div>
            <p v-if="msg.role === 'assistant' && msg.meta" class="mt-1 px-1 text-xs text-gray-400 dark:text-gray-500">{{ msg.meta }}</p>
          </div>
        </div>

        <div v-if="thinking" class="mb-4 flex justify-start">
          <div class="max-w-[80%] rounded-2xl rounded-tl-md border border-gray-100 bg-gray-50 px-4 py-3 dark:border-gray-800 dark:bg-gray-900">
            <div class="flex items-center gap-2">
              <span class="flex gap-1">
                <span class="size-2 animate-bounce rounded-full bg-brand-400" style="animation-delay: 0ms" />
                <span class="size-2 animate-bounce rounded-full bg-brand-400" style="animation-delay: 150ms" />
                <span class="size-2 animate-bounce rounded-full bg-brand-400" style="animation-delay: 300ms" />
              </span>
              <span class="text-sm text-gray-400">正在分析数据…</span>
            </div>
          </div>
        </div>
      </div>

      <div class="border-t border-gray-200 px-4 py-3 dark:border-gray-800">
        <p v-if="chatError" class="mb-2 text-xs text-red-500">{{ chatError }}</p>
        <form class="flex items-end gap-2" @submit.prevent="send">
          <textarea ref="inputRef" v-model="input" :disabled="thinking" class="max-h-32 min-h-[44px] flex-1 resize-none rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm shadow-sm placeholder:text-gray-400 focus:border-brand-400 focus:ring-2 focus:ring-brand-500/15 focus:outline-none dark:border-gray-700 dark:bg-gray-900 dark:text-white/90" placeholder="输入你的问题，比如「最近有哪些老人血压异常？」" rows="1" @keydown.enter.exact.prevent="send" @input="autoResize" />
          <button type="submit" :disabled="!input.trim() || thinking" class="flex size-11 shrink-0 items-center justify-center rounded-xl bg-brand-600 text-white shadow-sm transition-all hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-40">
            <svg class="size-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" /></svg>
          </button>
        </form>
        <p class="mt-2 text-xs text-gray-400 dark:text-gray-500">基于 DeepSeek · 数据来源：平台实时数据 · AI 建议仅供参考</p>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { sendAIChat, type AIChatMessage } from '@/api/ai'
import { ApiError } from '@/api/http'
import { listHealthWarnings } from '@/api/warnings'
import { listElderly } from '@/api/elderly'
import { listDevices } from '@/api/devices'
import { listHealthRecords } from '@/api/health'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  meta?: string
}

const messages = ref<ChatMessage[]>([])
const input = ref('')
const thinking = ref(false)
const chatError = ref('')
const msgContainer = ref<HTMLElement>()
const inputRef = ref<HTMLTextAreaElement>()

const quickQuestions = [
  '最近有哪些老人血压异常？',
  '当前设备在线率如何？',
  '本周新增了多少告警？',
  '哪些老人需要重点关注？',
  '护理人力分配是否合理？',
]

const ask = (q: string) => { input.value = q; nextTick(() => send()) }

const autoResize = () => {
  const el = inputRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 128) + 'px'
}

const scrollBottom = () => nextTick(() => {
  if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
})

const gatherContext = async (): Promise<string> => {
  const parts: string[] = []
  try {
    const elderly = await listElderly({ page: 1, size: 200 })
    const total = elderly.total ?? elderly.records.length
    parts.push(`老人总数: ${total}`)
  } catch { parts.push('老人数据: 获取失败') }

  try {
    const warnings = await listHealthWarnings({ page: 1, size: 100 })
    const pending = warnings.records.filter(w => w.status === 'pending').length
    const processing = warnings.records.filter(w => w.status === 'processing').length
    const high = warnings.records.filter(w => (w.warningLevel || w.level) === 'high').length
    parts.push(`健康预警: 共${warnings.total ?? warnings.records.length}条，待处理${pending}，处理中${processing}，紧急${high}`)
  } catch { parts.push('预警数据: 获取失败') }

  try {
    const devices = await listDevices({ page: 1, size: 100 })
    const online = devices.records.filter(d => d.status === 'normal').length
    parts.push(`设备: 共${devices.total ?? devices.records.length}台，在线${online}`)
  } catch { parts.push('设备数据: 获取失败') }

  try {
    const health = await listHealthRecords({ page: 1, size: 100 })
    const abnormal = health.records.filter(r => r.status === 'abnormal').length
    const pending = health.records.filter(r => r.status === 'pending').length
    parts.push(`健康记录: 共${health.total ?? health.records.length}条，异常${abnormal}，待复测${pending}`)
  } catch { parts.push('健康记录: 获取失败') }

  return parts.join('\n')
}

const send = async () => {
  const q = input.value.trim()
  if (!q || thinking.value) return

  messages.value.push({ role: 'user', content: q })
  input.value = ''
  chatError.value = ''
  thinking.value = true
  scrollBottom()

  let context = ''
  try { context = await gatherContext() } catch { /* continue without context */ }

  const apiMessages: AIChatMessage[] = [
    {
      role: 'system',
      content: `你是智慧养老管理平台的 AI 决策助手。你可以访问以下平台数据并基于数据回答问题、提供分析和决策建议。

数据上下文：
${context}

请根据以上数据分析回答用户问题。如果数据不足以回答，诚实说明。回答时引用具体数字，给出可操作建议。用中文。格式清晰，重点突出。`,
    },
    ...messages.value.map(m => ({ role: m.role as 'user' | 'assistant', content: m.content })),
  ]

  try {
    const resp = await sendAIChat({ messages: apiMessages, context })
    const reply = resp.reply || '抱歉，AI 暂时无法回复，请稍后重试。'
    messages.value.push({ role: 'assistant', content: reply, meta: `基于 ${context.split('\n').length} 条数据指标分析` })
  } catch (e) {
    const errMsg = e instanceof ApiError ? e.message : 'AI 服务暂不可用'
    messages.value.push({ role: 'assistant', content: `❌ ${errMsg}。请确认已配置 VITE_DEEPSEEK_API_KEY。`, meta: '请求失败' })
  } finally {
    thinking.value = false
    scrollBottom()
  }
}

const renderMarkdown = (text: string) => {
  return text
    .replace(/\*\*(.+?)\*\*/g, '<strong class="font-semibold text-gray-900 dark:text-white">$1</strong>')
    .replace(/\n- /g, '\n• ')
    .replace(/\n(\d+)\. /g, '\n$1. ')
    .replace(/\n\n/g, '</p><p class="mt-2">')
    .replace(/^/, '<p>')
    .replace(/$/, '</p>')
}
</script>
