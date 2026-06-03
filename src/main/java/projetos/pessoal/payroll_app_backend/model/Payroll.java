package projetos.pessoal.payroll_app_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "payrolls")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payroll {
    @Id
    private String id;
    
    private String employeeId; // Referência direta ao ID do funcionário
    private String employeeName; // Snapshot do nome para evitar lookups/joins
    private LocalDate referencePeriod;
    
    private BigDecimal grossSalary;
    private BigDecimal totalDeductions;
    private BigDecimal netSalary;
    private PayrollStatus status;
    
    // Lista de subdocumentos embutida diretamente
    private List<PayrollItem> items = new ArrayList<>();
    
    // Getters, Setters
}
