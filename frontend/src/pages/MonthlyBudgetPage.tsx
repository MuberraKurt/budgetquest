import { useEffect, useState } from 'react';
import { getMonthlyBudget, getProfile } from '../api/budgetquest';
import type { MonthlyBudgetSummary, UserProfile } from '../api/types';
import { StatCard } from '../components/StatCard';

const today = new Date();
const year = today.getFullYear();
const month = today.getMonth() + 1;

function formatMoney(value: number, currency: string) {
    return new Intl.NumberFormat(undefined, {
        style: 'currency',
        currency,
        maximumFractionDigits: 2,
    }).format(value);
}

function calculateSavingsRate(summary: MonthlyBudgetSummary | null) {
    if (!summary || summary.incomeTotal === 0) {
        return 0;
    }

    return Math.round((summary.balance / summary.incomeTotal) * 100);
}

export function MonthlyBudgetPage() {
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [summary, setSummary] = useState<MonthlyBudgetSummary | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadMonthlyBudget() {
            setError(null);

            try {
                const [profileData, summaryData] = await Promise.all([
                    getProfile(),
                    getMonthlyBudget(year, month),
                ]);

                setProfile(profileData);
                setSummary(summaryData);
            } catch {
                setError('Could not load monthly budget.');
            } finally {
                setLoading(false);
            }
        }

        loadMonthlyBudget();
    }, []);

    if (loading) {
        return (
            <div className="loading-screen inline">
                <div className="spinner" />
            </div>
        );
    }

    const currency = profile?.currency ?? 'USD';
    const savingsRate = calculateSavingsRate(summary);

    return (
        <div className="page-stack">
            <section className="page-hero">
                <div>
                    <p className="eyebrow">Monthly view</p>
                    <h1>Monthly Budget</h1>
                    <p className="subtitle">See this month’s income, expenses, balance, and savings rate.</p>
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
                    label="Balance"
                    value={formatMoney(summary?.balance ?? 0, currency)}
                    tone={(summary?.balance ?? 0) >= 0 ? 'accent' : 'negative'}
                />
                <StatCard
                    label="Savings rate"
                    value={`${savingsRate}%`}
                    tone={savingsRate >= 0 ? 'accent' : 'negative'}
                />
            </section>
        </div>
    );
}