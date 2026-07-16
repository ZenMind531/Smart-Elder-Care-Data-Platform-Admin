import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type CareType = 'medication' | 'vital_signs' | 'cleaning' | 'feeding' | 'exercise' | 'other'

export interface CareRecordApi {
  id: number
  elderlyId?: number
  elderlyName?: string
  caregiver?: string
  careType?: CareType
  careContent?: string
  careTime?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface CareRecordListQuery extends PageQuery {
  elderlyId?: number
  careType?: CareType
}

export interface CareRecordPayload {
  elderlyId: number
  careType: CareType
  careContent: string
  careTime: string
  caregiver?: string
  remark?: string
}

export const listCareRecords = (query: CareRecordListQuery = {}) =>
  request<PageResult<CareRecordApi>>(
    `/care-records${buildQuery({ page: 1, size: 50, ...query })}`,
  )

export const getCareRecord = (id: number) =>
  request<CareRecordApi>(`/care-records/${id}`)

export const createCareRecord = (payload: CareRecordPayload) =>
  request<CareRecordApi>('/care-records', {
    method: 'POST',
    body: payload,
  })

export const updateCareRecord = (id: number, payload: CareRecordPayload) =>
  request<CareRecordApi>(`/care-records/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const deleteCareRecord = (id: number) =>
  request<void>(`/care-records/${id}`, {
    method: 'DELETE',
  })
