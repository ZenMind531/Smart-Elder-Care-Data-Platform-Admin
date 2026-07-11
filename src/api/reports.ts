import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type RiskLevel = 'low' | 'medium' | 'high'

export interface AssessmentReportApi {
  id: number
  elderlyId?: number
  elderlyName?: string
  reportTitle?: string
  riskLevel?: RiskLevel
  summary?: string
  suggestion?: string
  reportTime?: string
  createTime?: string
  updateTime?: string
}

export interface ReportListQuery extends PageQuery {
  elderlyId?: number
  riskLevel?: RiskLevel
}

export interface ReportPayload {
  elderlyId: number
  reportTitle: string
  riskLevel: RiskLevel
  summary: string
  suggestion: string
  reportTime: string
}

export const listReports = (query: ReportListQuery = {}) =>
  request<PageResult<AssessmentReportApi>>(
    `/assessment-reports${buildQuery({ page: 1, size: 50, ...query })}`,
  )

export const getReport = (id: number) =>
  request<AssessmentReportApi>(`/assessment-reports/${id}`)

export const createReport = (payload: ReportPayload) =>
  request<AssessmentReportApi>('/assessment-reports', {
    method: 'POST',
    body: payload,
  })

export const updateReport = (id: number, payload: ReportPayload) =>
  request<AssessmentReportApi>(`/assessment-reports/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const deleteReport = (id: number) =>
  request<void>(`/assessment-reports/${id}`, {
    method: 'DELETE',
  })
