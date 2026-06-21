import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { Layout } from './components/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { DashboardPage } from './pages/DashboardPage';
import { GoalsPage } from './pages/GoalsPage';
import { LoginPage } from './pages/LoginPage';
import { ProfilePage } from './pages/ProfilePage';
import { TransactionsPage } from './pages/TransactionsPage';
import { YearlyBudgetPage } from './pages/YearlyBudgetPage';
import { MonthlyBudgetPage } from './pages/MonthlyBudgetPage';
import { AchievementsPage } from './pages/AchievementsPage';
import { InsightsPage } from './pages/InsightsPage';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route path="/" element={<DashboardPage />} />
            <Route path="/transactions" element={<TransactionsPage />} />
            <Route path="/goals" element={<GoalsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/settings" element={<ProfilePage />} />
            <Route path="/yearly-budget" element={<YearlyBudgetPage />} />
            <Route path="/monthly-budget" element={<MonthlyBudgetPage />} />
            <Route path="/achievements" element={<AchievementsPage />} />
            <Route path="/insights" element={<InsightsPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
