package projetos.pessoal.payroll_app_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import projetos.pessoal.payroll_app_backend.model.PayrollItem;
import projetos.pessoal.payroll_app_backend.model.enums.PayrollStatus;

public record UpdatePayrollRequest(
    List<PayrollItem> items,
    PayrollStatus status,
    LocalDateTime paymentDate
) {
}
