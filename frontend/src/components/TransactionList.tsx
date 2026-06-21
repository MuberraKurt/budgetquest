import type { Transaction } from '../api/types';

interface TransactionListProps {
  transactions: Transaction[];
  currency: string;
  onEdit?: (transaction: Transaction) => void;
  onDelete?: (id: string) => Promise<void>;
}

function formatMoney(value: number, currency: string) {
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency,
    maximumFractionDigits: 2,
  }).format(value);
}

export function TransactionList({
  transactions,
  currency,
  onEdit,
  onDelete,
}: TransactionListProps) {
  if (transactions.length === 0) {
    return <p className="empty-state">No transactions yet for this month.</p>;
  }

  return (
    <div className="transaction-list">
      {transactions.map((transaction) => (
        <article key={transaction.id} className="transaction-item">
          <div>
            <strong>{transaction.category}</strong>
            <span>{transaction.description || transaction.transactionDate}</span>
          </div>
          <div className="transaction-meta">
            <span className={transaction.type === 'INCOME' ? 'amount income' : 'amount expense'}>
              {transaction.type === 'INCOME' ? '+' : '-'}
              {formatMoney(transaction.amount, currency)}
            </span>
            {onEdit ? (
              <button
                type="button"
                className="ghost-button"
                onClick={() => onEdit(transaction)}
              >
                Edit
              </button>
            ) : null}
            {onDelete ? (
              <button
                type="button"
                className="ghost-button danger"
                onClick={() => onDelete(transaction.id)}
              >
                Delete
              </button>
            ) : null}
          </div>
        </article>
      ))}
    </div>
  );
}
