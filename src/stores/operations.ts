import { defineStore } from 'pinia'
import {
  createHealthRecord,
  deleteHealthRecord,
  getHealthRecord,
  listHealthRecords,
  type HealthRecordApi,
} from '@/api/health'
import {
  createHealthWarning,
  deleteHealthWarning,
  getHealthWarning,
  listHealthWarnings,
  updateHealthWarningStatus,
  type HealthWarningApi,
  type WarningLevel,
  type WarningStatus,
} from '@/api/warnings'
import {
  listDevices,
  updateDeviceStatus,
  type DeviceApiRecord,
  type DeviceStatusApi,
} from '@/api/devices'
import { listRoles, type RoleApiRecord } from '@/api/roles'
import {
  listCareRecords,
  createCareRecord,
  updateCareRecord,
  deleteCareRecord,
  type CareRecordApi,
  type CareType,
  type CareRecordPayload,
} from '@/api/care-records'
import {
  listAppointments,
  createAppointment,
  updateAppointment,
  patchAppointmentStatus,
  deleteAppointment,
  type AppointmentApi,
  type AppointmentStatus,
  type ServiceType,
  type AppointmentPayload,
} from '@/api/appointments'
import { normalizeStaffRole } from '@/config/roles'

export interface HealthRecord {
  id: number
  elderlyId?: number
  elderName: string
  room: string
  heartRate: number
  bloodPressure: string
  systolicPressure?: number
  diastolicPressure?: number
  bloodSugar?: number
  temperature: string
  bloodOxygen: number
  sleepHours: number
  status: '正常' | '需复测' | '异常'
  measuredAt: string
  remark?: string
  riskReason?: string
}

export interface HealthRecordInput {
  elderlyId?: number
  retestWarningId?: number
  elderName: string
  room: string
  heartRate: number
  systolicPressure: number
  diastolicPressure: number
  bloodSugar: number
  temperature: number
  bloodOxygen: number
  sleepHours: number
  measuredAt?: string
  remark?: string
}

export interface AlertRecord {
  id: number
  relatedRecordId?: number
  retestRecordId?: number
  elderlyId?: number
  warningType?: string
  elderName: string
  room: string
  type: string
  level: '紧急' | '关注' | '普通'
  source: string
  status: '待处理' | '处理中' | '已处理'
  time: string
  owner: string
  content?: string
  handleResult?: string
  handledAt?: string
  contactPhone?: string
  escalation?: boolean
  slaMinutes?: number
}

export interface ManualAlertInput {
  elderlyId?: number
  elderName: string
  room: string
  warningType: 'blood_pressure' | 'blood_sugar' | 'heart_rate' | 'temperature'
  warningLevel: WarningLevel
  warningContent: string
}

export interface DeviceRecord {
  id: string
  apiId?: number | string
  name: string
  type: string
  room: string
  boundElder: string
  battery: number
  status: '在线' | '低电量' | '离线' | '维护中'
  lastSync: string
}

export interface CareRecord {
  id: number
  elderlyId?: number
  elderName: string
  room: string
  type: string
  content: string
  caregiver: string
  status: '已完成' | '进行中' | '待补录'
  time: string
  remark?: string
}

export interface ServiceAppointment {
  id: number
  elderlyId?: number
  elderName: string
  room: string
  service: string
  requester: string
  scheduledAt: string
  status: '未完成' | '已完成' | '已取消'
  assignee: string
  description?: string
  cancelReason?: string
}

export interface RoleRecord {
  id: number
  name: string
  users: number
  scope: string
  enabled: boolean
}

export interface NotificationRule {
  id: number
  name: string
  channel: string
  target: string
  enabled: boolean
}

const apiDateTimeNow = () => new Date().toISOString().slice(0, 19).replace('T', ' ')

const parseTemperature = (value: string | number | undefined) => {
  if (typeof value === 'number') return value
  if (!value) return 0
  return Number.parseFloat(value)
}

