package model;

public class AdminUser extends User {
    public AdminUser(String username, String fullName, String password) {
        super(username, fullName, password);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMINISTRATOR;
    }
}
