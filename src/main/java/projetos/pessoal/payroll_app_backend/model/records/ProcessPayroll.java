package projetos.pessoal.payroll_app_backend.model.records;

import java.time.LocalDate;

public record ProcessPayroll(LocalDate referencePeriod, String employeeId) {
    
}
