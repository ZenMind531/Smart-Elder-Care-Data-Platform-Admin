import { buildQuery, request, type PageQuery, type PageResult } from './http'

export interface HealthRecordApi {
  id: number
  elderlyId?: number
  elderlyName?: string
  elderName?: string
  room?: string
  systolicPressure?: number
  diastolicPressure?: number
  bloodSugar?: number
  heartRate?: number
  temperature?: number
  bloodOxygen?: number
  sleepHours?: number
  status?: string
  recordTime?: string
  createTime?: string
  remark?: string
}

export interface HealthRecordPayload {
  elderlyId: number
  systolicPressure: number
  diastolicPressure: number
  bloodSugar?: number
  heartRate: number
  temperature: number
  recordTime: string
  remark?: string
}

export interface HealthRecordListQuery extends PageQuery {
  elderlyId?: number
}

export const listHealthRecords = (query: HealthRecordListQuery = {}) =>
  request<PageResult<HealthRecordApi>>(
    `/health-records${buildQuery({ page: 1, size: 50, ...query })}`,
  )

export const getHealthRecordTrend = <T = unknown>(query: { elderlyId: number; days?: number }) =>
  request<T>(`/health-records/trend${buildQuery({ days: 7, ...query })}`)

export const getHealthRecord = (id: number) => request<HealthRecordApi>(`/health-records/${id}`)

export const createHealthRecord = (payload: HealthRecordPayload) =>
  request<HealthRecordApi>('/health-records', {
    method: 'POST',
    body: payload,
  })

export const deleteHealthRecord = (id: number) =>
  request<void>(`/health-records/${id}`, {
    method: 'DELETE',
  })

export type HealthRecordStatus = 'normal' | 'abnormal' | 'pending'

export const updateHealthRecordStatus = (id: number, status: HealthRecordStatus) =>
  request<HealthRecordApi>(`/health-records/${id}/status`, {
    method: 'PATCH',
    body: { status },
  })
