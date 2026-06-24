package projetos.pessoal.payroll_app_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Schedule {
    private String clockIn;
    private String clockOut;
}
