package projetos.pessoal.payroll_app_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import projetos.pessoal.payroll_app_backend.model.Payroll;

@Repository
public interface PayrollRepository extends MongoRepository<Payroll, String> {

    Optional<List<Payroll>> findByEmployeeId(String employeeId);

    Optional<List<Payroll>> findByReferencePeriod(LocalDate period);

    Optional<Payroll> findByReferencePeriodAndEmployeeId(LocalDate period, String employeeId);
    
}
