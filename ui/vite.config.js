import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ command, mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  
  // 使用环境变量或默认值
  const apiTarget = env.VITE_API_BASE_URL || (
    command === 'serve' 
      ? 'http://127.0.0.1:8080'  // 开发模式使用本地
      : 'http://117.72.145.103:8080'  // 生产模式使用远程
  )

  return {
    plugins: [vue()],
    server: {
      port: 8081,
      proxy: {
        '/api': {
          target: apiTarget,
          changeOrigin: true,
          // 不再重写路径，保持/api前缀
          // rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    },
  resolve: {
      alias: {
        '@': '/src'
      }
    },
    build: {
      minify: 'terser',
      sourcemap: false,
      chunkSizeWarningLimit: 1000,
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (id.includes('node_modules')) {
              const packageName = id.toString().split('node_modules/')[1].split('/')[0];
              if (['element-plus', 'vue', 'vue-router', 'pinia', 'axios', '@element-plus/icons-vue', 'dayjs'].includes(packageName)) {
                return packageName;
              }
              return 'vendor';
            }
          },
        },
      },
    }
  }
})