package com.smartparking.exception;

/**
 * Exception thrown when an unrecognized, unsupported, or malformed
 * vehicle type is provided by the user.
 */
public class InvalidVehicleTypeException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidVehicleTypeException(String message) {
        super(message);
    }

    public InvalidVehicleTypeException(String inputType, String supportedTypes) {
        super(String.format("Invalid vehicle type '%s'. Supported types are: %s.",
                inputType, supportedTypes));
    }
}