const getHealthRiskReason = (record: {
  heartRate?: number
  systolicPressure?: number
  diastolicPressure?: number
  bloodSugar?: number
  temperature?: number | string
  bloodOxygen?: number
  sleepHours?: number
}) => {
  const reasons: string[] = []
  const temperature = parseTemperature(record.temperature)

  if ((record.systolicPressure || 0) >= 160 || (record.diastolicPressure || 0) >= 100) {
    reasons.push('血压达到高危阈值')
  } else if ((record.systolicPressure || 0) >= 140 || (record.diastolicPressure || 0) >= 90) {
    reasons.push('血压偏高，建议复测')
  }

  if ((record.heartRate || 0) >= 115 || (record.heartRate || 0) <= 55) {
    reasons.push('心率明显异常')
  } else if ((record.heartRate || 0) >= 100 || (record.heartRate || 0) <= 60) {
    reasons.push('心率接近异常范围')
  }

  if (temperature >= 38) {
    reasons.push('体温异常升高')
  } else if (temperature >= 37.3) {
    reasons.push('体温偏高')
  }

  if ((record.bloodOxygen || 100) <= 93) {
    reasons.push('血氧低于安全阈值')
  } else if ((record.bloodOxygen || 100) <= 95) {
    reasons.push('血氧偏低')
  }

  if ((record.bloodSugar || 0) >= 11.1) {
    reasons.push('血糖达到高危阈值')
  } else if ((record.bloodSugar || 0) >= 7.8) {
    reasons.push('血糖偏高')
  }

  if ((record.sleepHours || 0) > 0 && (record.sleepHours || 0) < 5) {
    reasons.push('睡眠时长不足 5 小时')
  }

  return reasons.join('、') || '体征处于正常范围'
}

const evaluateHealthStatus = (record: {
  heartRate?: number
  systolicPressure?: number
  diastolicPressure?: number
  bloodSugar?: number
  temperature?: number | string
  bloodOxygen?: number
  sleepHours?: number
}): HealthRecord['status'] => {
  const temperature = parseTemperature(record.temperature)
  const heartRate = record.heartRate || 0
  const systolic = record.systolicPressure || 0
  const diastolic = record.diastolicPressure || 0
  const bloodOxygen = record.bloodOxygen || 100
  const bloodSugar = record.bloodSugar || 0
  const sleepHours = record.sleepHours || 0

  if (
    systolic >= 160 ||
    diastolic >= 100 ||
    heartRate >= 115 ||
    heartRate <= 55 ||
    temperature >= 38 ||
    bloodOxygen <= 93 ||
    bloodSugar >= 11.1
  ) {
    return '异常'
  }

  if (
    systolic >= 140 ||
    diastolic >= 90 ||
    heartRate >= 100 ||
    heartRate <= 60 ||
    temperature >= 37.3 ||
    bloodOxygen <= 95 ||
    bloodSugar >= 7.8 ||
    (sleepHours > 0 && sleepHours < 5)
  ) {
    return '需复测'
  }

  return '正常'
}

const healthStatusFromApi = (record: HealthRecordApi): HealthRecord['status'] => {
  if (record.status === 'abnormal' || record.status === '异常') return '异常'
  if (
    record.status === 'pending' ||
    record.status === 'review' ||
    record.status === 'recheck' ||
    record.status === '需复测'
  ) {
    return '需复测'
  }
  if (record.status === 'normal' || record.status === '正常') return '正常'
  return evaluateHealthStatus(record)
}

const toHealthRecord = (record: HealthRecordApi): HealthRecord => {
  const systolic = record.systolicPressure || 0
  const diastolic = record.diastolicPressure || 0

  return {
    id: record.id,
    elderlyId: record.elderlyId,
    elderName: record.elderlyName || record.elderName || `老人 ${record.elderlyId || record.id}`,
    room: record.room || `ID-${record.elderlyId || record.id}`,
    heartRate: record.heartRate || 0,
    bloodPressure: systolic && diastolic ? `${systolic}/${diastolic}` : '待采集',
    systolicPressure: systolic || undefined,
    diastolicPressure: diastolic || undefined,
    bloodSugar: record.bloodSugar,
    temperature: record.temperature ? String(record.temperature) : '待采集',
    bloodOxygen: record.bloodOxygen || 97,
    sleepHours: record.sleepHours || 0,
    status: healthStatusFromApi(record),
    measuredAt: record.recordTime || record.createTime || '接口同步',
    remark: record.remark,
    riskReason: getHealthRiskReason(record),
  }
}

