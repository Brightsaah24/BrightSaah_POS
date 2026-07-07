package model;

import exceptions.ValidationException;

import java.math.BigDecimal;

public class CardPayment extends Payment {
    private final String authorizationCode;

    public CardPayment(BigDecimal amount, String authorizationCode) {
        super(amount);
        this.authorizationCode = authorizationCode == null ? "" : authorizationCode.trim();
    }

    @Override
    public String getMethodName() {
        return "Card";
    }

    @Override
    public String getReference() {
        return "Auth: " + authorizationCode;
    }

    @Override
    public void validate(BigDecimal totalDue) throws ValidationException {
        if (getAmount().compareTo(totalDue) != 0) {
            throw new ValidationException("Card payment must exactly match the total due.");
        }
        if (authorizationCode.length() < 4) {
            throw new ValidationException("Enter a valid card authorization code.");
        }
    }
}
