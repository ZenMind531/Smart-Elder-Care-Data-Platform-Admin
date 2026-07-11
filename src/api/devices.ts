import { buildQuery, request, type PageQuery, type PageResult } from './http'

export type DeviceStatusApi = 'normal' | 'abnormal' | 'offline' | 'maintenance'

export interface DeviceApiRecord {
  id: number | string
  deviceCode?: string
  deviceName?: string
  deviceType?: string
  elderlyId?: number
  elderlyName?: string
  room?: string
  battery?: number
  status?: DeviceStatusApi
  remark?: string
  lastSyncTime?: string
  updateTime?: string
  createTime?: string
}

export interface DeviceListQuery extends PageQuery {
  deviceType?: string
  status?: DeviceStatusApi
}

export interface DevicePayload {
  deviceCode: string
  deviceName: string
  deviceType: string
  elderlyId: number
  status: DeviceStatusApi
  remark?: string
}

export const listDevices = (query: DeviceListQuery = {}) =>
  request<PageResult<DeviceApiRecord>>(`/devices${buildQuery({ page: 1, size: 50, ...query })}`)

export const getDevice = (id: number | string) => request<DeviceApiRecord>(`/devices/${id}`)

export const createDevice = (payload: DevicePayload) =>
  request<DeviceApiRecord>('/devices', {
    method: 'POST',
    body: payload,
  })

export const updateDevice = (id: number | string, payload: DevicePayload) =>
  request<DeviceApiRecord>(`/devices/${id}`, {
    method: 'PUT',
    body: payload,
  })

export const updateDeviceStatus = (id: number | string, status: DeviceStatusApi) =>
  request<DeviceApiRecord>(`/devices/${id}/status`, {
    method: 'PATCH',
    body: { status },
  })

export const getDevicesByElderly = (elderlyId: number) =>
  request<DeviceApiRecord[]>(`/devices/elderly/${elderlyId}`)

export const bindDevice = (id: number | string, elderlyId: number) =>
  request<DeviceApiRecord>(`/devices/${id}/bind`, {
    method: 'PUT',
    body: { elderlyId },
  })

export const unbindDevice = (id: number | string) =>
  request<DeviceApiRecord>(`/devices/${id}/unbind`, {
    method: 'PUT',
  })

export const deleteDevice = (id: number | string) =>
  request<void>(`/devices/${id}`, {
    method: 'DELETE',
  })
