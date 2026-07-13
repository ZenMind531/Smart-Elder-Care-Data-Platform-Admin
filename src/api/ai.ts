import { request } from './http'

export interface AIChatRequest {
  message: string
}

export interface AIChatResponse {
  reply: string
}

export const sendAIChat = (payload: AIChatRequest) =>
  request<AIChatResponse>('/ai/chat', {
    method: 'POST',
    body: payload,
  })