const warningLevelMap: Record<WarningLevel, AlertRecord['level']> = {
  high: '紧急',
  medium: '关注',
  low: '普通',
}

const warningStatusMap: Record<WarningStatus, AlertRecord['status']> = {
  pending: '待处理',
  processing: '处理中',
  resolved: '已处理',
}

const toWarningStatus = (status: AlertRecord['status']): WarningStatus => {
  if (status === '已处理') return 'resolved'
  if (status === '处理中') return 'processing'
  return 'pending'
}

const warningTypeMap: Record<string, string> = {
  blood_pressure: '血压异常',
  heart_rate: '心率异常',
  blood_sugar: '血糖异常',
  temperature: '体温异常',
  device: '设备异常',
  manual: '人工提醒',
}

const toAlertRecord = (record: HealthWarningApi): AlertRecord => {
  const level = record.warningLevel || record.level || 'medium'
  const warningType = record.warningType || ''

  return {
    id: record.id,
    relatedRecordId: record.healthRecordId,
    retestRecordId: record.retestRecordId,
    elderlyId: record.elderlyId,
    warningType,
    elderName: record.elderlyName || record.elderName || `老人 ${record.elderlyId || record.id}`,
    room: record.room || `ID-${record.elderlyId || record.id}`,
    type: warningTypeMap[warningType] || record.warningContent || warningType || '健康预警',
    level: warningLevelMap[level],
    source: record.source || '健康预警',
    status: warningStatusMap[record.status || 'pending'],
    time: record.warningTime || record.createTime || '接口同步',
    owner: record.handlerName || record.owner || '待分配',
    content: record.warningContent,
    handleResult: record.handleResult,
    handledAt: record.handleTime,
    slaMinutes: level === 'high' ? 5 : level === 'medium' ? 15 : 30,
  }
}

const deviceTypeMap: Record<string, string> = {
  watch: '智能手环',
  sleep_monitor: '睡眠监测垫',
  door_sensor: '门磁传感器',
  emergency_button: '紧急呼叫按钮',
}

const deviceStatusMap: Record<DeviceStatusApi, DeviceRecord['status']> = {
  normal: '在线',
  abnormal: '离线',
  offline: '离线',
  maintenance: '维护中',
}

const toDeviceRecord = (record: DeviceApiRecord): DeviceRecord => {
  const status = deviceStatusMap[record.status || 'normal']

  return {
    id: record.deviceCode || String(record.id),
    apiId: record.id,
    name: record.deviceName || record.deviceCode || `设备 ${record.id}`,
    type: deviceTypeMap[record.deviceType || ''] || record.deviceType || '智能设备',
    room: record.room || `老人ID-${record.elderlyId || '-'}`,
    boundElder: record.elderlyName || '未绑定',
    battery: record.battery ?? (status === '在线' ? 86 : 0),
    status,
    lastSync: record.lastSyncTime || record.updateTime || record.createTime || '接口同步',
  }
}

const toRoleRecord = (record: RoleApiRecord): RoleRecord => ({
  id: record.id,
  name: record.roleName || record.name || `角色 ${record.id}`,
  users: record.userCount || record.users || 0,
  scope: record.description || record.roleCode || '按后端角色权限配置',
  enabled: record.enabled ?? record.status !== 'disabled',
})

const isStaffRole = (role: RoleRecord) => !/家属|患者|病人|老人端|个人端/.test(role.name)

const mergedSystemRoleScope = '系统配置、角色权限、机构资料、设备接入和运维巡检'

const normalizeRoleRecord = (role: RoleRecord): RoleRecord => {
  if (normalizeStaffRole(role.name) !== '系统管理员') return role

  return {
    ...role,
    id: role.name.includes('设备') ? 1 : role.id,
    name: '系统管理员',
    scope: mergedSystemRoleScope,
  }
}

const mergeRoleRecords = (roles: RoleRecord[]) =>
  roles
    .filter(isStaffRole)
    .map(normalizeRoleRecord)
    .reduce<RoleRecord[]>((mergedRoles, role) => {
      const existingRole = mergedRoles.find((item) => item.name === role.name)

      if (!existingRole) {
        mergedRoles.push({ ...role })
        return mergedRoles
      }

      existingRole.users += role.users
      existingRole.enabled = existingRole.enabled || role.enabled
      if (existingRole.name === '系统管理员') {
        existingRole.scope = mergedSystemRoleScope
      }

      return mergedRoles
    }, [])

