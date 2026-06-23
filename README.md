# payroll-app-backend

Backend Spring Boot para o app `payment_paper`, com contratos alinhados aos tipos TypeScript do frontend e ponto de integração para pagamentos via Stripe Connect.

## Configuração

Use variáveis de ambiente para apontar MongoDB e Stripe:

```bash
export MONGODB_URI="mongodb://localhost:27017/payroll_db"
export STRIPE_API_KEY="sk_test_..."
```

O projeto está configurado para Java 21.

## Endpoints principais

- `POST /api/auth/login` com `{ "email": "...", "cpf": "..." }`
- `GET /api/employees`
- `POST /api/employees`
- `GET /api/employees/{id}`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`
- `POST /api/payrolls` cria folha manual com `employeeId`, `period` e `items`
- `POST /api/payrolls/process` gera folha por funcionário ou em lote
- `GET /api/payrolls?employeeId=&month=&year=`
- `GET /api/payrolls/latest?employeeId=...`
- `GET /api/payrolls/history/{employeeId}`
- `GET /api/payrolls/{id}`
- `PUT /api/payrolls/{id}`
- `PATCH /api/payrolls/{id}/status?status=PAID`
- `DELETE /api/payrolls/{id}`
- `GET /api/payrolls/summary`
- `POST /api/payrolls/payments/process` com `{ "payrollId": "...", "paymentMethod": "PIX" }`

## Formato esperado pelo frontend

Os nomes seguem os tipos de `payment_paper/src/types`:

- Funcionário: `id`, `name`, `email`, `cpf`, `departmentName`, `phone`, `active`, `position`, `hireDate`
- Folha: `period.month`, `period.year`, `items[].amount`, `items[].type` como `EARNING` ou `DEDUCTION`
- Totais: `grossAmount`, `totalEarnings`, `totalDeductions`, `netAmount`
- Status: `DRAFT`, `PENDING`, `PAID`, `CANCELLED`
