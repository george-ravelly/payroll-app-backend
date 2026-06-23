package projetos.pessoal.payroll_app_backend.dto;

import java.math.BigDecimal;

public record PayrollSummaryResponse(
    long totalPayrolls,
    long pendingPayrolls,
    long paidPayrolls,
    long cancelledPayrolls,
    BigDecimal totalGrossAmount,
    BigDecimal totalNetAmount
) {
}
