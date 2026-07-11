import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type ApiGender = 'male' | 'female'
export type ApiRiskLevel = 'low' | 'medium' | 'high'

export interface ElderlyApiRecord {
  id: number
  elderlyName?: string
  gender?: ApiGender
  age?: number
  idCard?: string
  phoneNumber?: string
  address?: string
  riskLevel?: ApiRiskLevel
  emergencyContact?: string
  emergencyPhone?: string
  medicalHistory?: string
  allergyHistory?: string
  caregiverName?: string
  createTime?: string
  updateTime?: string
}

export interface ElderlyListQuery extends PageQuery {
  gender?: ApiGender
  riskLevel?: ApiRiskLevel
}

export type ElderlyPayload = Omit<ElderlyApiRecord, 'id' | 'createTime' | 'updateTime'>

export const listElderly = (query: ElderlyListQuery = {}) =>
  request<PageResult<ElderlyApiRecord>>(`/elderly${buildQuery({ page: 1, size: 50, ...query })}`)

export const getElderly = (id: number) => request<ElderlyApiRecord>(`/elderly/${id}`)

export const getElderlyHealthSummary = <T = unknown>(id: number) =>
  request<T>(`/elderly/${id}/health-summary`)

export const createElderly = (payload: ElderlyPayload) =>
  request<ElderlyApiRecord>('/elderly', {
    method: 'POST',
    body: payload,
  })

export const updateElderly = (id: number, payload: ElderlyPayload) =>
  request<ElderlyApiRecord>(`/elderly/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const deleteElderly = (id: number) =>
  request<void>(`/elderly/${id}`, {
    method: 'DELETE',
  })
