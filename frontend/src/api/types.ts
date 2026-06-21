export type TransactionType = 'INCOME' | 'EXPENSE';

export type GoalPriority = 'LOW' | 'MEDIUM' | 'HIGH';
export type GoalStatus = 'ACTIVE' | 'COMPLETED' | 'PAUSED' | 'CANCELLED';

export interface UserProfile {
  id: string;
  keycloakUserId: string;
  displayName: string;
  currency: string;
  createdAt: string;
  updatedAt: string;
}

export interface Transaction {
  id: string;
  type: TransactionType;
  amount: number;
  category: string;
  description: string | null;
  transactionDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRequest {
  type: TransactionType;
  amount: number;
  category: string;
  description?: string;
  transactionDate: string;
}

export interface MonthlyBudgetSummary {
  userId: string;
  year: number;
  month: number;
  incomeTotal: number;
  expenseTotal: number;
  balance: number;
  netSavings?: number;
  savingsRate?: number;
  currentBalance?: number;
  categoryBreakdown?: Array<{
    category: string;
    amount: number;
  }>;
}

export interface Goal {
  id: string;
  title: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string | null;
  priority: GoalPriority;
  category: string;
  status: GoalStatus;
  progressPercent: number;
  createdAt: string;
  updatedAt: string;
}

export interface GoalRequest {
  title: string;
  targetAmount: number;
  currentAmount: number;
  targetDate?: string;
  priority: GoalPriority;
  category: string;
  status: GoalStatus;
}

export interface GamificationProfile {
  userId: string;
  xp: number;
  level: number;
  currentStreak: number;
  totalTransactions: number;
}

export interface Badge {
  badgeCode: string;
  earnedAt: string;
}

export interface CoachingMessage {
  code: string;
  message: string;
  disclaimer: string;
}

export interface YearlyBudgetSummary {
  userId: string;
  year: number;
  incomeTotal: number;
  expenseTotal: number;
  netSavings: number;
  savingsRate?: number;
  currentBalance?: number;
  months: MonthlyBudgetSummary[];
}
