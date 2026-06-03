package projetos.pessoal.payroll_app_backend.service;

import org.springframework.stereotype.Service;

import projetos.pessoal.payroll_app_backend.repository.PayrollRepository;

@Service
public class PayrollService {
    private final PayrollRepository payrollRepository;
    
    public PayrollService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }
}
