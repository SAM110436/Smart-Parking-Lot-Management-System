package com.smartparking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Abstract class representing a generic Vehicle.
 * Demonstrates:
 * 1. Abstraction - provides common structure while leaving rate and type details to subclasses.
 * 2. Encapsulation - all fields are private with validated constructors and controlled getters.
 */
public abstract class Vehicle {
    // Encapsulated private attributes
    private final String licensePlate;
    private final String ownerName;
    private final LocalDateTime entryTime;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a Vehicle with basic validation.
     *
     * @param licensePlate Vehicle registration number (e.g. "KA-01-AB-1234")
     * @param ownerName    Name of the vehicle owner or driver
     * @param entryTime    Time when the vehicle entered the parking lot
     */
    public Vehicle(String licensePlate, String ownerName, LocalDateTime entryTime) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty.");
        }
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.ownerName = ownerName.trim();
        this.entryTime = (entryTime != null) ? entryTime : LocalDateTime.now();
    }

    /**
     * Polymorphic method: Returns the specific vehicle type (Car or Bike).
     */
    public abstract VehicleType getVehicleType();

    /**
     * Polymorphic method: Returns the hourly parking rate for this specific vehicle category.
     */
    public abstract double getHourlyRate();

    /**
     * Polymorphic method: Returns subclass-specific details (e.g., car body type, bike engine cc).
     */
    public abstract String getSpecificDetails();

    // Getters demonstrating encapsulation
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getFormattedEntryTime() {
        return entryTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Console-safe INR amount. Uses ASCII "Rs." so Windows terminals that cannot
     * render the ₹ glyph still display a readable rupee value.
     */
    public static String formatInr(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount < 0) {
            amount = 0;
        }
        return String.format("Rs.%.2f (INR)", amount);
    }

    /**
     * Two vehicles are considered equal if their normalized license plates match.
     * Essential for proper behavior in Java collections (HashMap, HashSet).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle other)) return false;
        return Objects.equals(this.licensePlate, other.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licensePlate);
    }

    @Override
    public String toString() {
        return String.format("[%s] Reg: %-12s | Owner: %-15s | Entry: %s | %s",
                getVehicleType().getDisplayName(),
                licensePlate,
                ownerName,
                getFormattedEntryTime(),
                getSpecificDetails());
    }
}
