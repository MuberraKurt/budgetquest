# BudgetQuest Demo Script

Use this script when presenting the project. A good demo is not clicking every
page. It is showing one meaningful action and proving the system reacted
correctly.

## 1. Open The App

Open the frontend:

- `http://localhost:5173`

Say:

> BudgetQuest is a personal finance app built with Spring Boot microservices
> and a React frontend. It lets users track transactions, budgets, goals,
> achievements, and educational insights.

## 2. Show Eureka

Open:

- `http://localhost:8761`

Say:

> All backend services register with Eureka, so the system can discover
> services dynamically.

Show these services:

- `api-gateway`
- `user-service`
- `transaction-service`
- `budget-service`
- `goal-service`
- `gamification-service`
- `insight-service`
- `config-server`

## 3. Show Dashboard

Say:

> The dashboard combines data from multiple backend services: budget summaries,
> goals, achievements, and insights.

Point out:

- income
- expenses
- balance
- goals
- badges
- coaching message

## 4. Create A Transaction

Create an expense transaction.

Say:

> The transaction is saved by Transaction Service. After saving, it publishes
> an event to RabbitMQ.

Then show:

- The transaction appears on the Transactions page.
- Dashboard expense total increases.
- Dashboard balance decreases.
- Monthly budget page updates.
- Gamification XP/profile may update.

## 5. Delete The Transaction

Delete the expense.

Say:

> Delete also publishes an event. Budget Service consumes it and subtracts the
> old amount from the summary.

Then show:

- The transaction disappears.
- Dashboard expense total decreases.
- Dashboard balance increases back.

## 6. Show Goals

Create or update a goal.

Say:

> Goal Service stores financial goals. When current amount reaches target
> amount, the goal is marked completed.

## 7. Show Achievements

Say:

> Gamification Service listens to transaction events and updates XP, levels,
> badges, and streaks.

## 8. Show Insights

Say:

> Insight Service returns mock educational coaching. It does not require an
> external AI API key, so the project is safe to run locally.

## 9. Final Explanation

Say:

> The main architecture idea is separation of responsibility. Transaction
> Service owns transactions. Budget Service owns summaries. Gamification
> Service owns XP and badges. Services stay synchronized through events.

## Demo Safety Checklist

Before presenting:

- [ ] All backend services are running.
- [ ] All expected services appear in Eureka.
- [ ] Frontend loads.
- [ ] Login works.
- [ ] Creating a transaction updates the dashboard.
- [ ] Deleting the transaction restores the dashboard totals.
