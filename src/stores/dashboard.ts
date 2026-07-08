import { defineStore } from 'pinia'

export type Tone = 'brand' | 'success' | 'warning' | 'error' | 'gray'

export interface DashboardMetric {
  key: 'elders' | 'alerts' | 'devices' | 'tasks'
  label: string
  value: string
  unit: string
  detail: string
  trend: string
  tone: Tone
}

export interface ElderStatus {
  id: number
  name: string
  age: number
  room: string
  heartRate: number
  bloodPressure: string
  temperature: string
  status: '正常' | '需关注' | '异常' | '离线'
  caregiver: string
}

export interface AlertItem {
  id: number
  elder: string
  room: string
  title: string
  level: '紧急' | '关注' | '普通'
  time: string
  handled: boolean
}

export interface CareTask {
  id: number
  time: string
  title: string
  area: string
  owner: string
  status: '待处理' | '进行中' | '已完成'
}

export interface DeviceStatus {
  id: number
  name: string
  online: number
  total: number
  tone: Tone
}

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    metrics: [
      {
        key: 'elders',
        label: '在管老人',
        value: '128',
        unit: '人',
        detail: '较昨日新增 3 人',
        trend: '+2.4%',
        tone: 'brand',
      },
      {
        key: 'alerts',
        label: '今日告警',
        value: '12',
        unit: '条',
        detail: '3 条待优先处理',
        trend: '+4',
        tone: 'error',
      },
      {
        key: 'devices',
        label: '在线设备',
        value: '342',
        unit: '台',
        detail: '在线率 98.3%',
        trend: '稳定',
        tone: 'success',
      },
      {
        key: 'tasks',
        label: '待完成护理',
        value: '27',
        unit: '项',
        detail: '今日任务 89 项',
        trend: '73%',
        tone: 'warning',
      },
    ] satisfies DashboardMetric[],
    elders: [
      {
        id: 1,
        name: '王建国',
        age: 78,
        room: 'A-302',
        heartRate: 76,
        bloodPressure: '126/82',
        temperature: '36.5',
        status: '正常',
        caregiver: '刘护士',
      },
      {
        id: 2,
        name: '李桂兰',
        age: 82,
        room: 'B-118',
        heartRate: 108,
        bloodPressure: '150/96',
        temperature: '37.4',
        status: '需关注',
        caregiver: '周护士',
      },
      {
        id: 3,
        name: '陈秀英',
        age: 75,
        room: 'A-210',
        heartRate: 72,
        bloodPressure: '118/76',
        temperature: '36.3',
        status: '正常',
        caregiver: '刘护士',
      },
      {
        id: 4,
        name: '赵德明',
        age: 86,
        room: 'C-405',
        heartRate: 58,
        bloodPressure: '102/64',
        temperature: '36.1',
        status: '离线',
        caregiver: '王护工',
      },
      {
        id: 5,
        name: '张淑芬',
        age: 80,
        room: 'A-116',
        heartRate: 94,
        bloodPressure: '138/88',
        temperature: '36.8',
        status: '异常',
        caregiver: '周护士',
      },
    ] satisfies ElderStatus[],
    alerts: [
      {
        id: 1,
        elder: '李桂兰',
        room: 'B-118',
        title: '心率持续偏高',
        level: '紧急',
        time: '2 分钟前',
        handled: false,
      },
      {
        id: 2,
        elder: '赵德明',
        room: 'C-405',
        title: '智能手环离线',
        level: '关注',
        time: '8 分钟前',
        handled: false,
      },
      {
        id: 3,
        elder: '张淑芬',
        room: 'A-116',
        title: '夜间离床超过 15 分钟',
        level: '关注',
        time: '18 分钟前',
        handled: true,
      },
      {
        id: 4,
        elder: '陈秀英',
        room: 'A-210',
        title: '血压复测提醒',
        level: '普通',
        time: '32 分钟前',
        handled: true,
      },
    ] satisfies AlertItem[],
    careTasks: [
      {
        id: 1,
        time: '08:30',
        title: 'A 区老人晨间体征记录',
        area: 'A 区',
        owner: '刘护士',
        status: '已完成',
      },
      {
        id: 2,
        time: '10:00',
        title: 'B-118 服药提醒',
        area: 'B 区',
        owner: '周护士',
        status: '进行中',
      },
      {
        id: 3,
        time: '14:30',
        title: 'C 区康复训练安排',
        area: 'C 区',
        owner: '王护工',
        status: '待处理',
      },
      {
        id: 4,
        time: '16:00',
        title: '晚间巡房准备',
        area: '全院',
        owner: '护理组',
        status: '待处理',
      },
    ] satisfies CareTask[],
    deviceStatuses: [
      {
        id: 1,
        name: '智能手环',
        online: 126,
        total: 128,
        tone: 'success',
      },
      {
        id: 2,
        name: '睡眠监测垫',
        online: 82,
        total: 86,
        tone: 'brand',
      },
      {
        id: 3,
        name: '门磁传感器',
        online: 64,
        total: 67,
        tone: 'warning',
      },
      {
        id: 4,
        name: '紧急呼叫按钮',
        online: 70,
        total: 71,
        tone: 'success',
      },
    ] satisfies DeviceStatus[],
    alertTrend: [3, 5, 2, 8, 4, 6, 12],
    healthTrend: {
      categories: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      heartRate: [74, 76, 75, 78, 79, 77, 76],
      bloodPressure: [124, 126, 125, 130, 132, 128, 126],
    },
    careCompletionRate: 73,
  }),
})
