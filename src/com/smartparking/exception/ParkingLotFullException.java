package com.smartparking.exception;

/**
 * Exception thrown when there are no available parking slots
 * for the requested vehicle type.
 */
public class ParkingLotFullException extends Exception {
    private static final long serialVersionUID = 1L;
    public ParkingLotFullException(String message) {
        super(message);
    }

    public ParkingLotFullException(String vehicleType, int totalCapacity) {
        super(String.format("Parking lot is FULL! No available slots for vehicle type: %s (Capacity: %d).",
                vehicleType, totalCapacity));
    }
}
