import { createRouter, createWebHistory } from 'vue-router'
import { getStoredUser,getAuthToken } from '@/api/http'
import { canAccessPath, getRoleLandingPath, publicPaths } from '@/config/roles'

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
        title: '工作台',
        subtitle: '集中查看岗位相关的关键信息',
      },
    },
    {
      path: '/dashboard/admin',
      name: 'AdminDashboard',
      component: () => import('../views/dashboards/AdminDashboard.vue'),
      meta: {
        title: '运营总览',
        subtitle: '集中查看平台告警、设备在线、账号审核和关键运营指标',
      },
    },
    {
      path: '/dashboard/nurse',
      name: 'NurseDashboard',
      component: () => import('../views/dashboards/NurseDashboard.vue'),
      meta: {
        title: '监护总览',
        subtitle: '集中查看老人状态、实时告警、设备在线和今日护理任务',
      },
    },
    {
      path: '/dashboard/doctor',
      name: 'DoctorDashboard',
      component: () => import('../views/dashboards/DoctorDashboard.vue'),
      meta: {
        title: '医疗总览',
        subtitle: '集中查看异常体征、复测队列和健康预警',
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
      component: () => import('../views/HealthView.vue'),
      meta: {
        title: '健康监测',
        subtitle: '查看心率、血压、体温、睡眠和活动趋势',
      },
    },
    {
      path: '/alerts',
      name: 'AlertsCenter',
      component: () => import('../views/AlertsCenterView.vue'),
      meta: {
        title: '告警中心',
        subtitle: '处理摔倒、离床、心率异常和设备离线等告警',
      },
    },
    {
      path: '/alerts/:id',
      name: 'AlertDetail',
      component: () => import('../views/AlertDetailView.vue'),
      meta: { title: '告警详情', subtitle: '查看并编辑告警信息' },
    },
    {
      path: '/devices',
      name: 'Devices',
      component: () => import('../views/DevicesView.vue'),
      meta: {
        title: '设备管理',
        subtitle: '管理智能手环、睡眠监测垫、门磁和呼叫按钮',
      },
    },
    {
      path: '/care-records',
      name: 'CareRecords',
      component: () => import('../views/CareRecordsView.vue'),
      meta: {
        title: '护理记录',
        subtitle: '记录巡房、服药、康复训练和异常处置过程',
      },
    },
    {
      path: '/services',
      name: 'Services',
      component: () => import('../views/ServicesView.vue'),
      meta: {
        title: '服务预约',
        subtitle: '安排上门护理、康复训练、健康咨询和陪诊服务',
      },
    },
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('../views/SettingsView.vue'),
      meta: {
        title: '系统设置',
        subtitle: '配置机构、角色权限、通知规则和基础字典',
      },
    },
    {
      path: '/accounts',
      name: 'Accounts',
      component: () => import('../views/AccountsView.vue'),
      meta: { title: '账号管理', subtitle: '审核、启用、禁用和删除工作人员账号' },
    },
    {
      path: '/reports',
      name: 'Reports',
      component: () => import('../views/ReportsView.vue'),
      meta: { title: '评估报告', subtitle: '查看看老人健康评估报告和风险建议' },
    },
    {
      path: '/populations',
      name: 'Populations',
      component: () => import('../views/PopulationsView.vue'),
      meta: { title: '重点人群', subtitle: '管理慢性病、独居等重点关注对象' },
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
        public: true,
      },
    },
    {
      path: '/signup',
      name: 'Signup',
      component: () => import('../views/Auth/Signup.vue'),
      meta: {
        title: '注册',
        public: true,
      },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/Errors/FourZeroFour.vue'),
      meta: {
        title: '页面不存在',
        public: true,
      },
    },
  ],
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '智慧养老管理系统'} | 智慧养老管理系统`

  if (to.meta.public || publicPaths.includes(to.path)) {
    next()
    return
  }

  const token = getAuthToken()
  const currentUser = getStoredUser()

  if (!token) {
    next({
      path: '/signin',
      query: {
        redirect: to.fullPath,
      },
    })
    return
  }

  if (currentUser && !canAccessPath(currentUser.roleName, to.path)) {
    next(getRoleLandingPath(currentUser.roleName))
    return
  }

  next()
})

if (typeof window !== 'undefined') {
  window.addEventListener('auth:expired', () => {
    if (router.currentRoute.value.path !== '/signin') {
      router.push({
        path: '/signin',
        query: {
          expired: '1',
        },
      })
    }
  })
}

export default router
