import { useCallback, useEffect, useState, type FormEvent } from 'react';
import { createGoal, deleteGoal, getGoals, updateGoal } from '../api/budgetquest';
import type { Goal, GoalPriority, GoalRequest } from '../api/types';

export function GoalsPage() {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [editingGoalId, setEditingGoalId] = useState<string | null>(null);
  const [title, setTitle] = useState('');
  const [targetAmount, setTargetAmount] = useState('');
  const [currentAmount, setCurrentAmount] = useState('0');
  const [category, setCategory] = useState('Savings');
  const [priority, setPriority] = useState<GoalPriority>('MEDIUM');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadGoals = useCallback(async () => {
    setError(null);
    try {
      setGoals(await getGoals());
    } catch {
      setError('Could not load goals.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadGoals();
  }, [loadGoals]);

  function resetForm() {
    setEditingGoalId(null);
    setTitle('');
    setTargetAmount('');
    setCurrentAmount('0');
    setCategory('Savings');
    setPriority('MEDIUM');
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    const payload: GoalRequest = {
      title,
      targetAmount: Number(targetAmount),
      currentAmount: Number(currentAmount),
      priority,
      category,
      status: 'ACTIVE',
    };

    try {
      if (editingGoalId) {
        await updateGoal(editingGoalId, payload);
      } else {
        await createGoal(payload);
      }

      resetForm();
      await loadGoals();
    } catch {
      setError(editingGoalId ? 'Could not update goal.' : 'Could not create goal.');
    } finally {
      setSubmitting(false);
    }
  }

  function handleEditGoal(goal: Goal) {
    setEditingGoalId(goal.id);
    setTitle(goal.title);
    setTargetAmount(String(goal.targetAmount));
    setCurrentAmount(String(goal.currentAmount));
    setCategory(goal.category);
    setPriority(goal.priority);
    setError(null);
  }

  async function handleDeleteGoal(id: string) {
    setError(null);

    try {
      await deleteGoal(id);
      if (editingGoalId === id) {
        resetForm();
      }
      await loadGoals();
    } catch {
      setError('Could not delete goal.');
    }
  }

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
          <p className="eyebrow">Targets</p>
          <h1>Goals</h1>
          <p className="subtitle">Track progress toward the milestones that matter to you.</p>
        </div>
      </section>

      {error ? <div className="alert">{error}</div> : null}

      <section className="content-grid single-column">
        <form className="panel form-panel" onSubmit={handleSubmit}>
          <div className="panel-header">
            <h2>{editingGoalId ? 'Edit goal' : 'Create goal'}</h2>
          </div>
          <div className="form-grid">
            <label>
              Title
              <input required value={title} onChange={(e) => setTitle(e.target.value)} />
            </label>
            <label>
              Target amount
              <input
                type="number"
                min="0.01"
                step="0.01"
                required
                value={targetAmount}
                onChange={(e) => setTargetAmount(e.target.value)}
              />
            </label>
            <label>
              Current amount
              <input
                type="number"
                min="0"
                step="0.01"
                required
                value={currentAmount}
                onChange={(e) => setCurrentAmount(e.target.value)}
              />
            </label>
            <label>
              Category
              <input required value={category} onChange={(e) => setCategory(e.target.value)} />
            </label>
            <label>
              Priority
              <select value={priority} onChange={(e) => setPriority(e.target.value as GoalPriority)}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </label>
          </div>
          <div className="topbar-actions">
            <button type="submit" className="primary-button" disabled={submitting}>
              {submitting ? 'Saving...' : editingGoalId ? 'Update goal' : 'Create goal'}
            </button>
            {editingGoalId ? (
              <button type="button" className="ghost-button" onClick={resetForm}>
                Cancel edit
              </button>
            ) : null}
          </div>
        </form>

        <article className="panel">
          <div className="panel-header">
            <h2>Your goals</h2>
            <span>{goals.length} total</span>
          </div>
          {goals.length === 0 ? (
            <p className="empty-state">No goals yet. Create your first one above.</p>
          ) : (
            <div className="goal-list">
              {goals.map((goal) => (
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
                    <button
                      type="button"
                      className="ghost-button"
                      onClick={() => handleEditGoal(goal)}
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      className="ghost-button danger"
                      onClick={() => handleDeleteGoal(goal.id)}
                    >
                      Delete
                    </button>
                  </div>
                </article>
              ))}
            </div>
          )}
        </article>
      </section>
    </div>
  );
}
