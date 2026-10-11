package com.smartparking.service;

import com.smartparking.exception.DuplicateVehicleException;
import com.smartparking.exception.ParkingLotFullException;
import com.smartparking.exception.VehicleNotFoundException;
import com.smartparking.model.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Core business engine managing parking operations, slots, and vehicle tracking.
 *
 * Utilizes:
 * - ArrayList<ParkingSlot>: Ordered repository of all parking slots.
 * - HashMap<String, Vehicle>: O(1) key-value lookup of active vehicles by registration number.
 * - HashSet<String>: O(1) active vehicle registration presence check to prohibit duplicates.
 * - HashMap<String, ParkingSlot>: O(1) mapping between vehicle registration and assigned slot.
 */
public class ParkingLot {
    private final String lotName;
    private final ArrayList<ParkingSlot> slots;
    private final HashMap<String, Vehicle> activeVehicleMap;
    private final HashSet<String> activeRegistrationNumbers;
    private final HashMap<String, ParkingSlot> vehicleToSlotMap;

    private final ParkingFeeCalculator feeCalculator;
    private double totalRevenueCollected;
    private int totalVehiclesServed;
    private int receiptSequenceCounter;

    /**
     * Default constructor: initializes a standard parking lot with 10 Car slots and 10 Bike slots.
     */
    public ParkingLot() {
        this("Central Smart Parking Lot", 10, 10, new ParkingFeeCalculator());
    }

    /**
     * Parameterized constructor allowing custom slot capacities and custom fee calculator.
     */
    public ParkingLot(String lotName, int carSlotCount, int bikeSlotCount, ParkingFeeCalculator feeCalculator) {
        if (carSlotCount <= 0 || bikeSlotCount <= 0) {
            throw new IllegalArgumentException("Slot capacities must be greater than zero.");
        }
        this.lotName = (lotName != null) ? lotName : "Smart Parking Lot";
        this.slots = new ArrayList<>(carSlotCount + bikeSlotCount);
        this.activeVehicleMap = new HashMap<>();
        this.activeRegistrationNumbers = new HashSet<>();
        this.vehicleToSlotMap = new HashMap<>();
        this.feeCalculator = (feeCalculator != null) ? feeCalculator : new ParkingFeeCalculator();
        this.totalRevenueCollected = 0.0;
        this.totalVehiclesServed = 0;
        this.receiptSequenceCounter = 1000;

        initializeSlots(carSlotCount, bikeSlotCount);
    }

    /**
     * Populates the ArrayList with numbered Car and Bike slots.
     */
    private void initializeSlots(int carSlotCount, int bikeSlotCount) {
        // Initialize Car slots (C-01, C-02, ...)
        for (int i = 1; i <= carSlotCount; i++) {
            String slotId = String.format("C-%02d", i);
            slots.add(new ParkingSlot(slotId, VehicleType.CAR));
        }

        // Initialize Bike slots (B-01, B-02, ...)
        for (int i = 1; i <= bikeSlotCount; i++) {
            String slotId = String.format("B-%02d", i);
            slots.add(new ParkingSlot(slotId, VehicleType.BIKE));
        }
    }

