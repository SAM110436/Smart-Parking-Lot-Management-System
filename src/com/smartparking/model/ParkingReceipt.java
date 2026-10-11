package com.smartparking.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Immutable value object representing a finalized parking checkout receipt.
 */
public class ParkingReceipt {
    private final String receiptId;
    private final Vehicle vehicle;
    private final String slotId;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final long totalMinutes;
    private final long billableHours;
    private final double hourlyRate;
    private final double totalFee;

    public ParkingReceipt(String receiptId, Vehicle vehicle, String slotId,
                          LocalDateTime entryTime, LocalDateTime exitTime,
                          long totalMinutes, long billableHours,
                          double hourlyRate, double totalFee) {
        this.receiptId = receiptId;
        this.vehicle = vehicle;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalMinutes = totalMinutes;
        this.billableHours = billableHours;
        this.hourlyRate = hourlyRate;
        this.totalFee = totalFee;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getSlotId() {
        return slotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public long getTotalMinutes() {
        return totalMinutes;
    }

    public long getBillableHours() {
        return billableHours;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getTotalFee() {
        return totalFee;
    }

    /**
     * Formats receipt into a beautiful ASCII ticket representation.
     */
    public String toFormattedReceipt() {
        long hoursPart = totalMinutes / 60;
        long minsPart = totalMinutes % 60;

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("             SMART PARKING LOT RECEIPT              \n");
        sb.append("====================================================\n");
        sb.append(String.format(" Receipt ID      : %s\n", receiptId));
        sb.append(String.format(" Registration No : %s\n", vehicle.getLicensePlate()));
        sb.append(String.format(" Vehicle Type    : %s\n", vehicle.getVehicleType().getDisplayName()));
        sb.append(String.format(" Owner Name      : %s\n", vehicle.getOwnerName()));
        sb.append(String.format(" Slot Released   : %s\n", slotId));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" Entry Time      : %s\n", entryTime.format(Vehicle.DATE_TIME_FORMATTER)));
        sb.append(String.format(" Exit Time       : %s\n", exitTime.format(Vehicle.DATE_TIME_FORMATTER)));
        sb.append(String.format(" Actual Duration : %d hours %d minutes (%d total mins)\n", hoursPart, minsPart, totalMinutes));
        sb.append(String.format(" Billable Hours  : %d hour(s) [Round-up ceiling]\n", billableHours));
        sb.append(String.format(" Hourly Rate     : %s / hour\n", Vehicle.formatInr(hourlyRate)));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" TOTAL FEE DUE   : %s\n", Vehicle.formatInr(totalFee)));
        sb.append("====================================================\n");
        sb.append("         Thank You For Parking With Us!             \n");
        sb.append("====================================================\n");
        return sb.toString();
    }
}
