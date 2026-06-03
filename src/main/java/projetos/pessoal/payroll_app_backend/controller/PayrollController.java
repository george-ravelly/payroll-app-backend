package projetos.pessoal.payroll_app_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import projetos.pessoal.payroll_app_backend.model.Payroll;
import projetos.pessoal.payroll_app_backend.service.PayrollService;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {
    private final PayrollService payrollService;
    
    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /*
        Gera em lote ou individualmente as folhas de pagamento para um mês de referência específico. O sistema busca o baseSalary do funcionário, aplica as regras tributárias/descontos e salva o registro como PENDING.
    */
    @PostMapping("/process")
    public void processPayroll() {
        payrollService.processPayroll();
    }

    @GetMapping
    public List<Payroll> getAllPayrolls(
        @RequestParam(required = false) LocalDate period,
        @RequestParam(required = false) long employeeId
    ) {
        return payrollService.getAllPayrolls(period, employeeId);
    }


    @GetMapping("/{id}")
    public Payroll getPayrollById(@RequestParam Long id) {
        return payrollService.getPayrollById(id);
    }

    @PatchMapping("/{id}/status")
    public Payroll updatePayrollStatus(
        @RequestParam Long id,
        @RequestParam String status
    ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePayrollStatus'");
    }
}
