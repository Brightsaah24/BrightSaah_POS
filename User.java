package model;

public abstract class User {
    private final String username;
    private String fullName;
    private String password;
    private boolean active = true;

    protected User(String username, String fullName, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        this.username = username.trim();
        setFullName(fullName);
        setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        this.fullName = fullName.trim();
    }

    public boolean passwordMatches(String attemptedPassword) {
        return password.equals(attemptedPassword);
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must contain at least 4 characters.");
        }
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract UserRole getRole();

    public boolean canManageProducts() {
        return getRole() == UserRole.ADMINISTRATOR;
    }

    public boolean canManageUsers() {
        return getRole() == UserRole.ADMINISTRATOR;
    }

    public boolean canViewReports() {
        return getRole() == UserRole.ADMINISTRATOR;
    }
}
