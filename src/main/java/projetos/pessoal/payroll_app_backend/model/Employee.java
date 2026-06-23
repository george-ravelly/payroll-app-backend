package projetos.pessoal.payroll_app_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    private String id; // O Mongo usa String/ObjectId por padrão
    private boolean active;
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    @Indexed(unique = true)
    private String cpf;
    
    private BigDecimal baseSalary;
    private String departmentName; // Guardar direto o nome simplifica para o MVP
    private String phone;
    private String position;
    private LocalDate hireDate;
    private String stripeAccountId;
}
