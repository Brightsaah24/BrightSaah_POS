package service;

import exceptions.ValidationException;
import model.AdminUser;
import model.CashierUser;
import model.User;
import model.UserRole;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class UserService {
    private final Map<String, User> users = new LinkedHashMap<>();

    public UserService() {
        users.put("admin", new AdminUser("admin", "Administrator", "admin123"));
        users.put("cashier", new CashierUser("cashier", "Cashier User", "cash123"));
    }

    public Optional<User> authenticate(String username, String password) {
        User user = users.get(username == null ? "" : username.trim());
        if (user != null && user.isActive() && user.passwordMatches(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public void addUser(String username, String fullName, String password, UserRole role, boolean active) throws ValidationException {
        if (users.containsKey(username)) {
            throw new ValidationException("Username already exists.");
        }
        User user = createUser(username, fullName, password, role);
        user.setActive(active);
        users.put(user.getUsername(), user);
    }

    public void updateUser(String username, String fullName, String password, UserRole role, boolean active) throws ValidationException {
        if (!users.containsKey(username)) {
            throw new ValidationException("Select a valid user to update.");
        }
        User user = createUser(username, fullName, password, role);
        user.setActive(active);
        users.put(user.getUsername(), user);
    }

    public void deleteUser(String username) throws ValidationException {
        if ("admin".equals(username)) {
            throw new ValidationException("The default admin account cannot be deleted.");
        }
        if (!users.containsKey(username)) {
            throw new ValidationException("Select a valid user to delete.");
        }
        users.remove(username);
    }

    public List<User> search(String query) {
        String normalized = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        return users.values().stream()
                .filter(user -> normalized.isBlank()
                        || user.getUsername().toLowerCase(Locale.ROOT).contains(normalized)
                        || user.getFullName().toLowerCase(Locale.ROOT).contains(normalized)
                        || user.getRole().name().toLowerCase(Locale.ROOT).contains(normalized))
                .sorted(Comparator.comparing(User::getUsername))
                .toList();
    }

    private User createUser(String username, String fullName, String password, UserRole role) throws ValidationException {
        try {
            if (role == UserRole.ADMINISTRATOR) {
                return new AdminUser(username, fullName, password);
            }
            return new CashierUser(username, fullName, password);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }
}
