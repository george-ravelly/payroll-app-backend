package projetos.pessoal.payroll_app_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import projetos.pessoal.payroll_app_backend.model.Employee;
import projetos.pessoal.payroll_app_backend.model.Payroll;
import projetos.pessoal.payroll_app_backend.model.PayrollItem;
import projetos.pessoal.payroll_app_backend.model.enums.ItemType;
import projetos.pessoal.payroll_app_backend.model.enums.PayrollStatus;
import projetos.pessoal.payroll_app_backend.model.records.ProcessPayroll;
import projetos.pessoal.payroll_app_backend.repository.PayrollRepository;

@Service
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final EmployeeService employeeService;
    
    public PayrollService(PayrollRepository payrollRepository, EmployeeService employeeService) {
        this.payrollRepository = payrollRepository;
        this.employeeService = employeeService;
    }

    /*
        Gera em lote ou individualmente as folhas de pagamento para um mês de referência específico. O sistema busca o baseSalary do funcionário, aplica as regras tributárias/descontos e salva o registro como PENDING.
    */
    public void processPayroll(ProcessPayroll processPayroll) {
       List<Employee> funcionarios;

        // 1. Determina se o processamento é individual ou em lote
        if (processPayroll.employeeId() != null && !processPayroll.employeeId().isEmpty()) {
            Employee emp = employeeService.getEmployeeById(processPayroll.employeeId());
            funcionarios = List.of(emp);
        } else {
            funcionarios = employeeService.getAllEmployees();
        }

        // 2. Processa a folha para cada funcionário encontrado
        for (Employee emp : funcionarios) {
            
            // Evita duplicidade: deleta a folha antiga do mesmo período se ela ainda estiver PENDING
            payrollRepository.findByReferencePeriodAndEmployeeId(processPayroll.referencePeriod(), emp.getId())
                .ifPresent(oldPayroll -> {
                    if (oldPayroll.getStatus() == PayrollStatus.PENDING) {
                        payrollRepository.delete(oldPayroll);
                    } else {
                        throw new IllegalStateException("A folha deste período já foi paga ou fechada.");
                    }
                });

            // 3. Cria a nova estrutura da folha
            Payroll payroll = new Payroll();
            payroll.setEmployeeId(emp.getId());
            payroll.setEmployeeName(emp.getName());
            payroll.setReferencePeriod(processPayroll.referencePeriod());
            payroll.setStatus(PayrollStatus.PENDING);

            // 4. Calcula os itens da folha (Regra de Negócio)
            calcPayrollItems(payroll, emp);

            // 5. Salva o documento unificado no MongoDB
            payrollRepository.save(payroll);
        }
    }

    private void calcPayrollItems(Payroll payroll, Employee employee) {
        BigDecimal salarioBase = employee.getBaseSalary();
        List<PayrollItem> itens = new ArrayList<>();

        // ─── 1. PROVENTOS (ENTRIES) ───
        // Item do Salário Base
        itens.add(new PayrollItem("Salário Base", salarioBase, ItemType.ENTRY));
        
        // Exemplo de Provento Variável (ex: Bônus fixo ou Hora Extra se houvesse no banco)
        // Verificar como calcular o bônus, aqui é apenas um exemplo fixo
        BigDecimal bonus = BigDecimal.ZERO; 
        if (bonus.compareTo(BigDecimal.ZERO) > 0) {
            itens.add(new PayrollItem("Bônus de Performance", bonus, ItemType.ENTRY));
        }

        BigDecimal salarioBruto = salarioBase.add(bonus);

        // ─── 2. DESCONTOS (DEDUCTIONS) ───
        // Vou deixar a alíquota simplificada de 11% por enquanto, mas isso pode ser ajustado para seguir a tabela progressiva real do INSS
        BigDecimal inss = salarioBruto.multiply(new BigDecimal("0.11"))
                                    .setScale(2, RoundingMode.HALF_UP);
        itens.add(new PayrollItem("Desconto INSS (11%)", inss, ItemType.DEDUCTION));

        // Cálculo Simulado de IRRF (ex: alíquota simplificada de 7.5% sobre o que sobra do INSS)
        BigDecimal baseCalculoIrrf = salarioBruto.subtract(inss);
        BigDecimal irrf = baseCalculoIrrf.multiply(new BigDecimal("0.075"))
                                        .setScale(2, RoundingMode.HALF_UP);
        itens.add(new PayrollItem("Imposto de Renda (7.5%)", irrf, ItemType.DEDUCTION));

        // ─── 3. CONSOLIDAR TOTAIS ───
        BigDecimal totalDescontos = inss.add(irrf);
        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescontos);

        // Injeta os valores calculados no objeto principal
        payroll.setItems(itens);
        payroll.setGrossSalary(salarioBruto);
        payroll.setTotalDeductions(totalDescontos);
        payroll.setNetSalary(salarioLiquido);
    }

    public List<Payroll> getAllPayrolls(LocalDate period, String employeeId) {
        throw new IllegalArgumentException("Folha de pagamento não encontrada.");
    }

    public Payroll getPayrollById(String id) {
        return payrollRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Folha de pagamento não encontrada."));
    }

    public Payroll updatePayrollStatus(String id, String status) {
        if (payrollRepository.existsById(id)) {
            Payroll payroll = payrollRepository.findById(id).orElseThrow();
            payroll.setStatus(PayrollStatus.fromString(status));
            return payrollRepository.save(payroll);
        }
        throw new IllegalArgumentException("Folha de pagamento não encontrada.");
    }
}
