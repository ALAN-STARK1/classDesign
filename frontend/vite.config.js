import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_')
  const aiToken = env.VITE_AI_INTERNAL_TOKEN || 'change-me'

  return {
    plugins: [vue()],
    server: {
      proxy: {
        '/ai-bridge': {
          target: 'http://localhost:8000',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/ai-bridge/, '/ai/v1'),
          configure: (proxy) => {
            proxy.on('proxyReq', (proxyReq) => {
              proxyReq.setHeader('X-Internal-Token', aiToken)
            })
          },
        },
      },
    },
  }
})
