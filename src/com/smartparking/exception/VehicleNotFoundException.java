package com.smartparking.exception;

/**
 * Exception thrown when a vehicle search, lookup, or removal operation
 * cannot locate the specified vehicle registration number in the active parking lot.
 */
public class VehicleNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public VehicleNotFoundException(String message) {
        super(message);
    }

    public static VehicleNotFoundException forLicensePlate(String licensePlate) {
        return new VehicleNotFoundException(
                String.format("No active vehicle found with registration number '%s'. Please check the number and try again.",
                        licensePlate));
    }
}
