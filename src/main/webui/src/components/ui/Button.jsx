const variants = {
  primary:   'bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500',
  secondary: 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50 focus:ring-indigo-500',
  danger:    'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
}

export default function Button({ variant = 'primary', children, className = '', ...props }) {
  return (
    <button
      className={`inline-flex items-center px-4 py-2 rounded-md text-sm font-medium
        focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50
        disabled:cursor-not-allowed transition-colors ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}
