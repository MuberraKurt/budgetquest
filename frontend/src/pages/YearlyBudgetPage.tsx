import { useEffect, useState } from 'react';
import { getProfile, getYearlyBudget } from '../api/budgetquest';
import type { UserProfile, YearlyBudgetSummary } from '../api/types';
import { StatCard } from '../components/StatCard';

const year = new Date().getFullYear();

function formatMoney(value: number, currency: string) {
    return new Intl.NumberFormat(undefined, {
        style: 'currency',
        currency,
        maximumFractionDigits: 2,
    }).format(value);
}

export function YearlyBudgetPage() {
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [summary, setSummary] = useState<YearlyBudgetSummary | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadYearlyBudget() {
            setError(null);

            try {
                const [profileData, yearlyData] = await Promise.all([
                    getProfile(),
                    getYearlyBudget(year),
                ]);

                setProfile(profileData);
                setSummary(yearlyData);
            } catch {
                setError('Could not load yearly budget.');
            } finally {
                setLoading(false);
            }
        }

        loadYearlyBudget();
    }, []);

    if (loading) {
        return (
            <div className="loading-screen inline">
                <div className="spinner" />
            </div>
        );
    }

    const currency = profile?.currency ?? 'USD';

    return (
        <div className="page-stack">
            <section className="page-hero">
                <div>
                    <p className="eyebrow">Annual view</p>
                    <h1>Yearly Budget</h1>
                    <p className="subtitle">Review your income, expenses, and savings across the year.</p>
                </div>
            </section>

            {error ? <div className="alert">{error}</div> : null}

            <section className="stat-grid">
                <StatCard
                    label="Income"
                    value={formatMoney(summary?.incomeTotal ?? 0, currency)}
                    tone="positive"
                />
                <StatCard
                    label="Expenses"
                    value={formatMoney(summary?.expenseTotal ?? 0, currency)}
                    tone="negative"
                />
                <StatCard
                    label="Net savings"
                    value={formatMoney(summary?.netSavings ?? 0, currency)}
                    tone={(summary?.netSavings ?? 0) >= 0 ? 'accent' : 'negative'}
                />
            </section>

            <section className="panel">
                <div className="panel-header">
                    <h2>Monthly breakdown</h2>
                    <span>{summary?.year ?? year}</span>
                </div>

                {summary?.months.length === 0 ? (
                    <p className="empty-state">No monthly summaries yet.</p>
                ) : (
                    <div className="transaction-list">
                        {summary?.months.map((month) => (
                            <article key={month.month} className="transaction-item">
                                <div>
                                    <strong>Month {month.month}</strong>
                                    <span>Balance {formatMoney(month.balance, currency)}</span>
                                </div>
                                <div className="transaction-meta">
                  <span className="amount income">
                    +{formatMoney(month.incomeTotal, currency)}
                  </span>
                                    <span className="amount expense">
                    -{formatMoney(month.expenseTotal, currency)}
                  </span>
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}