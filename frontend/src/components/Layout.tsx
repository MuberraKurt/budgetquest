import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const navItems = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/transactions', label: 'Transactions' },
  { to: '/goals', label: 'Goals' },
  { to: '/settings', label: 'Settings' },
  { to: '/yearly-budget', label: 'Yearly Budget' },
  { to: '/monthly-budget', label: 'Monthly Budget' },
  { to: '/achievements', label: 'Achievements' },
  { to: '/insights', label: 'Insights' },
];

export function Layout() {
  const { username, logout } = useAuth();

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark">BQ</span>
          <div>
            <strong>BudgetQuest</strong>
            <span>Personal finance, gamified</span>
          </div>
        </div>
        <div className="topbar-actions">
          <span className="user-chip">{username ?? 'User'}</span>
          <button type="button" className="ghost-button" onClick={logout}>
            Log out
          </button>
        </div>
      </header>

      <div className="app-body">
        <nav className="sidebar">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
