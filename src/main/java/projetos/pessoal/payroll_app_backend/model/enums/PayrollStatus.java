package projetos.pessoal.payroll_app_backend.model.enums;

public enum PayrollStatus {
    DRAFT, PENDING, PAID, CANCELLED;

    public static PayrollStatus fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return PayrollStatus.valueOf(name.toUpperCase());
    }
}
