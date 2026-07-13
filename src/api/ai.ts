import { ApiError } from './http'

const DEEPSEEK_BASE = 'https://api.deepseek.com'

export interface AIChatMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
}

export interface AIChatResponse {
  reply: string
  model?: string
}

const getApiKey = (): string => {
  const key = import.meta.env.VITE_DEEPSEEK_API_KEY
  if (!key) throw new ApiError('未配置 DEEPSEEK_API_KEY，请在 .env.local 中设置 VITE_DEEPSEEK_API_KEY=***')
  return key
}

export const sendAIChat = async (payload: {
  messages: AIChatMessage[]
  context?: string
}): Promise<AIChatResponse> => {
  const apiKey = getApiKey()

  const resp = await fetch(`${DEEPSEEK_BASE}/v1/chat/completions`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${apiKey}`,
    },
    body: JSON.stringify({
      model: 'deepseek-chat',
      messages: payload.messages,
      temperature: 0.3,
      max_tokens: 2048,
    }),
  })

  if (!resp.ok) {
    const err = await resp.json().catch(() => ({}))
    throw new ApiError((err as any).error?.message || `DeepSeek API ${resp.status}`)
  }

  const data = await resp.json()
  const reply = data.choices?.[0]?.message?.content || 'AI 返回为空'
  return { reply, model: data.model }
}
