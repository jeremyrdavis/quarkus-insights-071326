import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // When running Vite directly (not through Quinoa), proxy API calls to Quarkus
    proxy: {
      '/cfp': 'http://localhost:8080',
      '/presenters': 'http://localhost:8080',
      '/session-proposals': 'http://localhost:8080',
    },
  },
})
