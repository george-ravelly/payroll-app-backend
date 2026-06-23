package projetos.pessoal.payroll_app_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;

import projetos.pessoal.payroll_app_backend.dto.CreatePayrollRequest;
import projetos.pessoal.payroll_app_backend.dto.PayrollDetailsResponse;
import projetos.pessoal.payroll_app_backend.dto.PayrollListItemResponse;
import projetos.pessoal.payroll_app_backend.dto.PayrollPaymentResponse;
import projetos.pessoal.payroll_app_backend.dto.PayrollSummaryResponse;
import projetos.pessoal.payroll_app_backend.dto.ProcessPayrollPaymentRequest;
import projetos.pessoal.payroll_app_backend.dto.UpdatePayrollRequest;
import projetos.pessoal.payroll_app_backend.model.Employee;
import projetos.pessoal.payroll_app_backend.model.Payroll;
import projetos.pessoal.payroll_app_backend.model.PayrollItem;
import projetos.pessoal.payroll_app_backend.model.enums.ItemType;
import projetos.pessoal.payroll_app_backend.model.enums.PaymentStatus;
import projetos.pessoal.payroll_app_backend.model.enums.PayrollStatus;
import projetos.pessoal.payroll_app_backend.model.records.ProcessPayroll;
import projetos.pessoal.payroll_app_backend.repository.PayrollRepository;

