package projetos.pessoal.payroll_app_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import projetos.pessoal.payroll_app_backend.model.Payroll;
import projetos.pessoal.payroll_app_backend.model.records.ProcessPayroll;
import projetos.pessoal.payroll_app_backend.service.PayrollService;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {
    private final PayrollService payrollService;
    
    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/process")
    public void processPayroll(@RequestBody ProcessPayroll processPayroll) {
        payrollService.processPayroll(processPayroll);
    }

    @GetMapping
    public List<Payroll> getAllPayrolls(
        @RequestParam(required = false) LocalDate period,
        @RequestParam(required = false) String employeeId
    ) {
        return payrollService.getAllPayrolls(period, employeeId);
    }


    @GetMapping("/{id}")
    public Payroll getPayrollById(@PathVariable String id) {
        return payrollService.getPayrollById(id);
    }

    @PatchMapping("/{id}/status")
    public Payroll updatePayrollStatus(
        @PathVariable String id,
        @RequestParam String status
    ) {
        return payrollService.updatePayrollStatus(id, status);
    }
}
