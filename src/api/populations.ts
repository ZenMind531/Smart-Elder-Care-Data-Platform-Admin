import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type PopulationType = 'chronic' | 'disability' | 'solitary' | 'empty_nesters'
export type FollowStatus = 'pending' | 'completed'

export interface KeyPopulationApi {
  id: number
  elderlyId?: number
  elderlyName?: string
  populationType?: PopulationType
  reason?: string
  followStatus?: FollowStatus
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface PopulationListQuery extends PageQuery {
  populationType?: PopulationType
  followStatus?: FollowStatus
}

export interface PopulationPayload {
  elderlyId: number
  populationType: PopulationType
  reason: string
  followStatus: FollowStatus
  remark?: string
}

export const listPopulations = (query: PopulationListQuery = {}) =>
  request<PageResult<KeyPopulationApi>>(
    `/key-populations${buildQuery({ page: 1, size: 50, ...query })}`,
  )

export const getPopulation = (id: number) =>
  request<KeyPopulationApi>(`/key-populations/${id}`)

export const createPopulation = (payload: PopulationPayload) =>
  request<KeyPopulationApi>('/key-populations', {
    method: 'POST',
    body: payload,
  })

export const updatePopulation = (id: number, payload: PopulationPayload) =>
  request<KeyPopulationApi>(`/key-populations/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const deletePopulation = (id: number) =>
  request<void>(`/key-populations/${id}`, {
    method: 'DELETE',
  })

export const updateFollowStatus = (id: number, followStatus: FollowStatus) =>
  request<KeyPopulationApi>(`/key-populations/${id}/follow-status`, {
    method: 'PATCH',
    body: { followStatus },
  })
