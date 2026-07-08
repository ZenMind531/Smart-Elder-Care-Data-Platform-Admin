import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { left: 0, top: 0 }
  },
  routes: [
    {
      path: '/',
      name: 'Dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: {
        title: '监护总览',
        subtitle: '集中查看老人状态、实时告警、设备在线和今日护理任务',
      },
    },
    {
      path: '/elderly',
      name: 'Elderly',
      component: () => import('../views/ElderlyView.vue'),
      meta: {
        title: '老人档案',
        subtitle: '维护老人基础资料、房间、联系人、慢病和照护等级',
      },
    },
    {
      path: '/health',
      name: 'Health',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '健康监测',
        subtitle: '查看心率、血压、体温、睡眠和活动趋势',
      },
    },
    {
      path: '/alerts',
      name: 'AlertsCenter',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '告警中心',
        subtitle: '处理摔倒、离床、心率异常和设备离线等告警',
      },
    },
    {
      path: '/devices',
      name: 'Devices',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '设备管理',
        subtitle: '管理智能手环、睡眠监测垫、门磁和呼叫按钮',
      },
    },
    {
      path: '/care-records',
      name: 'CareRecords',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '护理记录',
        subtitle: '记录巡房、服药、康复训练和异常处置过程',
      },
    },
    {
      path: '/services',
      name: 'Services',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '服务预约',
        subtitle: '安排上门护理、康复训练、健康咨询和陪诊服务',
      },
    },
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '系统设置',
        subtitle: '配置机构、角色权限、通知规则和基础字典',
      },
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/PlaceholderView.vue'),
      meta: {
        title: '个人中心',
        subtitle: '查看当前账号、值班班组、消息偏好和登录安全设置',
      },
    },
    {
      path: '/signin',
      name: 'Signin',
      component: () => import('../views/Auth/Signin.vue'),
      meta: {
        title: '登录',
      },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/Errors/FourZeroFour.vue'),
      meta: {
        title: '页面不存在',
      },
    },
  ],
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '智慧养老管理系统'} | 智慧养老管理系统`
  next()
})

export default router
