package projetos.pessoal.payroll_app_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import projetos.pessoal.payroll_app_backend.model.Employee;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
