package model;

public class Customer {
    private final String customerId;
    private final String name;
    private final String phone;
    private final String email;
    private int loyaltyPoints;

    public Customer(String name, String phone, int loyaltyPoints) {
        this("WALK-IN", name, phone, "", loyaltyPoints);
    }

    public Customer(String customerId, String name, String phone, String email, int loyaltyPoints) {
        this.customerId = customerId == null || customerId.isBlank() ? "WALK-IN" : customerId.trim();
        this.name = name == null || name.isBlank() ? "Walk-in Customer" : name.trim();
        this.phone = phone == null ? "" : phone.trim();
        this.email = email == null ? "" : email.trim();
        this.loyaltyPoints = Math.max(0, loyaltyPoints);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addLoyaltyPoints(int points) {
        loyaltyPoints += Math.max(0, points);
    }

    public boolean isWalkIn() {
        return "Walk-in Customer".equals(name);
    }

    @Override
    public String toString() {
        return customerId + " - " + name;
    }
}