const careTypeLabel = (t?: CareType) => {
  if (t === 'medication') return '服药'
  if (t === 'vital_signs') return '体征监测'
  if (t === 'cleaning') return '清洁护理'
  if (t === 'feeding') return '饮食护理'
  if (t === 'exercise') return '康复训练'
  return '其他护理'
}

const serviceTypeLabel = (t?: ServiceType) => {
  if (t === 'health_check') return '健康检查'
  if (t === 'home_care') return '上门护理'
  if (t === 'rehabilitation') return '康复训练'
  if (t === 'consultation') return '健康咨询'
  return '其他服务'
}

const normalizedAppointmentStatus = (status?: AppointmentStatus): ServiceAppointment['status'] => {
  if (status === 'completed') return '已完成'
  if (status === 'cancelled') return '已取消'
  return '未完成' // pending, confirmed, or unknown → 未完成
}

const toCareRecord = (r: CareRecordApi): CareRecord => ({
  id: r.id,
  elderlyId: r.elderlyId,
  elderName: r.elderlyName || `老人ID-${r.elderlyId || '未知'}`,
  room: r.elderlyId ? `ID-${r.elderlyId}` : '—',
  type: careTypeLabel(r.careType),
  content: r.careContent || '',
  caregiver: r.caregiver || '—',
  status: '已完成', // API 无 status 字段，护理记录完成后即为已完成
  time: r.careTime || r.createTime || '接口同步',
  remark: r.remark,
})

const toServiceAppointment = (a: AppointmentApi): ServiceAppointment => ({
  id: a.id,
  elderlyId: a.elderlyId,
  elderName: a.elderlyName || `老人ID-${a.elderlyId || '未知'}`,
  room: a.elderlyId ? `ID-${a.elderlyId}` : '—',
  service: serviceTypeLabel(a.serviceType),
  requester: '系统登记',
  scheduledAt: a.appointmentTime || a.createTime || '接口同步',
  status: normalizedAppointmentStatus(a.status),
  assignee: a.doctorName || '待分配',
  description: a.description,
  cancelReason: a.cancelReason,
})

