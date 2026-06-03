package projetos.pessoal.payroll_app_backend.service;

import org.springframework.stereotype.Service;

import projetos.pessoal.payroll_app_backend.repository.EmployeeRepository;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
}
