<template>
  <div class="min-h-[100dvh] bg-canvas flex flex-col justify-center px-8 py-12">
    <div class="text-center mb-10">
      <h1 class="text-ink text-[40px] font-semibold tracking-[-0.28px] leading-[1.1]">CarePulse</h1>
      <p class="text-muted mt-3 text-[17px]">注册家属账号</p>
    </div>

    <div
      v-if="fieldErrors._general"
      class="bg-error/10 text-error text-sm rounded-lg px-4 py-3 mb-6"
    >{{ fieldErrors._general }}</div>

    <form @submit.prevent="handleRegister" class="space-y-4" novalidate>
      <!-- Username -->
      <div>
        <label class="block text-[14px] font-medium text-ink mb-1.5 tracking-[-0.224px]">用户名</label>
        <input
          v-model="form.username"
          type="text"
          maxlength="20"
          placeholder="4-20位字符"
          :class="inputClass('username')"
        />
        <p v-if="fieldError('username')" class="mt-1 text-[12px] text-error">{{ fieldError('username') }}</p>
        <p v-else class="mt-1 text-[12px] text-muted">4-20位</p>
      </div>

      <!-- Real Name -->
      <div>
        <label class="block text-[14px] font-medium text-ink mb-1.5 tracking-[-0.224px]">真实姓名</label>
        <input
          v-model="form.realName"
          type="text"
          placeholder="请输入真实姓名"
          :class="inputClass('realName')"
        />
        <p v-if="fieldError('realName')" class="mt-1 text-[12px] text-error">{{ fieldError('realName') }}</p>
        <p v-else class="mt-1 text-[12px] text-muted">必填</p>
      </div>

      <!-- Phone -->
      <div>
        <label class="block text-[14px] font-medium text-ink mb-1.5 tracking-[-0.224px]">手机号</label>
        <input
          v-model="form.phoneNumber"
          type="tel"
          maxlength="11"
          placeholder="11位手机号"
          :class="inputClass('phoneNumber')"
        />
        <p v-if="fieldError('phoneNumber')" class="mt-1 text-[12px] text-error">{{ fieldError('phoneNumber') }}</p>
        <p v-else class="mt-1 text-[12px] text-muted">11位手机号，1开头</p>
      </div>

      <!-- Password -->
      <div>
        <label class="block text-[14px] font-medium text-ink mb-1.5 tracking-[-0.224px]">密码</label>
        <div class="relative">
          <input
            v-model="form.password"
            :type="showPwd ? 'text' : 'password'"
            placeholder="8-20位，含字母和数字"
            :class="inputClass('password')"
            class="pr-10"
          />
          <button
            type="button"
            @click="showPwd = !showPwd"
            class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-soft hover:text-muted transition-colors"
            aria-label="切换密码可见"
          >
            <PhEyeSlash v-if="showPwd" :size="18" />
            <PhEye v-else :size="18" />
          </button>
        </div>
        <p v-if="fieldError('password')" class="mt-1 text-[12px] text-error">{{ fieldError('password') }}</p>
        <p v-else class="mt-1 text-[12px] text-muted">8-20位，必须包含字母和数字</p>
      </div>

      <!-- Apple pill CTA -->
      <button
        type="submit"
        :disabled="loading"
        class="w-full h-12 rounded-full bg-primary text-white font-medium text-[17px] active:scale-95 transition-all hover:bg-primary-active disabled:bg-primary-disabled disabled:text-muted mt-2"
      >
        {{ loading ? '注册中...' : '注册' }}
      </button>
    </form>

    <p class="text-center mt-8 text-[14px] text-muted tracking-[-0.224px]">
      已有账号？
      <router-link to="/login" class="text-primary font-medium">去登录</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { PhEye, PhEyeSlash } from '@phosphor-icons/vue'
import { auth } from '../api.js'

const router = useRouter()
const loading = ref(false)
const showPwd = ref(false)
const fieldErrors = reactive({})

const form = reactive({
  username: '',
  realName: '',
  phoneNumber: '',
  password: '',
})

function inputClass(field) {
  const hasErr = fieldErrors[field]
  return [
    'w-full h-11 px-4 rounded-lg border bg-white text-ink text-[17px] placeholder:text-muted focus:outline-none focus:ring-3 focus:ring-primary/10 transition-colors',
    hasErr ? 'border-error focus:border-error' : 'border-hairline focus:border-primary',
  ].join(' ')
}

function fieldError(field) {
  return fieldErrors[field] || ''
}

function parseFieldError(message) {
  const map = { username: 'username', phoneNumber: 'phoneNumber', realName: 'realName', password: 'password' }
  for (const key of Object.keys(map)) {
    if (message.toLowerCase().startsWith(key.toLowerCase())) {
      return { field: key, text: message.slice(key.length).trim() }
    }
  }
  return null
}

async function handleRegister() {
  Object.keys(fieldErrors).forEach((k) => delete fieldErrors[k])

  if (!form.realName.trim()) {
    fieldErrors.realName = '姓名不能为空'
    return
  }
  if (form.username.length < 4 || form.username.length > 20) {
    fieldErrors.username = '用户名长度为4-20位'
    return
  }
  if (!/^1\d{10}$/.test(form.phoneNumber)) {
    fieldErrors.phoneNumber = '手机号格式不正确（需要11位手机号）'
    return
  }
  if (form.password.length < 8 || form.password.length > 20) {
    fieldErrors.password = '密码长度为8-20位'
    return
  }
  if (!/[a-zA-Z]/.test(form.password) || !/\d/.test(form.password)) {
    fieldErrors.password = '密码必须包含字母和数字'
    return
  }

  loading.value = true

  try {
    await auth.register({
      username: form.username,
      realName: form.realName,
      phoneNumber: form.phoneNumber,
      password: form.password,
      roleId: 3,
    })
    router.push('/login')
  } catch (e) {
    if (e.code === 400) {
      const parsed = parseFieldError(e.message)
      if (parsed) {
        fieldErrors[parsed.field] = parsed.text
      } else {
        fieldErrors._general = e.message
      }
    } else {
      fieldErrors._general = e.message
    }
  } finally {
    loading.value = false
  }
}
</script>