export const useOperationsStore = defineStore('operations', {
  state: () => ({
    loading: false,
    error: '',
    healthRecords: [] as HealthRecord[],
    alerts: [] as AlertRecord[],
    devices: [] as DeviceRecord[],
    careRecords: [] as CareRecord[],
    serviceAppointments: [] as ServiceAppointment[],
    roles: [] as RoleRecord[],
    notificationRules: [] as NotificationRule[],
  }),
  getters: {
    unresolvedAlerts: (state) => state.alerts.filter((alert) => alert.status !== '已处理').length,
    onlineDevices: (state) => state.devices.filter((device) => device.status === '在线').length,
    pendingServices: (state) =>
      state.serviceAppointments.filter((service) => service.status === '未完成').length,
    todayCareDone: (state) =>
      state.careRecords.filter((record) => record.status === '已完成').length,
  },
  actions: {
    async fetchHealthRecords() {
      this.loading = true
      this.error = ''

      try {
        const result = await listHealthRecords({ page: 1, size: 100 })
        this.healthRecords = result.records.map(toHealthRecord)
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async fetchHealthRecordDetail(id: number) {
      try {
        const record = toHealthRecord(await getHealthRecord(id))
        const index = this.healthRecords.findIndex((item) => item.id === id)
        if (index >= 0) {
          this.healthRecords.splice(index, 1, record)
        } else {
          this.healthRecords.unshift(record)
        }
        this.error = ''
        return record
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        return this.healthRecords.find((item) => item.id === id) || null
      }
    },
    async fetchAlerts() {
      this.loading = true
      this.error = ''

      try {
        const result = await listHealthWarnings({ page: 1, size: 100 })
        this.alerts = result.records.map(toAlertRecord)
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async fetchAlertDetail(id: number) {
      try {
        const alert = toAlertRecord(await getHealthWarning(id))
        const index = this.alerts.findIndex((item) => item.id === id)
        if (index >= 0) {
          this.alerts.splice(index, 1, alert)
        } else {
          this.alerts.unshift(alert)
        }
        this.error = ''
        return alert
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        return this.alerts.find((item) => item.id === id) || null
      }
    },
    async fetchDevices() {
      this.loading = true
      this.error = ''

      try {
        const result = await listDevices({ page: 1, size: 100 })
        this.devices = result.records.map(toDeviceRecord)
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async fetchRoles() {
      this.loading = true
      this.error = ''

      try {
        this.roles = mergeRoleRecords((await listRoles()).map(toRoleRecord))
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async fetchCareRecords() {
      this.loading = true
      this.error = ''

      try {
        const result = await listCareRecords({ page: 1, size: 100 })
        this.careRecords = result.records.map(toCareRecord)
      } catch (error) {
        this.careRecords = []
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async fetchServiceAppointments() {
      this.loading = true
      this.error = ''

      try {
        const result = await listAppointments({ page: 1, size: 100 })
        this.serviceAppointments = result.records.map(toServiceAppointment)
      } catch (error) {
        this.serviceAppointments = []
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async addHealthRecord(payload?: HealthRecordInput) {
      const nextId = Math.max(...this.healthRecords.map((item) => item.id), 0) + 1
      const source = payload || {
        elderlyId: 7,
        elderName: '刘玉梅',
        room: 'C-212',
        heartRate: 82,
        systolicPressure: 128,
        diastolicPressure: 80,
        bloodSugar: 6.2,
        temperature: 36.6,
        bloodOxygen: 97,
        sleepHours: 6.8,
        measuredAt: '刚刚',
        remark: '前端新增体征记录',
      }
      const status = evaluateHealthStatus(source)
      const localRecord: HealthRecord = {
        id: nextId,
        elderlyId: source.elderlyId,
        elderName: source.elderName,
        room: source.room,
        heartRate: source.heartRate,
        bloodPressure: `${source.systolicPressure}/${source.diastolicPressure}`,
        systolicPressure: source.systolicPressure,
        diastolicPressure: source.diastolicPressure,
        bloodSugar: source.bloodSugar,
        temperature: String(source.temperature),
        bloodOxygen: source.bloodOxygen,
        sleepHours: source.sleepHours,
        status,
        measuredAt: source.measuredAt || '刚刚',
        remark: source.remark,
        riskReason: getHealthRiskReason(source),
      }

      this.healthRecords.unshift(localRecord)

      try {
        const savedRecord = await createHealthRecord({
          elderlyId: source.elderlyId,
          elderlyName: source.elderName,
          retestWarningId: source.retestWarningId,
          systolicPressure: source.systolicPressure,
          diastolicPressure: source.diastolicPressure,
          bloodSugar: source.bloodSugar,
          heartRate: source.heartRate,
          temperature: source.temperature,
          recordTime: apiDateTimeNow(),
          remark: source.remark || localRecord.riskReason,
        })
        const normalizedRecord = toHealthRecord(savedRecord)
        this.healthRecords = this.healthRecords.map((item) =>
          item.id === nextId ? normalizedRecord : item,
        )
        if (source.retestWarningId || localRecord.status !== '正常') {
          await this.fetchAlerts()
        }
        this.error = ''
        return normalizedRecord
      } catch (error) {
        this.healthRecords = this.healthRecords.filter((item) => item.id !== nextId)
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    async markHealthNormal(id: number) {
      const record = this.healthRecords.find((item) => item.id === id)
      if (!record) return
      if (this.alerts.length === 0) {
        try { await this.fetchAlerts() } catch { /* 静默失败 */ }
      }
      const healthTypes = ['血压异常','心率异常','血糖异常','体温异常','体征复测','体征异常','健康预警']
      const relatedAlerts = this.alerts.filter(
        (alert) =>
          alert.status !== '已处理' &&
          healthTypes.includes(alert.type) &&
          alert.elderlyId === record.elderlyId,
      )
      if (relatedAlerts.length === 0) {
        this.healthRecords = this.healthRecords.filter((item) => item.id !== id)
        return
      }
      for (const alert of relatedAlerts) {
        await updateHealthWarningStatus(alert.id, {
          status: 'resolved',
          handleResult: '复测后体征恢复正常，告警已归档。',
        })
        alert.status = '已处理'
        alert.handleResult = '复测后体征恢复正常，告警已归档。'
        alert.handledAt = '刚刚'
      }
      this.healthRecords = this.healthRecords.filter((item) => item.id !== id)
    },
    async removeHealthRecord(id: number) {
      const snapshot = [...this.healthRecords]
      this.healthRecords = this.healthRecords.filter((item) => item.id !== id)

      try {
        await deleteHealthRecord(id)
        this.error = ''
      } catch (error) {
        this.healthRecords = snapshot
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    async addManualAlert(payload?: ManualAlertInput) {
      const nextId = Math.max(...this.alerts.map((item) => item.id), 3000) + 1
      const source = payload || {
        elderlyId: 1,
        elderName: '刘玉梅',
        room: 'C-212',
        warningType: 'heart_rate' as const,
        warningLevel: 'medium' as const,
        warningContent: '护理员人工上报巡房提醒，请确认老人状态并记录处理结果。',
      }
      const mappedLevel = warningLevelMap[source.warningLevel]
      const localAlert: AlertRecord = {
        id: nextId,
        elderlyId: source.elderlyId,
        elderName: source.elderName,
        room: source.room,
        type: warningTypeMap[source.warningType] || '健康预警',
        level: mappedLevel,
        source: '人工上报',
        status: '待处理',
        time: '刚刚',
        owner: mappedLevel === '紧急' ? '当班医生' : '责任护理员',
        content: source.warningContent,
        contactPhone: '131****4468',
        slaMinutes: source.warningLevel === 'high' ? 5 : source.warningLevel === 'medium' ? 15 : 30,
      }

      this.alerts.unshift(localAlert)

      try {
        const savedAlert = await createHealthWarning({
          elderlyId: source.elderlyId || 1,
          warningType: source.warningType,
          warningLevel: source.warningLevel,
          warningContent: source.warningContent,
          warningTime: apiDateTimeNow(),
        })
        this.alerts = this.alerts.map((item) =>
          item.id === nextId ? toAlertRecord(savedAlert) : item,
        )
        this.error = ''
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      }
    },
    async startAlert(id: number) {
      const alert = this.alerts.find((item) => item.id === id)
      if (alert && alert.status === '待处理') {
        alert.status = '处理中'
        alert.handleResult = alert.handleResult || '已确认告警，正在现场核实。'

        try {
          await updateHealthWarningStatus(id, { status: toWarningStatus(alert.status) })
          this.error = ''
        } catch (error) {
          this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        }
      }
    },
    async resolveAlert(id: number, handleResult = '前端已标记处理完成') {
      const alert = this.alerts.find((item) => item.id === id)
      if (alert) {
        alert.status = '已处理'
        alert.handleResult = handleResult
        alert.handledAt = '刚刚'

        try {
          await updateHealthWarningStatus(id, {
            status: 'resolved',
            handleResult,
          })
          this.error = ''
        } catch (error) {
          this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        }
      }
    },
    escalateAlert(id: number) {
      const alert = this.alerts.find((item) => item.id === id)
      if (alert && alert.status !== '已处理') {
        alert.level = '紧急'
        alert.escalation = true
        alert.owner = '护理管理员 / 当班医生'
        alert.slaMinutes = 5
        alert.handleResult = alert.handleResult || '已升级通知护理管理员和当班医生。'
      }
    },
    async removeAlert(id: number) {
      const snapshot = [...this.alerts]
      this.alerts = this.alerts.filter((item) => item.id !== id)

      try {
        await deleteHealthWarning(id)
        this.error = ''
      } catch (error) {
        this.alerts = snapshot
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    async recoverDevice(id: string) {
      const device = this.devices.find((item) => item.id === id)
      if (device) {
        device.status = '在线'
        device.battery = Math.max(device.battery, 80)
        device.lastSync = '刚刚'

        try {
          await updateDeviceStatus(device.apiId || id, 'normal')
          this.error = ''
        } catch (error) {
          this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        }
      }
    },
    async createCareRecord(payload: CareRecordPayload) {
      const nextId = Math.max(...this.careRecords.map((item) => item.id), 0) + 1
      const localRecord: CareRecord = {
        id: nextId,
        elderlyId: payload.elderlyId,
        elderName: `老人ID-${payload.elderlyId}`,
        room: `ID-${payload.elderlyId}`,
        type: careTypeLabel(payload.careType),
        content: payload.careContent,
        caregiver: payload.caregiver || '—',
        status: '已完成',
        time: payload.careTime,
        remark: payload.remark,
      }
      this.careRecords.unshift(localRecord)

      try {
        const savedRecord = toCareRecord(await createCareRecord(payload))
        this.careRecords = this.careRecords.map((item) =>
          item.id === nextId ? savedRecord : item,
        )
        this.error = ''
        return savedRecord
      } catch (error) {
        this.careRecords = this.careRecords.filter((item) => item.id !== nextId)
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    async updateCareRecordById(id: number, payload: CareRecordPayload) {
      const savedRecord = toCareRecord(await updateCareRecord(id, payload))
      this.careRecords = this.careRecords.map((item) =>
        item.id === id ? savedRecord : item,
      )
      this.error = ''
    },
    async deleteCareRecordById(id: number) {
      const snapshot = [...this.careRecords]
      this.careRecords = this.careRecords.filter((item) => item.id !== id)
      try {
        await deleteCareRecord(id)
        this.error = ''
      } catch (error) {
        this.careRecords = snapshot
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    async createAppointment(payload: AppointmentPayload) {
      const nextId = Math.max(...this.serviceAppointments.map((item) => item.id), 0) + 1
      const localRecord: ServiceAppointment = {
        id: nextId,
        elderlyId: payload.elderlyId,
        elderName: `老人ID-${payload.elderlyId}`,
        room: `ID-${payload.elderlyId}`,
        service: serviceTypeLabel(payload.serviceType),
        requester: '系统登记',
        scheduledAt: payload.appointmentTime,
        status: '未完成',
        assignee: payload.doctorName || '待分配',
        description: payload.description,
      }
      this.serviceAppointments.unshift(localRecord)

      try {
        const savedRecord = toServiceAppointment(await createAppointment(payload))
        this.serviceAppointments = this.serviceAppointments.map((item) =>
          item.id === nextId ? savedRecord : item,
        )
        this.error = ''
        return savedRecord
      } catch (error) {
        this.serviceAppointments = this.serviceAppointments.filter((item) => item.id !== nextId)
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    async updateAppointmentById(id: number, payload: AppointmentPayload) {
      const savedRecord = toServiceAppointment(await updateAppointment(id, payload))
      this.serviceAppointments = this.serviceAppointments.map((item) =>
        item.id === id ? savedRecord : item,
      )
      this.error = ''
    },
    async updateAppointmentStatus(id: number, status: AppointmentStatus, cancelReason?: string) {
      const localStatus = normalizedAppointmentStatus(status)
      // 乐观更新：先改本地，再调 API
      this.serviceAppointments = this.serviceAppointments.map((item) =>
        item.id === id ? { ...item, status: localStatus, cancelReason: cancelReason || item.cancelReason } : item,
      )
      try {
        const result = await patchAppointmentStatus(id, { status, cancelReason })
        // 用 API 返回的真实数据替换（如果有的话）
        if (result && result.id) {
          const savedRecord = toServiceAppointment(result)
          this.serviceAppointments = this.serviceAppointments.map((item) =>
            item.id === id ? savedRecord : item,
          )
        }
      } catch {
        // API 失败则重新拉取，回滚乐观更新
        try { await this.fetchServiceAppointments() } catch { /* 静默 */ }
        throw new Error('状态更新失败，请刷新后重试')
      }
      this.error = ''
    },
    async deleteAppointmentById(id: number) {
      const snapshot = [...this.serviceAppointments]
      this.serviceAppointments = this.serviceAppointments.filter((item) => item.id !== id)
      try {
        await deleteAppointment(id)
        this.error = ''
      } catch (error) {
        this.serviceAppointments = snapshot
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
    toggleRole(id: number) {
      const role = this.roles.find((item) => item.id === id)
      if (role) {
        role.enabled = !role.enabled
      }
    },
    toggleNotificationRule(id: number) {
      const rule = this.notificationRules.find((item) => item.id === id)
      if (rule) {
        rule.enabled = !rule.enabled
      }
    },
  },
})
