# Integration Hub Downstream Mock API

Standalone mock API that simulates a DWP-style benefit checker dependency for the Integration Hub end-to-end flow.

See the API Platform's
[pilot mock API end-to-end flow](https://github.com/ministryofjustice/integration-hub-api-platform/blob/main/docs/pilot-mock-api-flow.md)
for the complete journey from the upstream client through Integration Hub to
this downstream provider.

## What this service does

- Exposes an independently authenticated downstream endpoint at `POST /v1/benefit-checks/assessments`
- Uses deterministic business rules so orchestration flows can be tested repeatedly
- Protects the endpoint with HTTP Basic authentication so it behaves more like an external partner API than an internal HMPPS service

## Authentication

The API expects HTTP Basic credentials.

Default local credentials:

- Username: `orchestration-client`
- Password: `orchestration-secret`

Override them with:

- `DOWNSTREAM_MOCK_BASIC_AUTH_USERNAME`
- `DOWNSTREAM_MOCK_BASIC_AUTH_PASSWORD`

## Running locally

```bash
GRADLE_USER_HOME=/tmp/integration-hub-downstream-mock-api-gradle ./gradlew bootRun --args='--spring.profiles.active=dev'
```

Swagger UI is available locally at:

- `http://localhost:8080/swagger-ui/index.html`

## Example request

```bash
curl -i \
  -u orchestration-client:orchestration-secret \
  -H 'Content-Type: application/json' \
  -X POST http://localhost:8080/v1/benefit-checks/assessments \
  -d '{
    "firstName": "Alex",
    "lastName": "Morgan",
    "nino": "AA123456A",
    "dateOfBirth": "1990-01-01",
    "claimedBenefits": ["UNIVERSAL_CREDIT", "CHILD_BENEFIT"],
    "annualIncome": 21000,
    "savingsAmount": 1000,
    "housingCostsPerMonth": 950,
    "dependantChildren": 2,
    "disabledApplicant": true,
    "caringResponsibilities": false,
    "postcode": "SW1A 1AA"
  }'
```

## Tests

```bash
GRADLE_USER_HOME=/tmp/integration-hub-downstream-mock-api-gradle ./gradlew test
```

## Deployment direction

The development deployment targets the dedicated Modernisation Platform ECS service:

- ECR repository: `integration-hub-downstream-mock-api`
- ECS cluster and service: `integration-hub-downstream-mock-api`
- GitHub environment: `integration-hub-downstream-mock-api-development`
- AWS authentication: GitHub OIDC using the scoped application deployment role

Pull requests run the test suite. A push to `main` builds an immutable commit-SHA image, pushes it to ECR, registers a new task-definition revision and waits for the ECS service to become stable.

The temporary `aws-ecs-deployment` branch trigger also publishes an immutable commit-SHA image so the initial Terraform-managed service can be bootstrapped before this workflow reaches `main`.
