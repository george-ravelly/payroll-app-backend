package projetos.pessoal.payroll_app_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projetos.pessoal.payroll_app_backend.model.enums.PayrollStatus;

@Document(collection = "payrolls")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payroll {
    @Id
    private String id;
    
    private Employee employee;
    private PayrollPeriod period;
    
    private BigDecimal grossAmount;
    private BigDecimal totalEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal netAmount;
    private PayrollStatus status;
    private LocalDateTime generatedAt;
    private LocalDateTime paymentDate;
    private String paymentTransactionId;
    
    private List<PayrollItem> items = new ArrayList<>();
}
