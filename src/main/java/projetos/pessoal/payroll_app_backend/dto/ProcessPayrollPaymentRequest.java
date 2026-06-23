package projetos.pessoal.payroll_app_backend.dto;

import projetos.pessoal.payroll_app_backend.model.enums.PaymentMethod;

public record ProcessPayrollPaymentRequest(
    String payrollId,
    PaymentMethod paymentMethod
) {
}
