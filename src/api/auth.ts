import { request, type StoredUserInfo } from './http'

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userInfo: StoredUserInfo
}

export interface RegisterPayload {
  username: string
  password: string
  realName: string
  phoneNumber: string
  roleId: number
  gender?: 'male' | 'female'
  department?: string
  title?: string
}

export const login = (payload: LoginPayload) =>
  request<LoginResult>('/auth/login', {
    method: 'POST',
    body: payload,
    auth: false,
  })

export const logout = () =>
  request<void>('/auth/logout', {
    method: 'POST',
  })

export const getCurrentUser = () => request<StoredUserInfo>('/auth/me')

export const updatePassword = (payload: { oldPassword: string; newPassword: string }) =>
  request<void>('/auth/password', {
    method: 'PUT',
    body: payload,
  })

export const register = (payload: RegisterPayload) =>
  request<StoredUserInfo>('/auth/register', {
    method: 'POST',
    body: payload,
  })
