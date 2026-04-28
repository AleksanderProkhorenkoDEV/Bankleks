import { defineConfig } from 'vite'

export default defineConfig({
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure:false,
                cookieDomainRewrite: 'localhost',
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        },
    }
})