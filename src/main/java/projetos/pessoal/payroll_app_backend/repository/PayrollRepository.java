package projetos.pessoal.payroll_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import projetos.pessoal.payroll_app_backend.model.Payroll;

@Repository
public interface PayrollRepository extends MongoRepository<Payroll, String> {

    List<Payroll> findByEmployee_IdOrderByGeneratedAtDesc(String employeeId);

    Optional<Payroll> findFirstByEmployee_IdOrderByGeneratedAtDesc(String employeeId);

    List<Payroll> findByPeriodMonthAndPeriodYear(int month, int year);

    Optional<Payroll> findByPeriodMonthAndPeriodYearAndEmployee_Id(int month, int year, String employeeId);
    
}
