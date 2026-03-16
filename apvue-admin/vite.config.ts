import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  base: '/admin/',
  server: {
    host: '0.0.0.0', // 建议开启，确保 Nginx 能通过 IP 访问到它
    port: 5174      // 确保这里的端口和 Nginx配置里的 proxy_pass 一致

}
})
