import {fileURLToPath, URL} from 'node:url'
import { resolve, dirname } from 'node:path'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        VueI18nPlugin({
            /* options */
            // locale messages resource pre-compile option
            include: resolve(dirname(fileURLToPath(import.meta.url)), './src/i18n/locales/*.json'),
            strictMessage: false
        }),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                rewrite: path => path.replace(/^\/api/, ''),
                secure: false,
                changeOrigin: true,
            },
        },
    }
})
