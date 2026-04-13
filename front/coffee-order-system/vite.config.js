import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    // 允许局域网内的其他用户访问到开发服务器
    // host: '0.0.0.0',  // 允许外部访问
    port: 5173,       // 开发服务器端口

    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        //target: 'http://10.112.73.21:8080',  // 局域网访问
        changeOrigin: true,
      },
      '/static': {
        target: 'http://localhost:8080',
        //target: 'http://10.112.73.21:8080',  // 局域网访问
        changeOrigin: true,
      },
      '/upload': {
        target: 'http://localhost:8080',
        //target: 'http://10.112.73.21:8080',  // 局域网访问
        changeOrigin: true,
      }
    }
  }
}
)