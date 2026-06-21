import { useCallback, useEffect, useState } from 'react';
import {
  createTransaction,
  getBadges,
  getCoachingInsight,
  getGamificationProfile,
  getGoals,
  getMonthlyBudget,
  getProfile,
  getTransactions,
} from '../api/budgetquest';
import type {
  Badge,
  CoachingMessage,
  GamificationProfile,
  Goal,
  MonthlyBudgetSummary,
  Transaction,
  UserProfile,
} from '../api/types';
import { StatCard } from '../components/StatCard';
import { TransactionForm } from '../components/TransactionForm';
import { TransactionList } from '../components/TransactionList';

const now = new Date();

function formatMoney(value: number, currency: string) {
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency,
    maximumFractionDigits: 2,
  }).format(value);
}

export function DashboardPage() {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [budget, setBudget] = useState<MonthlyBudgetSummary | null>(null);
  const [gamification, setGamification] = useState<GamificationProfile | null>(null);
  const [insight, setInsight] = useState<CoachingMessage | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [goals, setGoals] = useState<Goal[]>([]);
  const [badges, setBadges] = useState<Badge[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadDashboard = useCallback(async () => {
    setError(null);
    const year = now.getFullYear();
    const month = now.getMonth() + 1;

    try {
      const [
        profileData,
        budgetData,
        gamificationData,
        insightData,
        transactionData,
        goalData,
        badgeData,
      ] =
        await Promise.all([
          getProfile(),
          getMonthlyBudget(year, month),
          getGamificationProfile(),
          getCoachingInsight(),
          getTransactions(year, month),
          getGoals(),
          getBadges(),
        ]);

      setProfile(profileData);
      setBudget(budgetData);
      setGamification(gamificationData);
      setInsight(insightData);
      setTransactions(transactionData);
      setGoals(goalData);
      setBadges(badgeData);
    } catch {
      setError('Could not load dashboard data. Make sure backend services are running.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  async function handleCreateTransaction(
    payload: Parameters<typeof createTransaction>[0],
  ) {
    await createTransaction(payload);
    await loadDashboard();
  }

  if (loading) {
    return (
      <div className="loading-screen inline">
        <div className="spinner" />
        <p>Loading dashboard...</p>
      </div>
    );
  }

  const currency = profile?.currency ?? 'USD';

  return (
    <div className="page-stack">
      <section className="page-hero">
        <div>
          <p className="eyebrow">Welcome back</p>
          <h1>{profile?.displayName ?? 'BudgetQuest user'}</h1>
          <p className="subtitle">Here is your financial snapshot for this month.</p>
        </div>
        <button type="button" className="ghost-button" onClick={loadDashboard}>
          Refresh
        </button>
      </section>

      {error ? <div className="alert">{error}</div> : null}

      <section className="stat-grid">
        <StatCard
          label="Income"
          value={formatMoney(budget?.incomeTotal ?? 0, currency)}
          tone="positive"
        />
        <StatCard
          label="Expenses"
          value={formatMoney(budget?.expenseTotal ?? 0, currency)}
          tone="negative"
        />
        <StatCard
          label="Balance"
          value={formatMoney(budget?.balance ?? 0, currency)}
          tone={(budget?.balance ?? 0) >= 0 ? 'accent' : 'negative'}
        />
        <StatCard
          label="Level"
          value={`${gamification?.level ?? 1}`}
          hint={`${gamification?.xp ?? 0} XP · ${gamification?.currentStreak ?? 0} day streak`}
          tone="accent"
        />
      </section>

      <section className="content-grid">
        <article className="panel insight-panel">
          <div className="panel-header">
            <h2>Coach insight</h2>
            <span>{insight?.code ?? 'GENERAL_TIP'}</span>
          </div>
          <p>{insight?.message ?? 'Log a transaction to unlock personalized tips.'}</p>
          <small>{insight?.disclaimer}</small>
        </article>

        <TransactionForm currency={currency} onSubmit={handleCreateTransaction} />
      </section>

      <section className="content-grid">
        <article className="panel">
          <div className="panel-header">
            <h2>Active goals</h2>
            <span>{goals.filter((goal) => goal.status === 'ACTIVE').length} active</span>
          </div>
          {goals.length === 0 ? (
            <p className="empty-state">No goals yet. Create one from the Goals page.</p>
          ) : (
            <div className="goal-list">
              {goals.slice(0, 3).map((goal) => (
                <article key={goal.id} className="goal-card">
                  <div>
                    <strong>{goal.title}</strong>
                    <span>
                      {goal.category} · {goal.status}
                    </span>
                  </div>
                  <div className="goal-progress">
                    <div className="progress-bar">
                      <div style={{ width: `${Math.min(goal.progressPercent, 100)}%` }} />
                    </div>
                    <span>{goal.progressPercent}%</span>
                  </div>
                </article>
              ))}
            </div>
          )}
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Badges</h2>
            <span>{badges.length} earned</span>
          </div>
          {badges.length === 0 ? (
            <p className="empty-state">No badges yet. Add transactions to earn your first one.</p>
          ) : (
            <div className="goal-list">
              {badges.slice(0, 3).map((badge) => (
                <article key={`${badge.badgeCode}-${badge.earnedAt}`} className="goal-card">
                  <div>
                    <strong>{badge.badgeCode.replaceAll('_', ' ')}</strong>
                    <span>Earned {new Date(badge.earnedAt).toLocaleDateString()}</span>
                  </div>
                </article>
              ))}
            </div>
          )}
        </article>
      </section>

      <section className="panel">
        <div className="panel-header">
          <h2>Recent transactions</h2>
          <span>{transactions.length} this month</span>
        </div>
        <TransactionList transactions={transactions.slice(0, 5)} currency={currency} />
      </section>
    </div>
  );
}
