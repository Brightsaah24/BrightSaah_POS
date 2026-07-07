package service;

import exceptions.ValidationException;
import model.Customer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CustomerService {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public CustomerService() {
        addSeed(new Customer("CUS-1001", "Amina Bello", "555-0111", "amina@example.com", 80));
        addSeed(new Customer("CUS-1002", "Daniel Smith", "555-0122", "daniel@example.com", 25));
        addSeed(new Customer("CUS-1003", "Grace Mensah", "555-0133", "grace@example.com", 130));
    }

    public void addCustomer(Customer customer) throws ValidationException {
        validate(customer, true);
        customers.put(customer.getCustomerId(), customer);
    }

    public void updateCustomer(Customer customer) throws ValidationException {
        validate(customer, false);
        if (!customers.containsKey(customer.getCustomerId())) {
            throw new ValidationException("Select a valid customer to update.");
        }
        customers.put(customer.getCustomerId(), customer);
    }

    public void deleteCustomer(String customerId) throws ValidationException {
        if (!customers.containsKey(customerId)) {
            throw new ValidationException("Select a valid customer to delete.");
        }
        customers.remove(customerId);
    }

    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    public List<Customer> search(String query) {
        String normalized = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        return customers.values().stream()
                .filter(customer -> normalized.isBlank()
                        || customer.getCustomerId().toLowerCase(Locale.ROOT).contains(normalized)
                        || customer.getName().toLowerCase(Locale.ROOT).contains(normalized)
                        || customer.getPhone().toLowerCase(Locale.ROOT).contains(normalized)
                        || customer.getEmail().toLowerCase(Locale.ROOT).contains(normalized))
                .sorted(Comparator.comparing(Customer::getName))
                .toList();
    }

    public List<Customer> getAllCustomers() {
        return search("");
    }

    public int countCustomers() {
        return customers.size();
    }

    private void addSeed(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    private void validate(Customer customer, boolean unique) throws ValidationException {
        if (customer == null) {
            throw new ValidationException("Customer details are required.");
        }
        if (customer.getCustomerId().isBlank() || "WALK-IN".equals(customer.getCustomerId())) {
            throw new ValidationException("Customer ID is required.");
        }
        if (customer.getName().isBlank() || customer.isWalkIn()) {
            throw new ValidationException("Customer name is required.");
        }
        if (!customer.getEmail().isBlank() && !customer.getEmail().contains("@")) {
            throw new ValidationException("Enter a valid customer email address.");
        }
        if (unique && customers.containsKey(customer.getCustomerId())) {
            throw new ValidationException("Customer ID already exists.");
        }
    }
}
