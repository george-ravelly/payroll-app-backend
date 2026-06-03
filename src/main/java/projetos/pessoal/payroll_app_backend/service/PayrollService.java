package projetos.pessoal.payroll_app_backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import projetos.pessoal.payroll_app_backend.model.Payroll;
import projetos.pessoal.payroll_app_backend.repository.PayrollRepository;

@Service
public class PayrollService {
    private final PayrollRepository payrollRepository;
    
    public PayrollService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    public void processPayroll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPayroll'");
    }

    public List<Payroll> getAllPayrolls(LocalDate period, long employeeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllPayrolls'");
    }

    public Payroll getPayrollById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPayrollById'");
    }
}
