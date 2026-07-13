import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { getAuthToken, startAccountCheck } from './api/http'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 已有登录态时启动账号有效性格检查
if (getAuthToken()) {
  startAccountCheck()
}

app.mount('#app')
