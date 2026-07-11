import { defineStore } from 'pinia'
import {
  createElderly,
  listElderly,
  type ElderlyApiRecord,
  type ElderlyPayload,
  type ApiRiskLevel,
} from '@/api/elderly'

export type ElderStatus = '正常' | '需关注' | '异常' | '离线'
export type CareLevel = '一级照护' | '二级照护' | '三级照护' | '特级照护'

export interface ElderRecord {
  id: number
  name: string
  gender: '男' | '女'
  age: number
  room: string
  careLevel: CareLevel
  status: ElderStatus
  diseases: string[]
  contactName: string
  contactPhone: string
  caregiver: string
  lastCheck: string
  admissionDate: string
  idCard?: string
  phoneNumber?: string
  medicalHistory?: string
  allergyHistory?: string
}

const splitHealthTags = (record: ElderlyApiRecord) => {
  const tags = [record.medicalHistory, record.allergyHistory]
    .flatMap((value) => (value || '').split(/[、,，;；\s]+/))
    .map((value) => value.trim())
    .filter(Boolean)

  return tags.length ? tags : ['待完善']
}

const toDate = (value?: string) => {
  if (!value) return new Date().toISOString().slice(0, 10)
  return value.split(' ')[0] || value.slice(0, 10)
}

const riskToCareLevel = (riskLevel?: ApiRiskLevel): CareLevel => {
  if (riskLevel === 'high') return '特级照护'
  if (riskLevel === 'medium') return '三级照护'
  return '一级照护'
}

const riskToStatus = (riskLevel?: ApiRiskLevel): ElderStatus => {
  if (riskLevel === 'high') return '异常'
  if (riskLevel === 'medium') return '需关注'
  return '正常'
}

const statusToRisk = (status: ElderStatus, careLevel: CareLevel): ApiRiskLevel => {
  if (status === '异常' || careLevel === '特级照护') return 'high'
  if (status === '需关注' || careLevel === '三级照护') return 'medium'
  return 'low'
}

const toElderRecord = (record: ElderlyApiRecord): ElderRecord => ({
  id: record.id,
  name: record.elderlyName || `老人 ${record.id}`,
  gender: record.gender === 'male' ? '男' : '女',
  age: record.age || 0,
  room: record.address || '未填写',
  careLevel: riskToCareLevel(record.riskLevel),
  status: riskToStatus(record.riskLevel),
  diseases: splitHealthTags(record),
  contactName: record.emergencyContact || '未填写',
  contactPhone: record.emergencyPhone || record.phoneNumber || '未填写',
  caregiver: record.caregiverName || '待分配',
  lastCheck: record.updateTime || record.createTime || '接口同步',
  admissionDate: toDate(record.createTime),
  idCard: record.idCard,
  phoneNumber: record.phoneNumber,
  medicalHistory: record.medicalHistory,
  allergyHistory: record.allergyHistory,
})

const toElderlyPayload = (record: Omit<ElderRecord, 'id' | 'lastCheck' | 'admissionDate'>): ElderlyPayload => ({
  elderlyName: record.name,
  gender: record.gender === '男' ? 'male' : 'female',
  age: record.age,
  idCard: record.idCard || '',
  phoneNumber: record.phoneNumber || record.contactPhone,
  address: record.room,
  emergencyContact: record.contactName,
  emergencyPhone: record.contactPhone,
  medicalHistory:
    record.medicalHistory || record.diseases.filter((item) => item !== '待完善').join('、') || '',
  allergyHistory: record.allergyHistory || '',
  riskLevel: statusToRisk(record.status, record.careLevel),
})

export const useElderlyStore = defineStore('elderly', {
  state: () => ({
    records: [] as ElderRecord[],
    loading: false,
    error: '',
  }),
  getters: {
    totalCount: (state) => state.records.length,
    attentionCount: (state) =>
      state.records.filter((record) => record.status === '需关注' || record.status === '异常').length,
    specialCareCount: (state) => state.records.filter((record) => record.careLevel === '特级照护').length,
    offlineCount: (state) => state.records.filter((record) => record.status === '离线').length,
  },
  actions: {
    async fetchRecords() {
      this.loading = true
      this.error = ''

      try {
        const result = await listElderly({ page: 1, size: 100 })
        this.records = result.records.map(toElderRecord)
      } catch (error) {
        this.records = []
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    async addRecord(record: Omit<ElderRecord, 'id' | 'lastCheck' | 'admissionDate'>) {
      const nextId = Math.max(...this.records.map((item) => item.id), 1000) + 1
      const today = new Date().toISOString().slice(0, 10)
      const localRecord: ElderRecord = {
        ...record,
        id: nextId,
        lastCheck: '待采集',
        admissionDate: today,
      }

      this.records.unshift(localRecord)

      try {
        const savedRecord = await createElderly(toElderlyPayload(record))
        this.records = this.records.map((item) =>
          item.id === nextId ? toElderRecord(savedRecord) : item,
        )
        this.error = ''
      } catch (error) {
        this.error = error instanceof Error ? error.message : '服务器响应异常，请稍后重试'
        throw error
      }
    },
  },
})
