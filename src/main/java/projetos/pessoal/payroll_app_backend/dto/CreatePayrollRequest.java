package projetos.pessoal.payroll_app_backend.dto;

import java.util.List;

import projetos.pessoal.payroll_app_backend.model.PayrollItem;
import projetos.pessoal.payroll_app_backend.model.PayrollPeriod;

public record CreatePayrollRequest(
    String employeeId,
    PayrollPeriod period,
    List<PayrollItem> items
) {
}
