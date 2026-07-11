import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type UserStatus = 'pending' | 'enabled' | 'disabled'

export interface UserApiRecord {
  id: number
  username?: string
  realName?: string
  phoneNumber?: string
  roleId?: number
  roleName?: string
  status?: UserStatus
  createTime?: string
  updateTime?: string
}

export interface UserListQuery extends PageQuery {
  status?: UserStatus
}

export interface UserPayload {
  username: string
  password: string
  realName: string
  phoneNumber: string
  roleId: number
  status?: UserStatus
}

export const listUsers = (query: UserListQuery = {}) =>
  request<PageResult<UserApiRecord>>(`/users${buildQuery({ page: 1, size: 50, ...query })}`)

export const getUser = (id: number) => request<UserApiRecord>(`/users/${id}`)

export const createUser = (payload: UserPayload) =>
  request<UserApiRecord>('/users', {
    method: 'POST',
    body: payload,
  })

export const updateUser = (id: number, payload: UserPayload) =>
  request<UserApiRecord>(`/users/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const updateUserStatus = (id: number, status: UserStatus) =>
  request<UserApiRecord>(`/users/${id}/status`, {
    method: 'PATCH',
    body: { status },
  })

export const deleteUser = (id: number) =>
  request<void>(`/users/${id}`, {
    method: 'DELETE',
  })
