import { defineStore } from 'pinia'

export interface HealthRecord {
  id: number
  elderName: string
  room: string
  heartRate: number
  bloodPressure: string
  temperature: string
  bloodOxygen: number
  sleepHours: number
  status: '正常' | '需复测' | '异常'
  measuredAt: string
}

export interface AlertRecord {
  id: number
  elderName: string
  room: string
  type: string
  level: '紧急' | '关注' | '普通'
  source: string
  status: '待处理' | '处理中' | '已处理'
  time: string
  owner: string
}

export interface DeviceRecord {
  id: string
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
  elderName: string
  room: string
  type: string
  content: string
  caregiver: string
  status: '已完成' | '进行中' | '待补录'
  time: string
}

export interface ServiceAppointment {
  id: number
  elderName: string
  room: string
  service: string
  requester: string
  scheduledAt: string
  status: '待确认' | '已预约' | '服务中' | '已完成'
  assignee: string
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

export const useOperationsStore = defineStore('operations', {
  state: () => ({
    healthRecords: [
      {
        id: 1,
        elderName: '王建国',
        room: 'A-302',
        heartRate: 76,
        bloodPressure: '126/82',
        temperature: '36.5',
        bloodOxygen: 98,
        sleepHours: 7.2,
        status: '正常',
        measuredAt: '今日 08:42',
      },
      {
        id: 2,
        elderName: '李桂兰',
        room: 'B-118',
        heartRate: 108,
        bloodPressure: '150/96',
        temperature: '37.4',
        bloodOxygen: 94,
        sleepHours: 5.4,
        status: '异常',
        measuredAt: '今日 09:10',
      },
      {
        id: 3,
        elderName: '陈秀英',
        room: 'A-210',
        heartRate: 72,
        bloodPressure: '118/76',
        temperature: '36.3',
        bloodOxygen: 99,
        sleepHours: 7.8,
        status: '正常',
        measuredAt: '今日 08:20',
      },
      {
        id: 4,
        elderName: '赵德明',
        room: 'C-405',
        heartRate: 58,
        bloodPressure: '102/64',
        temperature: '36.1',
        bloodOxygen: 95,
        sleepHours: 4.9,
        status: '需复测',
        measuredAt: '昨日 19:35',
      },
      {
        id: 5,
        elderName: '张淑芬',
        room: 'A-116',
        heartRate: 94,
        bloodPressure: '138/88',
        temperature: '36.8',
        bloodOxygen: 97,
        sleepHours: 4.6,
        status: '需复测',
        measuredAt: '今日 07:58',
      },
    ] as HealthRecord[],
    alerts: [
      {
        id: 3001,
        elderName: '李桂兰',
        room: 'B-118',
        type: '心率异常',
        level: '紧急',
        source: '智能手环',
        status: '处理中',
        time: '今日 09:12',
        owner: '周护士',
      },
      {
        id: 3002,
        elderName: '赵德明',
        room: 'C-405',
        type: '设备离线',
        level: '关注',
        source: '智能手环',
        status: '待处理',
        time: '今日 08:54',
        owner: '王护工',
      },
      {
        id: 3003,
        elderName: '张淑芬',
        room: 'A-116',
        type: '夜间离床',
        level: '关注',
        source: '门磁传感器',
        status: '已处理',
        time: '今日 03:16',
        owner: '周护士',
      },
      {
        id: 3004,
        elderName: '陈秀英',
        room: 'A-210',
        type: '血压复测',
        level: '普通',
        source: '健康监测',
        status: '已处理',
        time: '昨日 18:40',
        owner: '刘护士',
      },
    ] as AlertRecord[],
    devices: [
      {
        id: 'WB-A302-01',
        name: '智能手环 A302',
        type: '智能手环',
        room: 'A-302',
        boundElder: '王建国',
        battery: 86,
        status: '在线',
        lastSync: '1 分钟前',
      },
      {
        id: 'WB-B118-01',
        name: '智能手环 B118',
        type: '智能手环',
        room: 'B-118',
        boundElder: '李桂兰',
        battery: 42,
        status: '在线',
        lastSync: '2 分钟前',
      },
      {
        id: 'SM-C405-01',
        name: '睡眠监测垫 C405',
        type: '睡眠监测垫',
        room: 'C-405',
        boundElder: '赵德明',
        battery: 0,
        status: '离线',
        lastSync: '58 分钟前',
      },
      {
        id: 'DM-A116-02',
        name: '门磁传感器 A116',
        type: '门磁传感器',
        room: 'A-116',
        boundElder: '张淑芬',
        battery: 19,
        status: '低电量',
        lastSync: '5 分钟前',
      },
      {
        id: 'EC-B206-01',
        name: '紧急呼叫按钮 B206',
        type: '紧急呼叫按钮',
        room: 'B-206',
        boundElder: '孙保国',
        battery: 72,
        status: '维护中',
        lastSync: '今日 08:20',
      },
    ] as DeviceRecord[],
    careRecords: [
      {
        id: 5001,
        elderName: '王建国',
        room: 'A-302',
        type: '晨间体征',
        content: '血压 126/82，体温 36.5，精神状态稳定。',
        caregiver: '刘护士',
        status: '已完成',
        time: '今日 08:42',
      },
      {
        id: 5002,
        elderName: '李桂兰',
        room: 'B-118',
        type: '服药提醒',
        content: '已提醒服用降压药，30 分钟后复测血压。',
        caregiver: '周护士',
        status: '进行中',
        time: '今日 10:00',
      },
      {
        id: 5003,
        elderName: '陈秀英',
        room: 'A-210',
        type: '康复训练',
        content: '完成下肢平衡训练 20 分钟。',
        caregiver: '刘护士',
        status: '已完成',
        time: '今日 14:30',
      },
      {
        id: 5004,
        elderName: '赵德明',
        room: 'C-405',
        type: '设备巡检',
        content: '手环离线，待设备管理员复联。',
        caregiver: '王护工',
        status: '待补录',
        time: '今日 15:20',
      },
    ] as CareRecord[],
    serviceAppointments: [
      {
        id: 7001,
        elderName: '李桂兰',
        room: 'B-118',
        service: '健康咨询',
        requester: '李慧',
        scheduledAt: '今日 15:30',
        status: '已预约',
        assignee: '张医生',
      },
      {
        id: 7002,
        elderName: '陈秀英',
        room: 'A-210',
        service: '康复训练',
        requester: '陈晓',
        scheduledAt: '明日 09:00',
        status: '待确认',
        assignee: '康复师王敏',
      },
      {
        id: 7003,
        elderName: '王建国',
        room: 'A-302',
        service: '陪诊服务',
        requester: '王明',
        scheduledAt: '7月10日 08:30',
        status: '已预约',
        assignee: '陪诊员刘洋',
      },
      {
        id: 7004,
        elderName: '孙保国',
        room: 'B-206',
        service: '上门护理',
        requester: '孙雨',
        scheduledAt: '今日 11:00',
        status: '已完成',
        assignee: '郑护士',
      },
    ] as ServiceAppointment[],
    roles: [
      {
        id: 1,
        name: '系统管理员',
        users: 2,
        scope: '系统配置、角色权限、机构资料',
        enabled: true,
      },
      {
        id: 2,
        name: '护理管理员',
        users: 6,
        scope: '老人档案、护理记录、告警处置',
        enabled: true,
      },
      {
        id: 3,
        name: '医生',
        users: 4,
        scope: '健康监测、健康评估、复测建议',
        enabled: true,
      },
      {
        id: 4,
        name: '家属账号',
        users: 38,
        scope: '个人端健康状态、服务预约、消息通知',
        enabled: true,
      },
      {
        id: 5,
        name: '设备管理员',
        users: 3,
        scope: '设备接入、在线状态、维护巡检',
        enabled: true,
      },
    ] as RoleRecord[],
    notificationRules: [
      {
        id: 1,
        name: '紧急告警即时通知',
        channel: '站内信 / 短信',
        target: '护理管理员、责任护理员',
        enabled: true,
      },
      {
        id: 2,
        name: '设备离线 10 分钟提醒',
        channel: '站内信',
        target: '设备管理员',
        enabled: true,
      },
      {
        id: 3,
        name: '每日护理任务汇总',
        channel: '站内信',
        target: '护理组长',
        enabled: true,
      },
    ] as NotificationRule[],
  }),
  getters: {
    unresolvedAlerts: (state) => state.alerts.filter((alert) => alert.status !== '已处理').length,
    onlineDevices: (state) => state.devices.filter((device) => device.status === '在线').length,
    pendingServices: (state) =>
      state.serviceAppointments.filter((service) => service.status === '待确认').length,
    todayCareDone: (state) =>
      state.careRecords.filter((record) => record.status === '已完成').length,
  },
  actions: {
    addHealthRecord() {
      const nextId = Math.max(...this.healthRecords.map((item) => item.id), 0) + 1

      this.healthRecords.unshift({
        id: nextId,
        elderName: '刘玉梅',
        room: 'C-212',
        heartRate: 82,
        bloodPressure: '128/80',
        temperature: '36.6',
        bloodOxygen: 97,
        sleepHours: 6.8,
        status: '正常',
        measuredAt: '刚刚',
      })
    },
    requestHealthRetest(id: number) {
      const record = this.healthRecords.find((item) => item.id === id)
      if (record) {
        record.status = '需复测'
        record.measuredAt = '刚刚'
      }
    },
    markHealthNormal(id: number) {
      const record = this.healthRecords.find((item) => item.id === id)
      if (record) {
        record.status = '正常'
        record.heartRate = Math.min(Math.max(record.heartRate, 70), 88)
        record.bloodOxygen = Math.max(record.bloodOxygen, 97)
        record.measuredAt = '刚刚'
      }
    },
    addManualAlert() {
      const nextId = Math.max(...this.alerts.map((item) => item.id), 3000) + 1

      this.alerts.unshift({
        id: nextId,
        elderName: '刘玉梅',
        room: 'C-212',
        type: '人工巡房提醒',
        level: '关注',
        source: '护理员上报',
        status: '待处理',
        time: '刚刚',
        owner: '王护工',
      })
    },
    startAlert(id: number) {
      const alert = this.alerts.find((item) => item.id === id)
      if (alert && alert.status === '待处理') {
        alert.status = '处理中'
      }
    },
    resolveAlert(id: number) {
      const alert = this.alerts.find((item) => item.id === id)
      if (alert) {
        alert.status = '已处理'
      }
    },
    recoverDevice(id: string) {
      const device = this.devices.find((item) => item.id === id)
      if (device) {
        device.status = '在线'
        device.battery = Math.max(device.battery, 80)
        device.lastSync = '刚刚'
      }
    },
    addCareRecord() {
      const nextId = Math.max(...this.careRecords.map((item) => item.id), 5000) + 1

      this.careRecords.unshift({
        id: nextId,
        elderName: '刘玉梅',
        room: 'C-212',
        type: '临时巡房',
        content: '已完成临时巡房，老人状态稳定，设备连接正常。',
        caregiver: '王护工',
        status: '已完成',
        time: '刚刚',
      })
    },
    addServiceAppointment() {
      const nextId = Math.max(...this.serviceAppointments.map((item) => item.id), 7000) + 1

      this.serviceAppointments.unshift({
        id: nextId,
        elderName: '刘玉梅',
        room: 'C-212',
        service: '康复训练',
        requester: '刘晨',
        scheduledAt: '明日 10:30',
        status: '待确认',
        assignee: '康复师王敏',
      })
    },
    confirmService(id: number) {
      const service = this.serviceAppointments.find((item) => item.id === id)
      if (service && service.status === '待确认') {
        service.status = '已预约'
      }
    },
    startService(id: number) {
      const service = this.serviceAppointments.find((item) => item.id === id)
      if (service && service.status === '已预约') {
        service.status = '服务中'
      }
    },
    completeService(id: number) {
      const service = this.serviceAppointments.find((item) => item.id === id)
      if (service) {
        service.status = '已完成'
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
