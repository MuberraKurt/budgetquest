# BudgetQuest API Checklist

Use this checklist when you want to verify the backend without relying only on
the frontend. The goal is to check each service by responsibility.

## Discovery Server

- [ ] Open Eureka dashboard: `http://localhost:8761`
- [ ] Confirm all backend services are registered.
- [ ] Confirm no expected service is stuck in `DOWN` or missing.

## Config Server

- [ ] Confirm Config Server starts after Discovery Server.
- [ ] Confirm services can start using config from the config server.
- [ ] Confirm service-specific configuration is not duplicated unnecessarily in
  business service code.

## API Gateway

- [ ] Confirm gateway is running: `http://localhost:8763/actuator/health`
- [ ] Confirm frontend API calls go through the gateway.
- [ ] Confirm protected routes require authentication.

## User Service

- [ ] `GET /api/users/me`
- [ ] `PUT /api/users/me`
- [ ] Confirm the returned user belongs to the logged-in account.
- [ ] Confirm one user cannot read or update another user's profile.

## Transaction Service

- [ ] Create an income transaction.
- [ ] Create an expense transaction.
- [ ] List transactions.
- [ ] Update a transaction amount.
- [ ] Update a transaction type from expense to income.
- [ ] Delete a transaction.
- [ ] Confirm deleted transactions disappear from the frontend.
- [ ] Confirm create, update, and delete publish events for downstream services.

## Budget Service

- [ ] Open the monthly budget page.
- [ ] Confirm income total.
- [ ] Confirm expense total.
- [ ] Confirm balance.
- [ ] Open the yearly budget page.
- [ ] Confirm yearly totals.
- [ ] Confirm deleting an expense reduces expense total.
- [ ] Confirm updating an expense recalculates the total.
- [ ] Confirm changing expense to income subtracts from expenses and adds to
  income.

## Goal Service

- [ ] Create a goal.
- [ ] List goals.
- [ ] Update goal progress.
- [ ] Confirm a goal is marked completed when current amount reaches target
  amount.
- [ ] Delete a goal.

## Gamification Service

- [ ] Create a transaction.
- [ ] Confirm XP changes.
- [ ] Confirm profile endpoint loads.
- [ ] Confirm badge list loads.
- [ ] Confirm streak endpoint loads.
- [ ] Confirm duplicate badges are not awarded.
- [ ] Confirm transaction updates adjust XP deterministically.

## Insight Service

- [ ] Open the Insights page.
- [ ] Confirm an educational message loads.
- [ ] Confirm no external AI API key is required.
- [ ] Confirm insight endpoints can load dashboard context.

## Frontend

- [ ] Dashboard loads.
- [ ] Transactions page works.
- [ ] Monthly budget page works.
- [ ] Yearly budget page works.
- [ ] Goals page works.
- [ ] Achievements page works.
- [ ] Insights page works.
- [ ] Settings/profile page works.

## Debugging Rule

If the Transactions page changes but the Dashboard does not, think in this
order:

1. Did Transaction Service save the change?
2. Did Transaction Service publish the event?
3. Is RabbitMQ running?
4. Is Budget Service running?
5. Did Budget Service consume the event?
6. Did the frontend reload or refetch dashboard data?
