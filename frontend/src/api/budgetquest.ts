import { apiClient } from './client';
import type {
  CoachingMessage,
  GamificationProfile,
  Goal,
  GoalRequest,
  MonthlyBudgetSummary,
  Transaction,
  TransactionRequest,
  UserProfile,
  Badge,
  YearlyBudgetSummary
} from './types';

export async function getProfile(): Promise<UserProfile> {
  const { data } = await apiClient.get<UserProfile>('/api/users/me');
  return data;
}

export async function updateProfile(payload: {
  displayName: string;
  currency: string;
}): Promise<UserProfile> {
  const { data } = await apiClient.put<UserProfile>('/api/users/me', payload);
  return data;
}

export async function getMonthlyBudget(year: number, month: number): Promise<MonthlyBudgetSummary> {
  const { data } = await apiClient.get<MonthlyBudgetSummary>('/api/budgets/monthly', {
    params: { year, month },
  });
  return data;
}

export async function getTransactions(year: number, month: number): Promise<Transaction[]> {
  const { data } = await apiClient.get<Transaction[]>('/api/transactions', {
    params: { year, month },
  });
  return data;
}

export async function createTransaction(payload: TransactionRequest): Promise<Transaction> {
  const { data } = await apiClient.post<Transaction>('/api/transactions', payload);
  return data;
}

export async function updateTransaction(
  id: string,
  payload: TransactionRequest,
): Promise<Transaction> {
  const { data } = await apiClient.put<Transaction>(`/api/transactions/${id}`, payload);
  return data;
}

export async function deleteTransaction(id: string): Promise<void> {
  await apiClient.delete(`/api/transactions/${id}`);
}

export async function getGoals(): Promise<Goal[]> {
  const { data } = await apiClient.get<Goal[]>('/api/goals');
  return data;
}

export async function createGoal(payload: GoalRequest): Promise<Goal> {
  const { data } = await apiClient.post<Goal>('/api/goals', payload);
  return data;
}

export async function updateGoal(id: string, payload: GoalRequest): Promise<Goal> {
  const { data } = await apiClient.put<Goal>(`/api/goals/${id}`, payload);
  return data;
}

export async function deleteGoal(id: string): Promise<void> {
  await apiClient.delete(`/api/goals/${id}`);
}

export async function getGamificationProfile(): Promise<GamificationProfile> {
  const { data } = await apiClient.get<GamificationProfile>('/api/gamification/me');
  return data;
}

export async function getBadges(): Promise<Badge[]> {
  const { data } = await apiClient.get<Badge[]>('/api/gamification/me/badges');
  return data;
}

export async function getCoachingInsight(): Promise<CoachingMessage> {
  const { data } = await apiClient.get<CoachingMessage>('/api/insights/coaching');
  return data;
}

export async function getYearlyBudget(year: number): Promise<YearlyBudgetSummary> {
  const { data } = await apiClient.get<YearlyBudgetSummary>('/api/budgets/yearly', {
    params: { year },
  });
  return data;
}
