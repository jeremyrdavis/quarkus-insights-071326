/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        // Backgrounds / surfaces
        ink: '#0c1826',
        'ink-2': '#0a1420',
        surface: '#122438',
        'surface-2': '#0d1a29',
        'surface-closed': '#0f1d2d',
        // Brand
        brand: '#4695eb',
        'brand-light': '#7cb3f4',
        'brand-tag': '#9cc4f5',
        // Status
        success: '#3fe08f',
        danger: '#ff004a',
        'danger-light': '#ff5b83',
        warning: '#ffc23d',
        // Muted text scale
        'muted-100': '#cdd8e6',
        'muted-200': '#a6bad0',
        'muted-300': '#8ba1ba',
        'muted-400': '#7d92aa',
        'muted-500': '#6f849c',
      },
      fontFamily: {
        display: ["'Red Hat Display'", 'system-ui', 'sans-serif'],
        sans: ["'Red Hat Text'", 'system-ui', 'sans-serif'],
        mono: ["'Red Hat Mono'", 'ui-monospace', 'monospace'],
      },
      boxShadow: {
        btn: '0 10px 26px rgba(70,149,235,.38)',
        'btn-lg': '0 14px 40px rgba(70,149,235,.42)',
        card: '0 24px 60px rgba(10,40,90,.35)',
      },
      maxWidth: {
        container: '1180px',
      },
    },
  },
  plugins: [],
}
