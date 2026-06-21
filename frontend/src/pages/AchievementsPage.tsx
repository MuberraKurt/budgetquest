import { useEffect, useState } from 'react';
import { getBadges, getGamificationProfile } from '../api/budgetquest';
import type { Badge, GamificationProfile } from '../api/types';
import { StatCard } from '../components/StatCard';

function formatBadgeName(code: string) {
  return code
    .toLowerCase()
    .split('_')
    .map((part) => part[0].toUpperCase() + part.slice(1))
    .join(' ');
}

export function AchievementsPage() {
  const [profile, setProfile] = useState<GamificationProfile | null>(null);
  const [badges, setBadges] = useState<Badge[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadAchievements() {
      setError(null);

      try {
        const [profileData, badgeData] = await Promise.all([
          getGamificationProfile(),
          getBadges(),
        ]);

        setProfile(profileData);
        setBadges(badgeData);
      } catch {
        setError('Could not load achievements.');
      } finally {
        setLoading(false);
      }
    }

    loadAchievements();
  }, []);

  if (loading) {
    return (
      <div className="loading-screen inline">
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div className="page-stack">
      <section className="page-hero">
        <div>
          <p className="eyebrow">Progress</p>
          <h1>Achievements</h1>
          <p className="subtitle">Track your XP, level, streak, and earned badges.</p>
        </div>
      </section>

      {error ? <div className="alert">{error}</div> : null}

      <section className="stat-grid">
        <StatCard label="Level" value={`${profile?.level ?? 1}`} tone="accent" />
        <StatCard label="XP" value={`${profile?.xp ?? 0}`} tone="positive" />
        <StatCard
          label="Streak"
          value={`${profile?.currentStreak ?? 0} days`}
          tone="accent"
        />
        <StatCard
          label="Transactions"
          value={`${profile?.totalTransactions ?? 0}`}
          tone="default"
        />
      </section>

      <section className="panel">
        <div className="panel-header">
          <h2>Badges</h2>
          <span>{badges.length} earned</span>
        </div>

        {badges.length === 0 ? (
          <p className="empty-state">No badges yet. Add transactions to earn your first one.</p>
        ) : (
          <div className="goal-list">
            {badges.map((badge) => (
              <article key={`${badge.badgeCode}-${badge.earnedAt}`} className="goal-card">
                <div>
                  <strong>{formatBadgeName(badge.badgeCode)}</strong>
                  <span>Earned {new Date(badge.earnedAt).toLocaleDateString()}</span>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
