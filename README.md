Library  Sync

A full-stack app that pulls book data from the Open Library Search API into MySQL,
detects changes with SHA-256 hashes, and shows everything on a React dashboard.

Tech stack
React + Vite (JavaScript) | Spring Boot 3 (Java 21, Maven) | MySQL + JPA/Hibernate | Spring Scheduling

Architecture
```
```
- controller: REST endpoints. service: sync logic. repository: database access.
- entity: JPA tables. dto: JSON shapes. client: Open Library calls. config: CORS, scheduling, errors.

Sync logic
1. A sync starts from the scheduler (every 5 minutes) or from `POST /api/sync`.
2. An `AtomicBoolean` lock blocks overlapping syncs (a second request gets HTTP 409).
3. A `sync_runs` row is created with status RUNNING.
4. Books are fetched from Open Library (outside any DB transaction). Records without id or title are skipped.
5. For each book a SHA-256 hash of `title|authors|year` is computed and compared with the stored hash:
   - no row -> **NEW**; different hash -> **UPDATED**; same hash -> **UNCHANGED**.
6. All book changes are saved in a single transaction (all or nothing).
7. The sync run is marked SUCCESS with counts, or FAILED with an error message.

Dashboard totals come from GET /api/stats (database counts), not from the filtered list.

Setup
1. Create the database: `mysql -u root -p < database/schema.sql`
2. Create a DB user (see the setup guide) and set the password as an environment variable.
3. Backend: `cd backend && export DB_PASSWORD=... && mvn spring-boot:run`
4. Frontend: `cd frontend && npm install && npm run dev`
5. Open http://localhost:5173

Environment variables (backend)
| Name | Default |
|---|---|
| DB_URL | jdbc:mysql://localhost:3306/library_sync?... |
| DB_USERNAME | library_user |
| DB_PASSWORD | (empty - set it yourself, never commit it) |
| OPENLIBRARY_QUERY | programming |
| OPENLIBRARY_LIMIT | 50 |
| CORS_ORIGIN | http://localhost:5173 |

Frontend: `VITE_API_URL` (default `http://localhost:8080/api`).

## API
| Method | Path | Description |
|---|---|---|
| GET | /api/health | Health check |
| GET | /api/books?search=&status= | List books |
| GET | /api/sync-runs?limit=10 | Sync history |
| POST | /api/sync | Manual sync |
| GET | /api/stats | Totals and counts by status |
