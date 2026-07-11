import { request } from './http'

export interface RoleApiRecord {
  id: number
  roleName?: string
  name?: string
  roleCode?: string
  description?: string
  userCount?: number
  users?: number
  status?: 'enabled' | 'disabled'
  enabled?: boolean
}

export interface RolePayload {
  roleName: string
  roleCode: string
  description?: string
}

export const listRoles = () => request<RoleApiRecord[]>('/roles')

export const createRole = (payload: RolePayload) =>
  request<RoleApiRecord>('/roles', {
    method: 'POST',
    body: payload,
  })

export const updateRole = (id: number, payload: RolePayload) =>
  request<RoleApiRecord>(`/roles/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const deleteRole = (id: number) =>
  request<void>(`/roles/${id}`, {
    method: 'DELETE',
  })
