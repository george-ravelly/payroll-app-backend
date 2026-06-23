package projetos.pessoal.payroll_app_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import projetos.pessoal.payroll_app_backend.dto.CreatePayrollRequest;
import projetos.pessoal.payroll_app_backend.dto.PayrollDetailsResponse;
import projetos.pessoal.payroll_app_backend.dto.PayrollListItemResponse;
import projetos.pessoal.payroll_app_backend.dto.PayrollPaymentResponse;
import projetos.pessoal.payroll_app_backend.dto.PayrollSummaryResponse;
import projetos.pessoal.payroll_app_backend.dto.ProcessPayrollPaymentRequest;
import projetos.pessoal.payroll_app_backend.dto.UpdatePayrollRequest;
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
    public List<Payroll> processPayroll(@RequestBody ProcessPayroll processPayroll) {
        return payrollService.processPayroll(processPayroll);
    }

    @PostMapping
    public Payroll createPayroll(@RequestBody CreatePayrollRequest request) {
        return payrollService.createPayroll(request);
    }

    @GetMapping
    public List<PayrollListItemResponse> getAllPayrolls(
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) String employeeId
    ) {
        return payrollService.getAllPayrolls(month, year, employeeId);
    }

    @GetMapping("/summary")
    public PayrollSummaryResponse getSummary() {
        return payrollService.getSummary();
    }

    @GetMapping("/latest")
    public PayrollDetailsResponse getLatestPayroll(@RequestParam String employeeId) {
        return payrollService.getLatestPayrollDetails(employeeId);
    }

    @GetMapping("/history/{employeeId}")
    public List<Payroll> getPayrollHistory(@PathVariable String employeeId) {
        return payrollService.getPayrollHistory(employeeId);
    }

    @GetMapping("/{id}")
    public PayrollDetailsResponse getPayrollById(@PathVariable String id) {
        return payrollService.getPayrollById(id);
    }

    @PutMapping("/{id}")
    public Payroll updatePayroll(@PathVariable String id, @RequestBody UpdatePayrollRequest request) {
        return payrollService.updatePayroll(id, request);
    }

    @PatchMapping("/{id}/status")
    public Payroll updatePayrollStatus(
        @PathVariable String id,
        @RequestParam String status
    ) {
        return payrollService.updatePayrollStatus(id, status);
    }

    @PostMapping("/payments/process")
    public PayrollPaymentResponse processPayment(@RequestBody ProcessPayrollPaymentRequest request) {
        return payrollService.processPayment(request);
    }

    @DeleteMapping("/{id}")
    public void deletePayroll(@PathVariable String id) {
        payrollService.deletePayroll(id);
    }
}
