import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // 开发期把 /api/* 反向代理到真后端，避开浏览器 CORS 限制
    proxy: {
      '/api': {
        target: 'https://smart-eldercare.online',
        changeOrigin: true,
        secure: true,
        // 给所有响应（即使后端 5xx/4xx）也加 CORS 头，避免浏览器把非 CORS 错误报成 CORS
        configure: (proxy) => {
          proxy.on('proxyRes', (proxyRes) => {
            proxyRes.headers['access-control-allow-origin'] = '*'
            proxyRes.headers['access-control-allow-headers'] = '*'
            proxyRes.headers['access-control-allow-methods'] = '*'
          })
        },
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-icons': ['lucide-vue-next'],
          'vendor-charts': ['vue3-apexcharts', 'apexcharts'],
        },
      },
    },
  },
})
