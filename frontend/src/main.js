import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import { router } from './router'
import './style.css'

async function bootstrap() {
  if (import.meta.env.VITE_USE_MOCK === 'enabled') {
    const { setupMock } = await import('./mock')
    setupMock()
  }

  createApp(App).use(createPinia()).use(router).use(ElementPlus).mount('#app')
}

bootstrap()
