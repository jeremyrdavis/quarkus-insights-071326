import { NavLink, Link } from 'react-router-dom'

export default function NavBar() {
  const linkClass = ({ isActive }) => `nav-link ${isActive ? 'nav-link-active' : ''}`

  return (
    <nav className="border-b border-white/[.08] bg-[rgba(10,20,32,.7)] backdrop-blur">
      <div className="container-1180 flex items-center justify-between h-[82px]">
        <Link to="/" className="flex items-center gap-[13px]">
          <img src="/quarkus-icon.svg" alt="Quarkus" className="w-[34px] h-[34px] block" />
          <span className="font-display font-extrabold text-[21px] tracking-[-.01em] text-white">
            Quarkus <span className="text-brand-light">CFP</span>
          </span>
        </Link>

        <div className="hidden md:flex items-center gap-9">
          <NavLink to="/" end className={linkClass}>Call for Papers</NavLink>
          <NavLink to="/submit" className={linkClass}>Submit</NavLink>
          <NavLink to="/reviews" className={linkClass}>Reviews</NavLink>
        </div>

        <Link to="/submit" className="btn-primary">Submit a talk →</Link>
      </div>
    </nav>
  )
}
