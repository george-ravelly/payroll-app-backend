package projetos.pessoal.payroll_app_backend.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projetos.pessoal.payroll_app_backend.model.enums.ItemType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollItem {
    private String description;
    private BigDecimal value;
    private ItemType type; // ENTRY ou DEDUCTION
}
