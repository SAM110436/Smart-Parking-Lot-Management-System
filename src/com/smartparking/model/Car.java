package com.smartparking.model;

import java.time.LocalDateTime;

/**
 * Concrete subclass representing a Car.
 * Demonstrates:
 * 1. Inheritance: Extends Vehicle, inheriting all base fields and behaviors.
 * 2. Polymorphism: Overrides abstract methods to provide car-specific logic and hourly rate.
 */
public class Car extends Vehicle {
    private final String carType; // e.g., "Sedan", "SUV", "Hatchback", "EV"
    private final double customHourlyRate;

    /**
     * Standard constructor for Car using default vehicle type rate.
     */
    public Car(String licensePlate, String ownerName, LocalDateTime entryTime, String carType) {
        this(licensePlate, ownerName, entryTime, carType, VehicleType.CAR.getDefaultHourlyRate());
    }

    /**
     * Overloaded constructor allowing a custom hourly rate.
     */
    public Car(String licensePlate, String ownerName, LocalDateTime entryTime, String carType, double customHourlyRate) {
        super(licensePlate, ownerName, entryTime);
        this.carType = (carType != null && !carType.trim().isEmpty()) ? carType.trim() : "Standard Sedan";
        this.customHourlyRate = customHourlyRate > 0 ? customHourlyRate : VehicleType.CAR.getDefaultHourlyRate();
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.CAR;
    }

    @Override
    public double getHourlyRate() {
        return customHourlyRate;
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Type: %s (Rate: %s/hr)", carType,
                com.smartparking.service.ParkingFeeCalculator.formatInr(customHourlyRate));
    }

    public String getCarType() {
        return carType;
    }
}
