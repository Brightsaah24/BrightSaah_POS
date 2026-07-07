package model;

import exceptions.ValidationException;

import java.math.BigDecimal;

public class MobileMoneyPayment extends Payment {
    private final String transactionId;

    public MobileMoneyPayment(BigDecimal amount, String transactionId) {
        super(amount);
        this.transactionId = transactionId == null ? "" : transactionId.trim();
    }

    @Override
    public String getMethodName() {
        return "Mobile Money";
    }

    @Override
    public String getReference() {
        return "Txn: " + transactionId;
    }

    @Override
    public void validate(BigDecimal totalDue) throws ValidationException {
        if (getAmount().compareTo(totalDue) != 0) {
            throw new ValidationException("Mobile money payment must exactly match the total due.");
        }
        if (transactionId.length() < 6) {
            throw new ValidationException("Enter a valid mobile money transaction ID.");
        }
    }
}
