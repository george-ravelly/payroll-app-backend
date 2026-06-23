package projetos.pessoal.payroll_app_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projetos.pessoal.payroll_app_backend.dto.UserLoginRequest;
import projetos.pessoal.payroll_app_backend.model.Employee;
import projetos.pessoal.payroll_app_backend.service.EmployeeService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public Employee login(@RequestBody UserLoginRequest request) {
        return employeeService.login(request.email(), request.cpf());
    }
}
