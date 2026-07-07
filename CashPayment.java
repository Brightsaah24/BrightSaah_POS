package model;

import exceptions.ValidationException;

import java.math.BigDecimal;

public class CashPayment extends Payment {
    public CashPayment(BigDecimal amount) {
        super(amount);
    }

    @Override
    public String getMethodName() {
        return "Cash";
    }

    @Override
    public String getReference() {
        return "Tendered: " + getAmount();
    }

    @Override
    public void validate(BigDecimal totalDue) throws ValidationException {
        if (getAmount().compareTo(totalDue) < 0) {
            throw new ValidationException("Cash tendered is less than the total due.");
        }
    }

    public BigDecimal getChange(BigDecimal totalDue) {
        return getAmount().subtract(totalDue);
    }
}
