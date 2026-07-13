const variants = {
  primary:   'btn-primary',
  secondary: 'btn-ghost',
  danger:    'btn-danger',
}

export default function Button({ variant = 'primary', children, className = '', ...props }) {
  return (
    <button className={`${variants[variant] ?? variants.primary} ${className}`} {...props}>
      {children}
    </button>
  )
}
