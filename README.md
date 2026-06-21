# BudgetQuest

BudgetQuest is a microservice-based personal finance and gamification app.
It lets a user track income and expenses, review monthly and yearly budget
summaries, manage financial goals, earn achievements, and read mock
educational coaching messages.

The project is built as a learning-focused Spring Boot microservices system
with a React + Vite frontend.

## What Is Completed

- Discovery Server with Eureka service registry
- Config Server for centralized service configuration
- API Gateway as the frontend entry point to backend APIs
- User Service for profile and preferences
- Transaction Service for income and expense CRUD
- Budget Service for monthly and yearly summaries
- Goal Service for financial goals
- Gamification Service for XP, levels, badges, and streaks
- Insight Service for mock educational coaching
- React + Vite frontend for dashboard, transactions, budgets, goals,
  achievements, insights, and settings

## Services

| Service | Responsibility |
| --- | --- |
| `discovery-server` | Eureka service registry |
| `config-server` | Centralized configuration |
| `api-gateway` | Routes frontend API calls to backend services |
| `user-service` | User profile and preferences |
| `transaction-service` | Income and expense CRUD |
| `budget-service` | Monthly and yearly budget summaries |
| `goal-service` | Financial goals and completion status |
| `gamification-service` | XP, levels, badges, and streaks |
| `insight-service` | Mock educational coaching messages |
| `frontend` | React + Vite user interface |

## Main Ports

| App | Port |
| --- | --- |
| Discovery Server | `8761` |
| Config Server | `8762` |
| API Gateway | `8763` |
| Frontend | `5173` |

Other backend service ports are configured by each service and the config
server.

## Run Order

Start services in this order:

1. `discovery-server`
2. `config-server`
3. `api-gateway`
4. `user-service`
5. `transaction-service`
6. `budget-service`
7. `goal-service`
8. `gamification-service`
9. `insight-service`
10. `frontend`

## Backend Command

Run this inside each backend service directory:

```bash
./mvnw spring-boot:run
```

Example:

```bash
cd backend/discovery-server
./mvnw spring-boot:run
```

## Frontend Command

```bash
cd frontend
npm run dev
```

## Verification Flow

Use this flow to prove the core app works:

1. Open the frontend.
2. Log in.
3. Create an expense transaction.
4. Confirm the transaction appears on the Transactions page.
5. Confirm Dashboard expenses increase.
6. Confirm Dashboard balance decreases.
7. Confirm monthly budget totals update.
8. Delete the transaction.
9. Confirm the transaction disappears.
10. Confirm Dashboard expenses decrease.
11. Confirm Dashboard balance increases back.
12. Check goals, achievements, and insights pages.

## Learning Note

The central architecture idea is separation of ownership:

- Transaction Service owns transaction records.
- Budget Service owns summary totals.
- Gamification Service owns XP, badges, levels, and streaks.
- Insight Service owns coaching messages.

Transaction Service publishes events after changes. Budget Service and
Gamification Service react to those events so their own data stays in sync.