@Service
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final EmployeeService employeeService;
    private final StripePayoutService stripePayoutService;
    
    public PayrollService(
        PayrollRepository payrollRepository,
        EmployeeService employeeService,
        StripePayoutService stripePayoutService
    ) {
        this.payrollRepository = payrollRepository;
        this.employeeService = employeeService;
        this.stripePayoutService = stripePayoutService;
    }

    public Payroll createPayroll(CreatePayrollRequest request) {
        Employee employee = employeeService.getEmployeeById(request.employeeId());

        payrollRepository.findByPeriodMonthAndPeriodYearAndEmployee_Id(
            request.period().getMonth(),
            request.period().getYear(),
            request.employeeId()
        ).ifPresent(existing -> {
            throw new IllegalStateException("Payroll already exists for this employee and period.");
        });

        Payroll payroll = new Payroll();
        payroll.setEmployee(snapshotEmployee(employee));
        payroll.setPeriod(request.period());
        payroll.setItems(prepareItems(request.items()));
        payroll.setStatus(PayrollStatus.DRAFT);
        payroll.setGeneratedAt(LocalDateTime.now());
        recalculateTotals(payroll);

        return payrollRepository.save(payroll);
    }

    public List<Payroll> processPayroll(ProcessPayroll processPayroll) {
        List<Employee> employees;
        if (processPayroll.employeeId() != null && !processPayroll.employeeId().isBlank()) {
            employees = List.of(employeeService.getEmployeeById(processPayroll.employeeId()));
        } else {
            employees = employeeService.getAllEmployeesActive(true);
        }

        List<Payroll> generatedPayrolls = new ArrayList<>();
        for (Employee employee : employees) {
            payrollRepository.findByPeriodMonthAndPeriodYearAndEmployee_Id(
                processPayroll.period().getMonth(),
                processPayroll.period().getYear(),
                employee.getId()
            ).ifPresent(existing -> {
                if (existing.getStatus() == PayrollStatus.PAID) {
                    throw new IllegalStateException("Paid payroll cannot be regenerated.");
                }
                payrollRepository.delete(existing);
            });

            Payroll payroll = new Payroll();
            payroll.setEmployee(snapshotEmployee(employee));
            payroll.setPeriod(processPayroll.period());
            payroll.setStatus(PayrollStatus.PENDING);
            payroll.setGeneratedAt(LocalDateTime.now());
            payroll.setItems(defaultItemsFor(employee));
            recalculateTotals(payroll);
            generatedPayrolls.add(payrollRepository.save(payroll));
        }
        return generatedPayrolls;
    }

    public List<PayrollListItemResponse> getAllPayrolls(Integer month, Integer year, String employeeId) {
        List<Payroll> payrolls;
        if (employeeId != null && !employeeId.isBlank()) {
            payrolls = payrollRepository.findByEmployee_IdOrderByGeneratedAtDesc(employeeId);
        } else if (month != null && year != null) {
            payrolls = payrollRepository.findByPeriodMonthAndPeriodYear(month, year);
        } else {
            payrolls = payrollRepository.findAll();
        }

        return payrolls.stream().map(this::toListItem).toList();
    }

    public List<Payroll> getPayrollHistory(String employeeId) {
        return payrollRepository.findByEmployee_IdOrderByGeneratedAtDesc(employeeId);
    }

    public PayrollDetailsResponse getLatestPayrollDetails(String employeeId) {
        return payrollRepository.findFirstByEmployee_IdOrderByGeneratedAtDesc(employeeId)
            .map(this::toDetails)
            .orElseThrow(() -> new IllegalArgumentException("Payroll not found."));
    }

    public PayrollDetailsResponse getPayrollById(String id) {
        return payrollRepository.findById(id)
            .map(this::toDetails)
            .orElseThrow(() -> new IllegalArgumentException("Payroll not found."));
    }

    public Payroll updatePayroll(String id, UpdatePayrollRequest request) {
        Payroll payroll = findPayroll(id);
        if (request.items() != null) {
            payroll.setItems(prepareItems(request.items()));
            recalculateTotals(payroll);
        }
        if (request.status() != null) {
            payroll.setStatus(request.status());
        }
        if (request.paymentDate() != null) {
            payroll.setPaymentDate(request.paymentDate());
        }
        return payrollRepository.save(payroll);
    }

    public Payroll updatePayrollStatus(String id, String status) {
        Payroll payroll = findPayroll(id);
        payroll.setStatus(PayrollStatus.fromString(status));
        return payrollRepository.save(payroll);
    }

    public void deletePayroll(String id) {
        payrollRepository.delete(findPayroll(id));
    }

    public PayrollSummaryResponse getSummary() {
        List<Payroll> payrolls = payrollRepository.findAll();
        return new PayrollSummaryResponse(
            payrolls.size(),
            payrolls.stream().filter(p -> p.getStatus() == PayrollStatus.PENDING).count(),
            payrolls.stream().filter(p -> p.getStatus() == PayrollStatus.PAID).count(),
            payrolls.stream().filter(p -> p.getStatus() == PayrollStatus.CANCELLED).count(),
            payrolls.stream().map(Payroll::getGrossAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
            payrolls.stream().map(Payroll::getNetAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    public PayrollPaymentResponse processPayment(ProcessPayrollPaymentRequest request) {
        Payroll payroll = findPayroll(request.payrollId());
        Employee employee = payroll.getEmployee();
        if (employee.getStripeAccountId() == null || employee.getStripeAccountId().isBlank()) {
            throw new IllegalStateException("Employee does not have a Stripe connected account.");
        }

        try {
            String transactionId = stripePayoutService.realizarPagamentoSalarial(payroll, employee.getStripeAccountId());
            LocalDateTime paymentDate = LocalDateTime.now();
            payroll.setStatus(PayrollStatus.PAID);
            payroll.setPaymentDate(paymentDate);
            payroll.setPaymentTransactionId(transactionId);
            payrollRepository.save(payroll);

            return new PayrollPaymentResponse(
                payroll.getId(),
                employee.getId(),
                employee.getName(),
                payroll.getNetAmount(),
                PaymentStatus.PAID,
                request.paymentMethod(),
                paymentDate,
                transactionId
            );
        } catch (StripeException exception) {
            return new PayrollPaymentResponse(
                payroll.getId(),
                employee.getId(),
                employee.getName(),
                payroll.getNetAmount(),
                PaymentStatus.FAILED,
                request.paymentMethod(),
                null,
                null
            );
        }
    }

    private Payroll findPayroll(String id) {
        return payrollRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Payroll not found."));
    }

    private Employee snapshotEmployee(Employee employee) {
        Employee snapshot = new Employee();
        snapshot.setId(employee.getId());
        snapshot.setName(employee.getName());
        snapshot.setEmail(employee.getEmail());
        snapshot.setCpf(employee.getCpf());
        snapshot.setDepartmentName(employee.getDepartmentName());
        snapshot.setPhone(employee.getPhone());
        snapshot.setPosition(employee.getPosition());
        snapshot.setHireDate(employee.getHireDate());
        snapshot.setActive(employee.isActive());
        snapshot.setBaseSalary(employee.getBaseSalary());
        snapshot.setStripeAccountId(employee.getStripeAccountId());
        return snapshot;
    }

    private List<PayrollItem> defaultItemsFor(Employee employee) {
        BigDecimal baseSalary = employee.getBaseSalary() == null ? BigDecimal.ZERO : employee.getBaseSalary();
        List<PayrollItem> items = new ArrayList<>();
        items.add(new PayrollItem(UUID.randomUUID().toString(), "Salário Base", baseSalary, ItemType.EARNING));

        BigDecimal inss = baseSalary.multiply(new BigDecimal("0.11")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal irrf = baseSalary.subtract(inss).multiply(new BigDecimal("0.075")).setScale(2, RoundingMode.HALF_UP);
        items.add(new PayrollItem(UUID.randomUUID().toString(), "INSS", inss, ItemType.DEDUCTION));
        items.add(new PayrollItem(UUID.randomUUID().toString(), "IRRF", irrf, ItemType.DEDUCTION));
        return items;
    }

    private List<PayrollItem> prepareItems(List<PayrollItem> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream().map(item -> new PayrollItem(
            item.getId() == null || item.getId().isBlank() ? UUID.randomUUID().toString() : item.getId(),
            item.getDescription(),
            item.getAmount() == null ? BigDecimal.ZERO : item.getAmount(),
            item.getType()
        )).toList();
    }

    private void recalculateTotals(Payroll payroll) {
        BigDecimal totalEarnings = payroll.getItems().stream()
            .filter(item -> item.getType() == ItemType.EARNING)
            .map(PayrollItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDeductions = payroll.getItems().stream()
            .filter(item -> item.getType() == ItemType.DEDUCTION)
            .map(PayrollItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        payroll.setTotalEarnings(totalEarnings);
        payroll.setGrossAmount(totalEarnings);
        payroll.setTotalDeductions(totalDeductions);
        payroll.setNetAmount(totalEarnings.subtract(totalDeductions));
    }

    private PayrollListItemResponse toListItem(Payroll payroll) {
        Employee employee = payroll.getEmployee();
        return new PayrollListItemResponse(
            payroll.getId(),
            employee.getId(),
            employee.getName(),
            employee.getDepartmentName(),
            employee.getPosition(),
            payroll.getPeriod(),
            payroll.getGrossAmount(),
            payroll.getNetAmount(),
            payroll.getStatus(),
            payroll.getPaymentDate()
        );
    }

    private PayrollDetailsResponse toDetails(Payroll payroll) {
        Employee employee = payroll.getEmployee();
        return new PayrollDetailsResponse(
            payroll.getId(),
            employee.getId(),
            employee.getName(),
            employee.getDepartmentName(),
            employee.getPosition(),
            payroll.getPeriod(),
            payroll.getItems(),
            payroll.getGrossAmount(),
            payroll.getTotalEarnings(),
            payroll.getTotalDeductions(),
            payroll.getNetAmount(),
            payroll.getStatus(),
            payroll.getGeneratedAt(),
            payroll.getPaymentDate()
        );
    }
}
