import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

import Login from './pages/Login.vue'
import Register from './pages/Register.vue'
import Home from './pages/Home.vue'
import ElderlyDetail from './pages/ElderlyDetail.vue'
import Appointments from './pages/Appointments.vue'
import NewAppointment from './pages/NewAppointment.vue'
import Profile from './pages/Profile.vue'
import BindElderly from './pages/BindElderly.vue'
import NewElderly from './pages/NewElderly.vue'
import HealthRecords from './pages/HealthRecords.vue'
import GsapDemo from './pages/GsapDemo.vue'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: Login, meta: { guest: true } },
  { path: '/register', component: Register, meta: { guest: true } },
  { path: '/home', component: Home, meta: { auth: true } },
  { path: '/elderly/:id', component: ElderlyDetail, meta: { auth: true } },
  { path: '/elderly/:id/health', component: HealthRecords, meta: { auth: true } },
  { path: '/appointments', component: Appointments, meta: { auth: true } },
  { path: '/appointments/new', component: NewAppointment, meta: { auth: true } },
  { path: '/profile', component: Profile, meta: { auth: true } },
  { path: '/bind', component: BindElderly, meta: { auth: true } },
  { path: '/elderly/new', component: NewElderly, meta: { auth: true } },
  { path: '/gsap-demo', component: GsapDemo },
]

const router = createRouter({
  history: createWebHistory('/family/'),
  routes,
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.auth && !token) return '/login'
  if (to.meta.guest && token) return '/home'
})

const app = createApp(App)
app.use(router)
app.mount('#app')
