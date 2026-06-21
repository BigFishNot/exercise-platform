import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'
import path from 'node:path'

// Vite 配置：代理到管理端后端 8081，baseURL /api
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  const backendUrl = env.VITE_BACKEND_URL || 'http://127.0.0.1:8081'
  return {
    plugins: [
      vue(),
      AutoImport({ resolvers: [AntDesignVueResolver({ importStyle: false })] }),
      Components({ resolvers: [AntDesignVueResolver({ importStyle: false })] })
    ],
    resolve: {
      alias: { '@': path.resolve(process.cwd(), 'src') }
    },
    server: {
      port: Number(env.VITE_DEV_PORT) || 5174,
      proxy: {
        '/api': {
          target: backendUrl,
          changeOrigin: true
        }
      }
    },
    css: {
      preprocessorOptions: {
        scss: { additionalData: `@use "@/assets/styles/variables.scss" as *;` }
      }
    }
  }
})