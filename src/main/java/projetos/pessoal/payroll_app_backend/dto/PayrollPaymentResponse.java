package projetos.pessoal.payroll_app_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import projetos.pessoal.payroll_app_backend.model.enums.PaymentMethod;
import projetos.pessoal.payroll_app_backend.model.enums.PaymentStatus;

public record PayrollPaymentResponse(
    String payrollId,
    String employeeId,
    String employeeName,
    BigDecimal amount,
    PaymentStatus status,
    PaymentMethod paymentMethod,
    LocalDateTime paymentDate,
    String transactionId
) {
}
