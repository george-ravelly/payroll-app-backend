package projetos.pessoal.payroll_app_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import projetos.pessoal.payroll_app_backend.model.Employee;
import projetos.pessoal.payroll_app_backend.repository.EmployeeRepository;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }
        if (employee.getDepartmentName() == null || employee.getDepartmentName().isEmpty()) {
            throw new IllegalArgumentException("Employee department cannot be null or empty");
        }
        if (employee.getCpf() == null || employee.getCpf().isEmpty()) {
            throw new IllegalArgumentException("Employee CPF cannot be null or empty");
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployeesActive(boolean active) {
        return employeeRepository.findAllByActive(active);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    public Employee updateEmployee(String id, Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (employee.getName() != null && !employee.getName().isEmpty()) {
            existingEmployee.setName(employee.getName());
        }
        if (employee.getDepartmentName() != null && !employee.getDepartmentName().isEmpty()) {
            existingEmployee.setDepartmentName(employee.getDepartmentName());
        }
        if (employee.getCpf() != null && !employee.getCpf().isEmpty()) {
            existingEmployee.setCpf(employee.getCpf());
        }

        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(String id) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        existingEmployee.setActive(false);
        employeeRepository.save(existingEmployee);
    }
}
