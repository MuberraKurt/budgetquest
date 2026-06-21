interface StatCardProps {
  label: string;
  value: string;
  tone?: 'default' | 'positive' | 'negative' | 'accent';
  hint?: string;
}

export function StatCard({ label, value, tone = 'default', hint }: StatCardProps) {
  return (
    <article className={`stat-card tone-${tone}`}>
      <span className="stat-label">{label}</span>
      <strong className="stat-value">{value}</strong>
      {hint ? <span className="stat-hint">{hint}</span> : null}
    </article>
  );
}
