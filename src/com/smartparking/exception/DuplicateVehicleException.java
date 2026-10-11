package com.smartparking.exception;

/**
 * Exception thrown when an attempt is made to park a vehicle
 * whose registration number is already actively registered/parked in the lot.
 */
public class DuplicateVehicleException extends Exception {
    private static final long serialVersionUID = 1L;
    public DuplicateVehicleException(String message) {
        super(message);
    }

    public DuplicateVehicleException(String licensePlate, String slotId) {
        super(String.format("Vehicle with registration number '%s' is ALREADY PARKED in slot '%s'. Duplicate active registration is not permitted.",
                licensePlate, slotId));
    }
}
