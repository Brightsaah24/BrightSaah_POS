package model;

import exceptions.ValidationException;

import java.math.BigDecimal;

public abstract class Payment {
    private final BigDecimal amount;

    protected Payment(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public abstract String getMethodName();

    public abstract String getReference();

    public abstract void validate(BigDecimal totalDue) throws ValidationException;
}