    /**
     * Parks a vehicle into the first available compatible slot.
     * Maintains synchronized consistency across ArrayList, HashMap, and HashSet.
     *
     * @param vehicle The vehicle to be parked
     * @return The ParkingSlot allocated
     * @throws DuplicateVehicleException if registration number is already in active lot
     * @throws ParkingLotFullException   if no available slot exists for vehicle type
     */
    public synchronized ParkingSlot parkVehicle(Vehicle vehicle)
            throws DuplicateVehicleException, ParkingLotFullException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot park a null vehicle.");
        }

        String regNumber = vehicle.getLicensePlate();

        // 1. Fast O(1) duplicate check using HashSet
        if (activeRegistrationNumbers.contains(regNumber)) {
            ParkingSlot currentSlot = vehicleToSlotMap.get(regNumber);
            String slotId = (currentSlot != null) ? currentSlot.getSlotId() : "UNKNOWN";
            throw new DuplicateVehicleException(regNumber, slotId);
        }

        // 2. Find first compatible available slot in ArrayList
        ParkingSlot allocatedSlot = findAvailableSlot(vehicle.getVehicleType());
        if (allocatedSlot == null) {
            int capacity = getCapacityForType(vehicle.getVehicleType());
            throw new ParkingLotFullException(vehicle.getVehicleType().getDisplayName(), capacity);
        }

        // 3. Update Slot state
        allocatedSlot.park(vehicle);

        // 4. Synchronize all collections
        activeVehicleMap.put(regNumber, vehicle);
        activeRegistrationNumbers.add(regNumber);
        vehicleToSlotMap.put(regNumber, allocatedSlot);

        return allocatedSlot;
    }

    /**
     * Removes an actively parked vehicle, vacates its slot, calculates the fee,
     * updates financial metrics, and produces a parking receipt.
     *
     * @param licensePlate Registration number of the vehicle to check out
     * @param exitTime     Actual or simulated exit time
     * @return ParkingReceipt containing checkout billing breakdown
     * @throws VehicleNotFoundException if vehicle is not found in active lot
     */
    public synchronized ParkingReceipt removeVehicle(String licensePlate, LocalDateTime exitTime)
            throws VehicleNotFoundException {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }

        String normalizedPlate = licensePlate.trim().toUpperCase();

        // 1. Validate vehicle existence using HashSet / HashMap
        if (!activeRegistrationNumbers.contains(normalizedPlate)) {
            throw VehicleNotFoundException.forLicensePlate(normalizedPlate);
        }

        Vehicle vehicle = activeVehicleMap.get(normalizedPlate);
        ParkingSlot slot = vehicleToSlotMap.get(normalizedPlate);

        if (vehicle == null || slot == null) {
            throw VehicleNotFoundException.forLicensePlate(normalizedPlate);
        }

        LocalDateTime validExitTime = (exitTime != null) ? exitTime : LocalDateTime.now();
        if (validExitTime.isBefore(vehicle.getEntryTime())) {
            validExitTime = vehicle.getEntryTime().plusMinutes(1);
        }

        // 2. Compute duration and billing fee BEFORE mutating lot state
        long durationMinutes = feeCalculator.calculateDurationMinutes(vehicle.getEntryTime(), validExitTime);
        long billableHours = feeCalculator.calculateBillableHours(durationMinutes);
        double fee = feeCalculator.calculateFee(vehicle, vehicle.getEntryTime(), validExitTime);
        if (fee < 0) {
            fee = 0;
        }

        // 3. Vacate the physical slot only after a successful fee calculation
        slot.vacate();

        // 4. Atomic collection synchronization: remove from all data structures
        activeRegistrationNumbers.remove(normalizedPlate);
        activeVehicleMap.remove(normalizedPlate);
        vehicleToSlotMap.remove(normalizedPlate);

        // 5. Update revenue and served vehicle counters only after successful checkout
        totalRevenueCollected += fee;
        totalVehiclesServed++;
        String receiptId = "RCP-" + (++receiptSequenceCounter);

        return new ParkingReceipt(
                receiptId,
                vehicle,
                slot.getSlotId(),
                vehicle.getEntryTime(),
                validExitTime,
                durationMinutes,
                billableHours,
                vehicle.getHourlyRate(),
                fee
        );
    }

    /**
     * Searches for an actively parked vehicle by its registration number.
     *
     * @param licensePlate Vehicle registration number
     * @return Found Vehicle object
     * @throws VehicleNotFoundException if vehicle is not parked in lot
     */
    public Vehicle searchVehicle(String licensePlate) throws VehicleNotFoundException {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be empty.");
        }
        String normalized = licensePlate.trim().toUpperCase();
        Vehicle vehicle = activeVehicleMap.get(normalized);
        if (vehicle == null) {
            throw VehicleNotFoundException.forLicensePlate(normalized);
        }
        return vehicle;
    }

    /**
     * Retrieves the slot where a given vehicle is currently parked.
     */
    public ParkingSlot getSlotForVehicle(String licensePlate) throws VehicleNotFoundException {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }
        String normalized = licensePlate.trim().toUpperCase();
        ParkingSlot slot = vehicleToSlotMap.get(normalized);
        if (slot == null) {
            throw VehicleNotFoundException.forLicensePlate(normalized);
        }
        return slot;
    }

    /**
     * Checks if a vehicle is currently actively parked.
     */
    public boolean isVehicleParked(String licensePlate) {
        if (licensePlate == null) return false;
        return activeRegistrationNumbers.contains(licensePlate.trim().toUpperCase());
    }

    /**
     * Finds the first available slot compatible with the specified vehicle type.
     */
    private ParkingSlot findAvailableSlot(VehicleType type) {
        for (ParkingSlot slot : slots) {
            if (slot.isCompatibleWith(type) && slot.isAvailable()) {
                return slot;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of all parking slots.
     */
    public List<ParkingSlot> getAllSlots() {
        return Collections.unmodifiableList(slots);
    }

    /**
     * Returns an unmodifiable collection of all currently parked vehicles.
     */
    public Collection<Vehicle> getParkedVehicles() {
        return Collections.unmodifiableCollection(activeVehicleMap.values());
    }

    /**
     * Generates a comprehensive snapshot of parking lot statistics.
     */
    public ParkingStatistics getStatistics() {
        int totalCap = slots.size();
        int totalOcc = activeVehicleMap.size();
        int totalAvail = totalCap - totalOcc;

        int carCap = 0, carOcc = 0;
        int bikeCap = 0, bikeOcc = 0;

        for (ParkingSlot s : slots) {
            if (s.getSupportedType() == VehicleType.CAR) {
                carCap++;
                if (s.isOccupied()) carOcc++;
            } else if (s.getSupportedType() == VehicleType.BIKE) {
                bikeCap++;
                if (s.isOccupied()) bikeOcc++;
            }
        }

        return new ParkingStatistics(
                totalCap, totalOcc, totalAvail,
                carCap, carOcc, carCap - carOcc,
                bikeCap, bikeOcc, bikeCap - bikeOcc,
                totalRevenueCollected, totalVehiclesServed
        );
    }

    private int getCapacityForType(VehicleType type) {
        int count = 0;
        for (ParkingSlot s : slots) {
            if (s.getSupportedType() == type) count++;
        }
        return count;
    }

    public ParkingFeeCalculator getFeeCalculator() {
        return feeCalculator;
    }

    public String getLotName() {
        return lotName;
    }

    /**
     * Validates whether all internal collections (ArrayList, HashMap, HashSet)
     * are strictly synchronized and mathematically consistent.
     *
     * @return true if all collections are 100% consistent, false otherwise
     */
    public boolean isCollectionConsistencyValid() {
        // 1. Number of active plates must match active vehicle map count
        if (activeRegistrationNumbers.size() != activeVehicleMap.size()) {
            return false;
        }

        // 2. Number of active vehicles must match vehicleToSlotMap count
        if (activeVehicleMap.size() != vehicleToSlotMap.size()) {
            return false;
        }

        // 3. Count of physically occupied slots must equal active vehicle map count
        long occupiedCount = slots.stream().filter(ParkingSlot::isOccupied).count();
        if (occupiedCount != activeVehicleMap.size()) {
            return false;
        }

        // 4. Every key in activeRegistrationNumbers must exist in map and slot
        for (String plate : activeRegistrationNumbers) {
            Vehicle v = activeVehicleMap.get(plate);
            ParkingSlot s = vehicleToSlotMap.get(plate);
            if (v == null || s == null || !s.isOccupied() || !s.getParkedVehicle().getLicensePlate().equals(plate)) {
                return false;
            }
        }

        return true;
    }
}
