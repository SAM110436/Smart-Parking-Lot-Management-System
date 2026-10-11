package com.smartparking.model;

import com.smartparking.exception.InvalidVehicleTypeException;

/**
 * Enum representing supported vehicle types in the Smart Parking Lot.
 * Encapsulates the display name, slot identifier prefix, and standard default hourly rate.
 */
public enum VehicleType {
    CAR("Car", "C-", 40.0),
    BIKE("Bike", "B-", 20.0);

    private final String displayName;
    private final String slotPrefix;
    private final double defaultHourlyRate;

    VehicleType(String displayName, String slotPrefix, double defaultHourlyRate) {
        this.displayName = displayName;
        this.slotPrefix = slotPrefix;
        this.defaultHourlyRate = defaultHourlyRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSlotPrefix() {
        return slotPrefix;
    }

    public double getDefaultHourlyRate() {
        return defaultHourlyRate;
    }

    /**
     * Parses a string representation into a VehicleType enum.
     * Accepts names ("CAR", "BIKE") or number shortcuts ("1" for Car, "2" for Bike).
     *
     * @param input Raw user input string
     * @return Corresponding VehicleType
     * @throws InvalidVehicleTypeException if the input doesn't match any supported vehicle type
     */
    public static VehicleType fromString(String input) throws InvalidVehicleTypeException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidVehicleTypeException("null/empty", "CAR, BIKE (or 1 for Car, 2 for Bike)");
        }

        String cleaned = input.trim().toUpperCase();

        if (cleaned.equals("1") || cleaned.equals("CAR") || cleaned.equals("C")) {
            return CAR;
        } else if (cleaned.equals("2") || cleaned.equals("BIKE") || cleaned.equals("B") || cleaned.equals("MOTORCYCLE")) {
            return BIKE;
        } else {
            throw new InvalidVehicleTypeException(input.trim(), "CAR, BIKE (or 1 for Car, 2 for Bike)");
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
