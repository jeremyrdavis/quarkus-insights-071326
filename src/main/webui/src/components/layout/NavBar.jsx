import { NavLink } from 'react-router-dom'

export default function NavBar() {
  const linkClass = ({ isActive }) =>
    `px-3 py-2 rounded-md text-sm font-medium transition-colors ${
      isActive ? 'bg-indigo-700 text-white' : 'text-indigo-100 hover:bg-indigo-600'
    }`

  return (
    <nav className="bg-indigo-800 shadow">
      <div className="max-w-5xl mx-auto px-4 flex items-center gap-4 h-14">
        <span className="text-white font-bold text-lg mr-4">CFP Manager</span>
        <NavLink to="/cfp" className={linkClass}>CFPs</NavLink>
        <NavLink to="/submit" className={linkClass}>Submit Proposal</NavLink>
      </div>
    </nav>
  )
}
