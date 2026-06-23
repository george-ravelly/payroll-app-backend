package projetos.pessoal.payroll_app_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import projetos.pessoal.payroll_app_backend.model.PayrollPeriod;
import projetos.pessoal.payroll_app_backend.model.enums.PayrollStatus;

public record PayrollListItemResponse(
    String id,
    String employeeId,
    String employeeName,
    String departmentName,
    String positionName,
    PayrollPeriod period,
    BigDecimal grossAmount,
    BigDecimal netAmount,
    PayrollStatus status,
    LocalDateTime paymentDate
) {
}
