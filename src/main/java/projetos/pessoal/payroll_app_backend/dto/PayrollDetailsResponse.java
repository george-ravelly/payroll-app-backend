package projetos.pessoal.payroll_app_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import projetos.pessoal.payroll_app_backend.model.PayrollItem;
import projetos.pessoal.payroll_app_backend.model.PayrollPeriod;
import projetos.pessoal.payroll_app_backend.model.enums.PayrollStatus;

public record PayrollDetailsResponse(
    String id,
    String employeeId,
    String employeeName,
    String departmentName,
    String positionName,
    PayrollPeriod period,
    List<PayrollItem> items,
    BigDecimal grossAmount,
    BigDecimal totalEarnings,
    BigDecimal totalDeductions,
    BigDecimal netAmount,
    PayrollStatus status,
    LocalDateTime generatedAt,
    LocalDateTime paymentDate
) {
}
