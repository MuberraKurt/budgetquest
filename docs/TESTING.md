# BudgetQuest Testing Guide

This guide explains how to check the project after changes. The goal is to
catch regressions before they become confusing runtime bugs.

## Frontend Build Check

```bash
cd frontend
npm run build
```

This confirms TypeScript compiles and the React app can be bundled.

## Backend Service Tests

Run tests inside each backend service:

```bash
cd backend/discovery-server
./mvnw test
```

```bash
cd backend/config-server
./mvnw test
```

```bash
cd backend/api-gateway
./mvnw test
```

```bash
cd backend/user-service
./mvnw test
```

```bash
cd backend/transaction-service
./mvnw test
```

```bash
cd backend/budget-service
./mvnw test
```

```bash
cd backend/goal-service
./mvnw test
```

```bash
cd backend/gamification-service
./mvnw test
```

```bash
cd backend/insight-service
./mvnw test
```

## Most Important Regression Checks

After changing transaction code, always verify:

- Creating an expense updates dashboard expenses.
- Deleting an expense decreases dashboard expenses.
- Updating an expense amount changes dashboard totals.
- Changing transaction type from expense to income updates totals correctly.
- Budget monthly/yearly pages still match dashboard totals.

## Event-Driven Checks

Transaction events should affect:

- Budget Service: totals and balance
- Gamification Service: XP, levels, badges, streaks

If the Transactions page changes but Dashboard does not, suspect:

1. RabbitMQ is not running.
2. Budget Service is not running.
3. Transaction Service did not publish an event.
4. Budget Service event logic has a bug.

## Contract Checks

The riskiest changes are shared contracts:

- Event DTOs
- API response DTOs
- Gateway routes
- Auth configuration
- Frontend API types

When one of these changes, check both sides:

- The service that produces the data
- The service or frontend code that consumes the data

## When To Run All Tests

Run all tests before:

- Submitting the project
- Showing the project to someone
- Adding Docker, Jenkins, or Kubernetes later
- Changing shared DTOs or event payloads

## Learning Note

Tests are not only for proving that code works. They also document what the
system promises to do. In BudgetQuest, the most important promise is that a
transaction change eventually updates the dashboard and gamification state.
