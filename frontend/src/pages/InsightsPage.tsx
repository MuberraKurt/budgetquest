import { useEffect, useState } from 'react';
import { getCoachingInsight } from '../api/budgetquest';
import type { CoachingMessage } from '../api/types';

export function InsightsPage() {
  const [insight, setInsight] = useState<CoachingMessage | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadInsight() {
      setError(null);

      try {
        setInsight(await getCoachingInsight());
      } catch {
        setError('Could not load insight.');
      } finally {
        setLoading(false);
      }
    }

    loadInsight();
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
          <p className="eyebrow">Coach</p>
          <h1>Insights</h1>
          <p className="subtitle">Educational coaching based on your dashboard context.</p>
        </div>
      </section>

      {error ? <div className="alert">{error}</div> : null}

      <section className="panel insight-panel">
        <div className="panel-header">
          <h2>{insight?.code ?? 'GENERAL_TIP'}</h2>
          <span>Mock AI</span>
        </div>

        <p>{insight?.message ?? 'No insight available yet.'}</p>
        <small>{insight?.disclaimer}</small>
      </section>
    </div>
  );
}
