# ExperimentLab

**A full-stack product experimentation platform for creating, running, and analyzing A/B tests.**

ExperimentLab simulates how users interact with different product experiences, allowing teams to compare variant performance and evaluate product decisions using measurable outcomes.

## Overview

ExperimentLab is designed to demonstrate the engineering behind a product experimentation platform—from experiment configuration and traffic allocation to synthetic user simulation, event tracking, and performance analytics.

Users can explore a demo environment, create experiments, configure variants, run simulations, and analyze the resulting metrics.

## Features

### Experiment Management
- Create and manage product experiments.
- Define experiment hypotheses and primary metrics.
- Configure experiment variants.
- Edit and delete variants while experiments are in draft.
- Validate traffic allocation across variants.
- Lock configuration after an experiment starts.

### Simulation Engine
- Generate synthetic users and simulate interactions.
- Assign users to variants based on configured traffic allocation.
- Generate exposure, click, and conversion events.
- Persist assignments and events through backend services.
- Run simulations with configurable user counts.

### Analytics and Reporting
- View simulation history.
- Compare historical simulation results.
- Analyze performance by variant.
- Track:
    - Click-through rate (CTR)
    - Conversion rate
    - Click-to-conversion rate
    - Assigned users
    - Exposures
    - Clicks
    - Conversions
- Visualize results through interactive charts and comparison tables.

### Demo Sessions
- Explore the platform without creating an account.
- Maintain isolated demo sessions.
- Scope experiment access to the active session.

## Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React, TypeScript, Vite |
| Styling | Tailwind CSS |
| Data Fetching | TanStack Query |
| Routing | React Router |
| Data Visualization | Recharts |
| Backend | Java 25, Spring Boot |
| API | Spring Web |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Database Migrations | Flyway |
| Validation | Spring Validation |
| Testing | Spring Boot Test |

## Architecture

ExperimentLab uses a separate frontend and backend communicating through REST APIs.

```text
React + TypeScript Frontend
          |
          | REST API
          v
Spring Boot Backend
          |
          v
Application Services
          |
          v
Spring Data JPA
          |
          v
PostgreSQL Database
```

The frontend handles experiment configuration, user interactions, and analytics visualization.

The backend manages experiment lifecycle operations, variant configuration, traffic allocation, synthetic user assignment, event generation, and analytics aggregation.

Synthetic simulation data is processed through backend services and persisted in PostgreSQL rather than generated exclusively in the frontend.

Flyway manages database schema migrations, while Hibernate validates the database schema at startup.

## Local Development Setup

### Prerequisites

- Java 25
- Node.js and npm
- PostgreSQL
- Git

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd experimentlab
```

### 2. Create the PostgreSQL database

Make sure PostgreSQL is running locally.

Create the database:

```sql
CREATE DATABASE experimentlab;
```

### 3. Configure the backend

Open:

`backend/src/main/resources/application.properties`

Configure your local PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/experimentlab
spring.datasource.username=YOUR_POSTGRES_USERNAME
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

Flyway applies the database migrations when the backend starts.

### 4. Start the backend

From the project root:

```bash
cd backend
./mvnw spring-boot:run
```

The backend runs at:

`http://localhost:8080`

### 5. Configure and start the frontend

Open a second terminal:

```bash
cd frontend
npm install
```

Create or configure the frontend `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Start the development server:

```bash
npm run dev
```

Vite will display the local frontend URL in the terminal.

## Running Tests and Builds

### Backend

From the `backend` directory:

```bash
./mvnw test
```

### Frontend

From the `frontend` directory:

```bash
npm run build
npm run lint
```

## Engineering Considerations

ExperimentLab explores several important full-stack engineering concepts:

- REST API design and separation of concerns.
- Session-based access control and ownership validation.
- Database persistence and schema migrations.
- Traffic allocation and randomized user assignment.
- Event-driven simulation and metric aggregation.
- Historical data retrieval.
- Frontend server-state management with TanStack Query.
- Input validation and experiment lifecycle constraints.
- Responsive data visualization.

## Project Structure

```text
experimentlab/
├── backend/
│   ├── src/
│   │   └── main/
│   │       └── resources/
│   │           ├── db/
│   │           │   └── migration/
│   │           └── application.properties
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
│
├── docs/
└── README.md
```

## Project Status

The core experiment lifecycle, simulation engine, and analytics workflow are implemented. Functional testing has covered experiment management, variant configuration, simulation execution, historical analytics, validation, and demo-session isolation.

---

**ExperimentLab** — Explore experiments. Simulate behavior. Measure outcomes.
