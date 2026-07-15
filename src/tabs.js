import { PhHouse, PhCalendarCheck, PhHeart, PhUser } from '@phosphor-icons/vue'

export const mainTabs = [
  { path: '/home', label: '首页', icon: PhHouse, exact: true },
  { path: '/appointments', label: '预约', icon: PhCalendarCheck },
  { path: '/bind', label: '家人', icon: PhHeart, exact: true },
  { path: '/profile', label: '我的', icon: PhUser, exact: true },
]
