<template>
  <AdminLayout>
    <div class="mb-6">
      <span class="text-theme-sm font-semibold uppercase tracking-wider text-brand-600 dark:text-brand-400">AI Decision Assistant</span>
      <h1 class="mt-2 text-2xl font-bold text-gray-900 dark:text-white">AI 决策助手</h1>
      <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 dark:text-gray-400">基于平台数据，用自然语言提问，AI 分析并给出决策建议。</p>
    </div>

    <div class="mx-auto flex h-[calc(100vh-260px)] min-h-[500px] flex-col rounded-[18px] border border-gray-200 bg-white shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
      <div ref="msgContainer" class="flex-1 overflow-y-auto px-4 py-4">
        <div v-if="messages.length === 0" class="flex h-full flex-col items-center justify-center text-center">
          <div class="flex size-16 items-center justify-center rounded-[18px] bg-brand-50 dark:bg-brand-500/10">
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
            <div v-if="msg.role === 'assistant'" class="rounded-[18px] rounded-tl-md border border-gray-100 bg-gray-50 px-4 py-3 dark:border-gray-800 dark:bg-gray-900">
              <!-- eslint-disable-next-line vue/no-v-html -->
              <div class="prose prose-sm max-w-none text-sm text-gray-700 dark:text-gray-300" v-html="renderMarkdown(msg.content)" />
            </div>
            <div v-else class="rounded-[18px] rounded-tr-md bg-brand-600 px-4 py-3 text-sm text-white shadow-sm">{{ msg.content }}</div>
          </div>
        </div>

        <div v-if="thinking" class="mb-4 flex justify-start">
          <div class="max-w-[80%] rounded-[18px] rounded-tl-md border border-gray-100 bg-gray-50 px-4 py-3 dark:border-gray-800 dark:bg-gray-900">
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
        <p class="mt-2 text-xs text-gray-400 dark:text-gray-500">AI 决策基于平台实时数据 · 建议仅供参考</p>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { sendAIChat } from '@/api/ai'
import { ApiError } from '@/api/http'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
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

const send = async () => {
  const q = input.value.trim()
  if (!q || thinking.value) return

  messages.value.push({ role: 'user', content: q })
  input.value = ''
  chatError.value = ''
  thinking.value = true
  scrollBottom()

  try {
    const resp = await sendAIChat({ message: q })
    messages.value.push({ role: 'assistant', content: resp.reply || 'AI 暂无回复' })
  } catch (e) {
    const errMsg = e instanceof ApiError ? e.message : 'AI 服务暂不可用'
    chatError.value = errMsg
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
