# BudgetQuest Runbook

This runbook is the practical operating guide for local development. Use it
when you want to start, stop, test, or debug the project.

## Start Order

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

Run this inside each backend service folder:

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

## Main URLs

- Eureka dashboard: `http://localhost:8761`
- API Gateway: `http://localhost:8763`
- Frontend: `http://localhost:5173`

## Docker Compose Mode

Start the full backend stack:

```bash
docker compose up --build
```

Start the full backend stack in the background:

```bash
docker compose up --build -d
```

Check running containers:

```bash
docker compose ps
```

View logs for one service:

```bash
docker compose logs api-gateway
```

Stop all containers:

```bash
docker compose down
```

Remove containers and local database volume data:

```bash
docker compose down -v
```

Main Docker URLs:

- Eureka: `http://localhost:8761`
- API Gateway health: `http://localhost:8763/actuator/health`
- RabbitMQ: `http://localhost:15672`
- Keycloak: `http://localhost:8080`

During development, run the frontend locally:

```bash
cd frontend
npm run dev
```

## Quick Health Check

After all services start:

1. Open `http://localhost:8761`.
2. Confirm all services are registered.
3. Open the frontend.
4. Log in.
5. Create a transaction.
6. Confirm dashboard totals update.
7. Delete the transaction.
8. Confirm dashboard totals update again.

## Common Problems

### Service Does Not Appear In Eureka

Check:

- Is `discovery-server` running?
- Did the service finish starting?
- Is the service using the correct Eureka URL?
- Is the service name configured correctly?

### Dashboard Says Data Could Not Load

Check:

- `api-gateway` is running.
- `budget-service` is running.
- `transaction-service` is running.
- The user is logged in.
- The browser console has no authorization error.

### Budget Totals Do Not Update

Check:

- RabbitMQ is running.
- Transaction Service published an event.
- Budget Service is running.
- Budget Service logs show event handling.
- The changed transaction event includes previous values for updates.

### Frontend Does Not Load

Check:

- `npm run dev` is running.
- Frontend port is `5173`.
- API Gateway port is `8763`.
- The frontend environment points to the gateway URL.

### Transaction Appears But Dashboard Does Not Change

This usually means the command side worked but the event side did not.

Check:

1. Transaction Service saved the transaction.
2. Transaction Service published the event.
3. RabbitMQ accepted the event.
4. Budget Service consumed the event.
5. Dashboard refetched the budget summary.

## Debugging Mindset

In a microservice app, ask two questions:

1. Which service owns this data?
2. Which service is supposed to react to this event?

Those questions usually point you to the right logs and the right code file.
