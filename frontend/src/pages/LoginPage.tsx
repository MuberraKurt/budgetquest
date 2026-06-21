import { Navigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function LoginPage() {
  const { initialized, authenticated, login } = useAuth();

  if (!initialized) {
    return (
      <div className="loading-screen">
        <div className="spinner" />
        <p>Preparing sign in...</p>
      </div>
    );
  }

  if (authenticated) {
    return <Navigate to="/" replace />;
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <span className="brand-mark large">BQ</span>
        <h1>BudgetQuest</h1>
        <p>
          Track spending, hit savings goals, earn XP, and get educational coaching — all in one
          place.
        </p>
        <button type="button" className="primary-button" onClick={login}>
          Sign in with Keycloak
        </button>
      </div>
    </div>
  );
}
