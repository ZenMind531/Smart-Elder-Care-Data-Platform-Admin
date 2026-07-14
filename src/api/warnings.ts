import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type WarningLevel = 'low' | 'medium' | 'high'
export type WarningStatus = 'pending' | 'processing' | 'resolved'

export interface HealthWarningApi {
  id: number
  elderlyId?: number
  healthRecordId?: number
  retestRecordId?: number
  elderlyName?: string
  elderName?: string
  room?: string
  warningType?: string
  warningLevel?: WarningLevel
  level?: WarningLevel
  warningContent?: string
  source?: string
  status?: WarningStatus
  handleResult?: string
  handlerName?: string
  owner?: string
  createTime?: string
  warningTime?: string
  handleTime?: string
}

export interface HealthWarningListQuery extends PageQuery {
  elderlyId?: number
  level?: WarningLevel
  warningLevel?: WarningLevel
  status?: WarningStatus
}

export interface HealthWarningPayload {
  elderlyId: number
  warningType: string
  warningLevel: WarningLevel
  warningContent: string
  warningTime?: string
}

export const listHealthWarnings = (query: HealthWarningListQuery = {}) => {
  const { level, warningLevel, ...rest } = query

  return request<PageResult<HealthWarningApi>>(
    `/health-warnings${buildQuery({
      page: 1,
      size: 50,
      ...rest,
      warningLevel: warningLevel || level,
    })}`,
  )
}

export const getHealthWarning = (id: number) => request<HealthWarningApi>(`/health-warnings/${id}`)

export const createHealthWarning = (payload: HealthWarningPayload) =>
  request<HealthWarningApi>('/health-warnings', {
    method: 'POST',
    body: payload,
  })

export const updateHealthWarningStatus = (
  id: number,
  payload: { status: WarningStatus; handleResult?: string },
) =>
  request<HealthWarningApi>(`/health-warnings/${id}/status`, {
    method: 'PATCH',
    body: payload,
  })

export const deleteHealthWarning = (id: number) =>
  request<void>(`/health-warnings/${id}`, {
    method: 'DELETE',
  })
