package projetos.pessoal.payroll_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import projetos.pessoal.payroll_app_backend.model.Employee;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    List<Employee> findAllByActive(boolean active);

    Optional<Employee> findByEmailAndCpf(String email, String cpf);
}
