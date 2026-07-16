import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type ServiceType = 'health_check' | 'home_care' | 'rehabilitation' | 'consultation' | 'other'
export type AppointmentStatus = 'pending' | 'confirmed' | 'completed' | 'cancelled'

export interface AppointmentApi {
  id: number
  elderlyId?: number
  elderlyName?: string
  serviceType?: ServiceType
  appointmentTime?: string
  doctorName?: string
  description?: string
  status?: AppointmentStatus
  cancelReason?: string
  createTime?: string
  updateTime?: string
}

export interface AppointmentListQuery extends PageQuery {
  elderlyId?: number
  status?: AppointmentStatus
  serviceType?: ServiceType
}

export interface AppointmentPayload {
  elderlyId: number
  serviceType: ServiceType
  appointmentTime: string
  doctorName?: string
  description?: string
}

export interface AppointmentStatusPayload {
  status: AppointmentStatus
  cancelReason?: string
}

export const listAppointments = (query: AppointmentListQuery = {}) =>
  request<PageResult<AppointmentApi>>(
    `/appointments${buildQuery({ page: 1, size: 50, ...query })}`,
  )

export const getAppointment = (id: number) =>
  request<AppointmentApi>(`/appointments/${id}`)

export const createAppointment = (payload: AppointmentPayload) =>
  request<AppointmentApi>('/appointments', {
    method: 'POST',
    body: payload,
  })

export const updateAppointment = (id: number, payload: AppointmentPayload) =>
  request<AppointmentApi>(`/appointments/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const patchAppointmentStatus = (id: number, payload: AppointmentStatusPayload) =>
  request<AppointmentApi>(`/appointments/${id}/status`, {
    method: 'PATCH',
    body: payload,
  })

export const deleteAppointment = (id: number) =>
  request<void>(`/appointments/${id}`, {
    method: 'DELETE',
  })
