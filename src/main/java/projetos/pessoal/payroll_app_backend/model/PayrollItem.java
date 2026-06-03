package projetos.pessoal.payroll_app_backend.model;

import java.math.BigDecimal;

import com.stripe.param.radar.ValueListCreateParams.ItemType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayrollItem {
    private String description;
    private BigDecimal value;
    private ItemType type; // ENTRY ou DEDUCTION
}
