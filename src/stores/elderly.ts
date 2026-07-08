import { defineStore } from 'pinia'

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
}

export const useElderlyStore = defineStore('elderly', {
  state: () => ({
    records: [
      {
        id: 1001,
        name: '王建国',
        gender: '男',
        age: 78,
        room: 'A-302',
        careLevel: '二级照护',
        status: '正常',
        diseases: ['高血压', '糖尿病'],
        contactName: '王明',
        contactPhone: '138****5261',
        caregiver: '刘护士',
        lastCheck: '今日 08:42',
        admissionDate: '2024-03-16',
      },
      {
        id: 1002,
        name: '李桂兰',
        gender: '女',
        age: 82,
        room: 'B-118',
        careLevel: '特级照护',
        status: '需关注',
        diseases: ['冠心病', '高血压'],
        contactName: '李慧',
        contactPhone: '136****9028',
        caregiver: '周护士',
        lastCheck: '今日 09:10',
        admissionDate: '2023-11-02',
      },
      {
        id: 1003,
        name: '陈秀英',
        gender: '女',
        age: 75,
        room: 'A-210',
        careLevel: '一级照护',
        status: '正常',
        diseases: ['骨质疏松'],
        contactName: '陈晓',
        contactPhone: '139****3618',
        caregiver: '刘护士',
        lastCheck: '今日 08:20',
        admissionDate: '2024-06-21',
      },
      {
        id: 1004,
        name: '赵德明',
        gender: '男',
        age: 86,
        room: 'C-405',
        careLevel: '三级照护',
        status: '离线',
        diseases: ['阿尔茨海默症', '慢阻肺'],
        contactName: '赵磊',
        contactPhone: '137****6612',
        caregiver: '王护工',
        lastCheck: '昨日 19:35',
        admissionDate: '2022-09-08',
      },
      {
        id: 1005,
        name: '张淑芬',
        gender: '女',
        age: 80,
        room: 'A-116',
        careLevel: '三级照护',
        status: '异常',
        diseases: ['高血压', '睡眠障碍'],
        contactName: '张婷',
        contactPhone: '135****0196',
        caregiver: '周护士',
        lastCheck: '今日 07:58',
        admissionDate: '2023-04-13',
      },
      {
        id: 1006,
        name: '孙保国',
        gender: '男',
        age: 73,
        room: 'B-206',
        careLevel: '一级照护',
        status: '正常',
        diseases: ['关节炎'],
        contactName: '孙雨',
        contactPhone: '132****7710',
        caregiver: '郑护士',
        lastCheck: '今日 09:26',
        admissionDate: '2025-01-05',
      },
      {
        id: 1007,
        name: '刘玉梅',
        gender: '女',
        age: 84,
        room: 'C-212',
        careLevel: '特级照护',
        status: '需关注',
        diseases: ['糖尿病', '视力障碍'],
        contactName: '刘强',
        contactPhone: '131****4468',
        caregiver: '王护工',
        lastCheck: '今日 09:02',
        admissionDate: '2022-12-19',
      },
    ] as ElderRecord[],
  }),
  getters: {
    totalCount: (state) => state.records.length,
    attentionCount: (state) =>
      state.records.filter((record) => record.status === '需关注' || record.status === '异常').length,
    specialCareCount: (state) =>
      state.records.filter((record) => record.careLevel === '特级照护').length,
    offlineCount: (state) => state.records.filter((record) => record.status === '离线').length,
  },
  actions: {
    addRecord(record: Omit<ElderRecord, 'id' | 'lastCheck' | 'admissionDate'>) {
      const nextId = Math.max(...this.records.map((item) => item.id), 1000) + 1
      const today = new Date().toISOString().slice(0, 10)

      this.records.unshift({
        ...record,
        id: nextId,
        lastCheck: '待采集',
        admissionDate: today,
      })
    },
  },
})
