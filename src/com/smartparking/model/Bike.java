package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Concrete subclass representing a Bike / Motorcycle.
 * Demonstrates:
 * 1. Inheritance: Extends Vehicle, inheriting all base fields and behaviors.
 * 2. Polymorphism: Overrides abstract methods to provide bike-specific logic and hourly rate.
 */
public class Bike extends Vehicle {
    private final String bikeType; // e.g., "Standard Scooter", "Sports Bike", "Cruiser"
    private final double customHourlyRate;

    /**
     * Standard constructor for Bike using default vehicle type rate.
     */
    public Bike(String licensePlate, String ownerName, LocalDateTime entryTime, String bikeType) {
        this(licensePlate, ownerName, entryTime, bikeType, VehicleType.BIKE.getDefaultHourlyRate());
    }

    /**
     * Overloaded constructor allowing a custom hourly rate.
     */
    public Bike(String licensePlate, String ownerName, LocalDateTime entryTime, String bikeType, double customHourlyRate) {
        super(licensePlate, ownerName, entryTime);
        this.bikeType = (bikeType != null && !bikeType.trim().isEmpty()) ? bikeType.trim() : "Standard Two-Wheeler";
        this.customHourlyRate = customHourlyRate > 0 ? customHourlyRate : VehicleType.BIKE.getDefaultHourlyRate();
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.BIKE;
    }

    @Override
    public double getHourlyRate() {
        return customHourlyRate;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Type: %s (Rate: %s/hr)", bikeType,
                com.smartparking.service.ParkingFeeCalculator.formatInr(customHourlyRate));
    }

    public String getBikeType() {
        return bikeType;
    }
}
