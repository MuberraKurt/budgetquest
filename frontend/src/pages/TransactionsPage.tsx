import { useCallback, useEffect, useState } from 'react';
import {
  createTransaction,
  deleteTransaction,
  getProfile,
  getTransactions,
  updateTransaction,
} from '../api/budgetquest';
import type { Transaction, TransactionRequest } from '../api/types';
import { TransactionForm } from '../components/TransactionForm';
import { TransactionList } from '../components/TransactionList';

const now = new Date();

export function TransactionsPage() {
  const [currency, setCurrency] = useState('USD');
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadTransactions = useCallback(async () => {
    setError(null);
    const year = now.getFullYear();
    const month = now.getMonth() + 1;

    try {
      const [profile, transactionData] = await Promise.all([
        getProfile(),
        getTransactions(year, month),
      ]);
      setCurrency(profile.currency);
      setTransactions(transactionData);
    } catch {
      setError('Could not load transactions.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTransactions();
  }, [loadTransactions]);

  async function handleCreate(payload: TransactionRequest) {
    if (editingTransaction) {
      await updateTransaction(editingTransaction.id, payload);
      setEditingTransaction(null);
    } else {
      await createTransaction(payload);
    }

    await loadTransactions();
  }

  async function handleDelete(id: string) {
    await deleteTransaction(id);
    if (editingTransaction?.id === id) {
      setEditingTransaction(null);
    }
    await loadTransactions();
  }

  const editingValue: TransactionRequest | undefined = editingTransaction
    ? {
        type: editingTransaction.type,
        amount: editingTransaction.amount,
        category: editingTransaction.category,
        description: editingTransaction.description ?? undefined,
        transactionDate: editingTransaction.transactionDate,
      }
    : undefined;

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
          <p className="eyebrow">Money log</p>
          <h1>Transactions</h1>
          <p className="subtitle">Create, review, and remove entries for the current month.</p>
        </div>
      </section>

      {error ? <div className="alert">{error}</div> : null}

      <section className="content-grid single-column">
        <TransactionForm
          currency={currency}
          onSubmit={handleCreate}
          initialValue={editingValue}
          title={editingTransaction ? 'Edit transaction' : 'Add transaction'}
          submitLabel={editingTransaction ? 'Update transaction' : 'Save transaction'}
          onCancel={editingTransaction ? () => setEditingTransaction(null) : undefined}
        />
        <article className="panel">
          <div className="panel-header">
            <h2>All transactions</h2>
            <span>{transactions.length} total</span>
          </div>
          <TransactionList
            transactions={transactions}
            currency={currency}
            onEdit={setEditingTransaction}
            onDelete={handleDelete}
          />
        </article>
      </section>
    </div>
  );
}
