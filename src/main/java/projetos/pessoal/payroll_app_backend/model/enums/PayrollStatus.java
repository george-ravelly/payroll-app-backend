package projetos.pessoal.payroll_app_backend.model.enums;

public enum PayrollStatus {
    PENDING, PAID, CANCELLED;

    public static PayrollStatus fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        switch (name) {
            case "PENDING":
                return PENDING;
            case "PAID":
                return PAID;
            case "CANCELLED":
                return CANCELLED;
            default:
                throw new IllegalArgumentException("Unknown status: " + name);
        }
    }
}
