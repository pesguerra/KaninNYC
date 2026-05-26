# Kanin NYC Backend

## Database Setup

Run the production schema first, then the initial data:

```sql
source server/database/production-schema.sql;
source server/database/initial-data.sql;
```

The backend connects to `kanin_nyc` by default:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/kanin_nyc}
```

Seeded login accounts all use the password `password`:

```text
admin@kaninnyc.com
cashier@kaninnyc.com
chef@kaninnyc.com
```

The app stores passwords as plain text because this project is not intended for public deployment.

Protected requests use the same simple course-style authorization header pattern as the workbook: log in, copy the returned `user` value, and send it as the `authorization` header.

## Stripe Checkout

Card orders use Stripe hosted Checkout Sessions. Add these environment variables before starting the backend:

```powershell
$env:STRIPE_SECRET_KEY="sk_test_..."
$env:STRIPE_CURRENCY="usd"
$env:CLIENT_BASE_URL="http://localhost:5173"
```

`CLIENT_BASE_URL` must match the exact React URL you use in the browser so Stripe returns to the same site where the cashier is logged in.
