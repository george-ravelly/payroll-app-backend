package projetos.pessoal.payroll_app_backend.model.records;

import projetos.pessoal.payroll_app_backend.model.PayrollPeriod;

public record ProcessPayroll(PayrollPeriod period, String employeeId) {
    
}
