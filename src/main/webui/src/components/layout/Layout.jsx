import { Outlet } from 'react-router-dom'
import NavBar from './NavBar.jsx'
import Footer from './Footer.jsx'

export default function Layout() {
  return (
    <div className="min-h-screen flex flex-col bg-ink text-white">
      <NavBar />
      <main className="flex-1 container-1180 py-12">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
