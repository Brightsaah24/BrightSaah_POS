package model;

public class CashierUser extends User {
    public CashierUser(String username, String fullName, String password) {
        super(username, fullName, password);
    }

    @Override
    public UserRole getRole() {
        return UserRole.CASHIER;
    }
}
