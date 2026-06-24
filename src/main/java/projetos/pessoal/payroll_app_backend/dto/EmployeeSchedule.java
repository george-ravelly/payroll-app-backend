package projetos.pessoal.payroll_app_backend.dto;

import projetos.pessoal.payroll_app_backend.model.Schedule;

public record EmployeeSchedule(
    String id,
    String name,
    String position,
    Schedule schedules
) {
    
}
