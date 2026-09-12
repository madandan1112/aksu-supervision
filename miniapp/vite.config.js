import { defineConfig } from 'vite'
import { createRequire } from 'module'

const require = createRequire(import.meta.url)
const uni = require('@dcloudio/vite-plugin-uni').default

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 5174,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
