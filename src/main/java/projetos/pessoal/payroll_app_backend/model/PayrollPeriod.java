package projetos.pessoal.payroll_app_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollPeriod {
    private int month;
    private int year;
}
