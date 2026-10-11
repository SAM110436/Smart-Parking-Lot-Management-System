package com.smartparking.service;

import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service class responsible for computing parking duration and billing fees.
 * Implements round-up-to-next-hour billing rule with a 1-hour minimum charge.
 */
public class ParkingFeeCalculator {
    private double carHourlyRate;
    private double bikeHourlyRate;

    /**
     * Default constructor with standard rates (₹40.00/hr for Car, ₹20.00/hr for Bike).
     */
    public ParkingFeeCalculator() {
        this(VehicleType.CAR.getDefaultHourlyRate(), VehicleType.BIKE.getDefaultHourlyRate());
    }

    /**
     * Parameterized constructor for configurable rates.
     */
    public ParkingFeeCalculator(double carHourlyRate, double bikeHourlyRate) {
        setCarHourlyRate(carHourlyRate);
        setBikeHourlyRate(bikeHourlyRate);
    }

    public double getCarHourlyRate() {
        return carHourlyRate;
    }

    public void setCarHourlyRate(double carHourlyRate) {
        if (carHourlyRate <= 0) {
            throw new IllegalArgumentException("Hourly rate must be positive.");
        }
        this.carHourlyRate = carHourlyRate;
    }

    public double getBikeHourlyRate() {
        return bikeHourlyRate;
    }

    public void setBikeHourlyRate(double bikeHourlyRate) {
        if (bikeHourlyRate <= 0) {
            throw new IllegalArgumentException("Hourly rate must be positive.");
        }
        this.bikeHourlyRate = bikeHourlyRate;
    }

    public double getRateForVehicleType(VehicleType type) {
        return switch (type) {
            case CAR -> carHourlyRate;
            case BIKE -> bikeHourlyRate;
        };
    }

    /**
     * Computes total duration in minutes between entry and exit.
     */
    public long calculateDurationMinutes(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null) {
            throw new IllegalArgumentException("Entry time and exit time must not be null.");
        }
        if (exitTime.isBefore(entryTime)) {
            // Guard against negative durations: treat inverted timestamps as immediate minimum checkout
            return 1;
        }
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        return Math.max(1, minutes); // Treat immediate checkout as at least 1 minute
    }

    /**
     * Computes billable hours using round-up-to-next-hour (ceiling) logic.
     * Minimum billable duration is 1 hour.
     *
     * Example:
     *  - 1 to 60 minutes   -> 1 hour
     *  - 61 to 120 minutes -> 2 hours
     *  - 125 minutes       -> 3 hours
     */
    public long calculateBillableHours(long durationMinutes) {
        if (durationMinutes <= 0) {
            return 1;
        }
        return (long) Math.ceil(durationMinutes / 60.0);
    }

    /**
     * Calculates the total parking fee.
     *
     * @param vehicle   The vehicle parked
     * @param entryTime Recorded check-in time
     * @param exitTime  Recorded check-out time
     * @return Total fee in INR
     */
    public double calculateFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        long minutes = calculateDurationMinutes(entryTime, exitTime);
        long billableHours = calculateBillableHours(minutes);
        double rate = vehicle.getHourlyRate();
        double fee = billableHours * rate;
        if (fee < 0) {
            return 0;
        }
        return Math.round(fee * 100.0) / 100.0;
    }

    /**
     * Formats a monetary amount for console output (delegates to Vehicle.formatInr).
     */
    public static String formatInr(double amount) {
        return Vehicle.formatInr(amount);
    }

    /**
     * Returns a formatted text explaining the active parking fee rules.
     */
    public String getFeeRulesSummary() {
        return "====================================================\n" +
               "               PARKING FEE RULES & TARIFF           \n" +
               "====================================================\n" +
               String.format(" 1. Car Hourly Rate       : %s / hour\n", Vehicle.formatInr(carHourlyRate)) +
               String.format("    Unicode symbol        : \u20B9%.2f / hour\n", carHourlyRate) +
               String.format(" 2. Bike Hourly Rate      : %s / hour\n", Vehicle.formatInr(bikeHourlyRate)) +
               String.format("    Unicode symbol        : \u20B9%.2f / hour\n", bikeHourlyRate) +
               " 3. Minimum Billing       : 1 Hour minimum charge applies.\n" +
               " 4. Billing Method        : Round-up-to-next-hour (Ceiling billing).\n" +
               "    * 1 to 60 minutes     -> Billed as 1 Full Hour\n" +
               "    * 61 to 120 minutes   -> Billed as 2 Full Hours\n" +
               "    * 121 to 180 minutes  -> Billed as 3 Full Hours\n" +
               " 5. Currency              : Indian Rupees (INR / Rs. / \u20B9)\n" +
               " 6. Lost Ticket / Support : Contact Parking Helpdesk\n" +
               "====================================================\n";
    }
}
