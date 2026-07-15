<template>
  <div class="relative min-h-[100dvh] bg-canvas flex items-center justify-center px-5">
    <!-- Brand mark -->
    <div class="absolute top-0 left-0 pt-10 px-8">
      <div class="flex items-center gap-2.5">
        <div class="w-8 h-8 rounded-lg bg-primary flex items-center justify-center">
          <PhHeartbeat :size="20" weight="fill" class="text-white" />
        </div>
        <span class="text-ink text-2xl tracking-[-0.28px] font-semibold">CarePulse</span>
      </div>
    </div>

    <!-- Content block -->
    <div class="max-w-[420px] w-full">
      <h1 class="text-ink text-5xl leading-[1.07] tracking-[-0.28px] font-semibold text-center">
        关爱家人<br />健康
      </h1>
      <p class="text-center text-muted mt-4 text-[17px] leading-relaxed tracking-[-0.374px]">
        家属端 · 随时关注长辈健康
      </p>

      <!-- Apple-style card: 18px radius, white, hairline border, no shadow -->
      <div class="mt-10 bg-white rounded-[18px] border border-hairline p-7">
        <div
          v-if="error"
          class="mb-4 bg-error/8 text-error text-[13px] rounded-lg px-4 py-2.5"
        >
          {{ error }}
        </div>

        <form @submit.prevent="handleLogin" class="space-y-4">
          <input
            v-model="form.phone"
            type="tel"
            required
            placeholder="手机号"
            class="w-full h-[50px] px-4 rounded-lg border border-hairline bg-white text-ink text-[17px] placeholder:text-muted-soft focus:outline-none focus:border-primary focus:ring-3 focus:ring-primary/10 transition-colors"
          />
          <input
            v-model="form.password"
            type="password"
            required
            placeholder="密码"
            class="w-full h-[50px] px-4 rounded-lg border border-hairline bg-white text-ink text-[17px] placeholder:text-muted-soft focus:outline-none focus:border-primary focus:ring-3 focus:ring-primary/10 transition-colors"
          />
          <!-- Apple pill CTA -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full h-[50px] rounded-full bg-primary text-white text-[17px] font-medium active:scale-95 transition-all hover:bg-primary-active disabled:bg-primary-disabled disabled:text-muted"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
      </div>

      <p class="text-center mt-7 text-[14px] text-muted tracking-[-0.224px]">
        没有账号？
        <router-link to="/register" class="text-primary font-medium">注册家属账号</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { auth } from '../api.js'
import { PhHeartbeat } from '@phosphor-icons/vue'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const form = reactive({ phone: '', password: '' })

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    const data = await auth.login(form)
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({ id: data.id, name: data.name }))
    router.replace('/home')
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>
