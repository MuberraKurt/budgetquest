import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { initialized, authenticated } = useAuth();

  if (!initialized) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <p>Loading BudgetQuest...</p>
      </div>
    );
  }

  if (!authenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
