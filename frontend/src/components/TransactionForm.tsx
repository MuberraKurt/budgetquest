import { useEffect, useState, type FormEvent } from 'react';
import type { TransactionRequest, TransactionType } from '../api/types';

interface TransactionFormProps {
  currency: string;
  onSubmit: (payload: TransactionRequest) => Promise<void>;
  initialValue?: TransactionRequest;
  title?: string;
  submitLabel?: string;
  onCancel?: () => void;
}

const today = new Date().toISOString().slice(0, 10);

export function TransactionForm({
  currency,
  onSubmit,
  initialValue,
  title = 'Add transaction',
  submitLabel = 'Save transaction',
  onCancel,
}: TransactionFormProps) {
  const [type, setType] = useState<TransactionType>(initialValue?.type ?? 'EXPENSE');
  const [amount, setAmount] = useState(initialValue ? String(initialValue.amount) : '');
  const [category, setCategory] = useState(initialValue?.category ?? '');
  const [description, setDescription] = useState(initialValue?.description ?? '');
  const [transactionDate, setTransactionDate] = useState(initialValue?.transactionDate ?? today);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setType(initialValue?.type ?? 'EXPENSE');
    setAmount(initialValue ? String(initialValue.amount) : '');
    setCategory(initialValue?.category ?? '');
    setDescription(initialValue?.description ?? '');
    setTransactionDate(initialValue?.transactionDate ?? today);
    setError(null);
  }, [initialValue]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      await onSubmit({
        type,
        amount: Number(amount),
        category,
        description: description || undefined,
        transactionDate,
      });
      setAmount('');
      setCategory('');
      setDescription('');
      setTransactionDate(today);
    } catch {
      setError('Could not save transaction. Check the form and try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form className="panel form-panel" onSubmit={handleSubmit}>
      <div className="panel-header">
        <h2>{title}</h2>
        <span>Amounts in {currency}</span>
      </div>

      <div className="form-grid">
        <label>
          Type
          <select value={type} onChange={(e) => setType(e.target.value as TransactionType)}>
            <option value="EXPENSE">Expense</option>
            <option value="INCOME">Income</option>
          </select>
        </label>

        <label>
          Amount
          <input
            type="number"
            min="0.01"
            step="0.01"
            required
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
        </label>

        <label>
          Category
          <input
            required
            maxLength={80}
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            placeholder="Food, Salary, Transport"
          />
        </label>

        <label>
          Date
          <input
            type="date"
            required
            value={transactionDate}
            onChange={(e) => setTransactionDate(e.target.value)}
          />
        </label>

        <label className="full-width">
          Description
          <input
            maxLength={255}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Optional note"
          />
        </label>
      </div>

      {error ? <p className="form-error">{error}</p> : null}

      <div className="topbar-actions">
        <button type="submit" className="primary-button" disabled={submitting}>
          {submitting ? 'Saving...' : submitLabel}
        </button>
        {onCancel ? (
          <button type="button" className="ghost-button" onClick={onCancel}>
            Cancel edit
          </button>
        ) : null}
      </div>
    </form>
  );
}
