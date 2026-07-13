import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/layout/Layout.jsx'
import CfpListPage from './pages/cfp/CfpListPage.jsx'
import CfpCreatePage from './pages/cfp/CfpCreatePage.jsx'
import CfpDetailPage from './pages/cfp/CfpDetailPage.jsx'
import CfpEditPage from './pages/cfp/CfpEditPage.jsx'
import WizardPage from './pages/wizard/WizardPage.jsx'
import ReviewsPage from './pages/reviews/ReviewsPage.jsx'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Navigate to="/cfp" replace />} />
        <Route path="/cfp" element={<CfpListPage />} />
        <Route path="/cfp/new" element={<CfpCreatePage />} />
        <Route path="/cfp/:id" element={<CfpDetailPage />} />
        <Route path="/cfp/:id/edit" element={<CfpEditPage />} />
        <Route path="/submit" element={<WizardPage />} />
        <Route path="/reviews" element={<ReviewsPage />} />
        <Route path="*" element={<Navigate to="/cfp" replace />} />
      </Route>
    </Routes>
  )
}
