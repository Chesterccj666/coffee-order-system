import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

//配置ElementPlus组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(ElementPlus) // 使用Element Plus

app.use(router)

app.mount('#app')
