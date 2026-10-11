package com.smartparking.model;

import java.util.Objects;

/**
 * Represents an individual parking slot in the parking lot.
 * Demonstrates Encapsulation by shielding its internal vehicle state
 * behind verified mutator methods (park, vacate).
 */
public class ParkingSlot {
    private final String slotId;
    private final VehicleType supportedType;
    private boolean occupied;
    private Vehicle parkedVehicle;

    /**
     * Constructs a parking slot.
     *
     * @param slotId        Unique slot identifier (e.g., "C-01", "B-01")
     * @param supportedType The vehicle type this slot can accommodate
     */
    public ParkingSlot(String slotId, VehicleType supportedType) {
        if (slotId == null || slotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Slot ID cannot be null or empty.");
        }
        this.slotId = slotId.trim().toUpperCase();
        this.supportedType = Objects.requireNonNull(supportedType, "Supported vehicle type must not be null.");
        this.occupied = false;
        this.parkedVehicle = null;
    }

    public String getSlotId() {
        return slotId;
    }

    public VehicleType getSupportedType() {
        return supportedType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public boolean isAvailable() {
        return !occupied;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    /**
     * Checks if this slot can accommodate the given vehicle type.
     */
    public boolean isCompatibleWith(VehicleType type) {
        return this.supportedType == type;
    }

    /**
     * Parks a vehicle into this slot.
     *
     * @param vehicle The vehicle to park
     * @throws IllegalStateException if the slot is already occupied
     * @throws IllegalArgumentException if the vehicle type does not match slot type
     */
    public void park(Vehicle vehicle) {
        if (this.occupied) {
            throw new IllegalStateException("Slot " + slotId + " is already occupied!");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot park a null vehicle.");
        }
        if (!isCompatibleWith(vehicle.getVehicleType())) {
            throw new IllegalArgumentException(String.format(
                    "Incompatible vehicle type! Slot %s is reserved for %s, but vehicle is a %s.",
                    slotId, supportedType.getDisplayName(), vehicle.getVehicleType().getDisplayName()));
        }
        this.parkedVehicle = vehicle;
        this.occupied = true;
    }

    /**
     * Vacates the slot and returns the vehicle that was parked.
     *
     * @return The released vehicle, or null if slot was already empty
     */
    public Vehicle vacate() {
        if (!this.occupied) {
            return null;
        }
        Vehicle released = this.parkedVehicle;
        this.parkedVehicle = null;
        this.occupied = false;
        return released;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingSlot other)) return false;
        return Objects.equals(this.slotId, other.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId);
    }

    @Override
    public String toString() {
        String status = occupied ? ("OCCUPIED by [" + parkedVehicle.getLicensePlate() + "]") : "AVAILABLE";
        return String.format("Slot %-5s | For: %-4s | Status: %s",
                slotId, supportedType.getDisplayName(), status);
    }
}
