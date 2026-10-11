package com.smartparking.model;

/**
 * Encapsulates the current operational and financial statistics of the parking lot.
 */
public class ParkingStatistics {
    private final int totalCapacity;
    private final int totalOccupied;
    private final int totalAvailable;

    private final int carCapacity;
    private final int carOccupied;
    private final int carAvailable;

    private final int bikeCapacity;
    private final int bikeOccupied;
    private final int bikeAvailable;

    private final double totalRevenue;
    private final int totalVehiclesServed;

    public ParkingStatistics(int totalCapacity, int totalOccupied, int totalAvailable,
                             int carCapacity, int carOccupied, int carAvailable,
                             int bikeCapacity, int bikeOccupied, int bikeAvailable,
                             double totalRevenue, int totalVehiclesServed) {
        this.totalCapacity = totalCapacity;
        this.totalOccupied = totalOccupied;
        this.totalAvailable = totalAvailable;
        this.carCapacity = carCapacity;
        this.carOccupied = carOccupied;
        this.carAvailable = carAvailable;
        this.bikeCapacity = bikeCapacity;
        this.bikeOccupied = bikeOccupied;
        this.bikeAvailable = bikeAvailable;
        this.totalRevenue = totalRevenue;
        this.totalVehiclesServed = totalVehiclesServed;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getTotalOccupied() {
        return totalOccupied;
    }

    public int getTotalAvailable() {
        return totalAvailable;
    }

    public int getCarCapacity() {
        return carCapacity;
    }

    public int getCarOccupied() {
        return carOccupied;
    }

    public int getCarAvailable() {
        return carAvailable;
    }

    public int getBikeCapacity() {
        return bikeCapacity;
    }

    public int getBikeOccupied() {
        return bikeOccupied;
    }

    public int getBikeAvailable() {
        return bikeAvailable;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalVehiclesServed() {
        return totalVehiclesServed;
    }

    public int getParkedCarCount() {
        return carOccupied;
    }

    public int getParkedBikeCount() {
        return bikeOccupied;
    }

    public double getOccupancyRate() {
        if (totalCapacity == 0) return 0.0;
        return ((double) totalOccupied / totalCapacity) * 100.0;
    }

    public String toFormattedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("            PARKING LOT REAL-TIME METRICS           \n");
        sb.append("====================================================\n");
        sb.append(String.format(" Total Slots Capacity : %d\n", totalCapacity));
        sb.append(String.format(" Currently Occupied   : %d (%.1f%% Occupancy)\n", totalOccupied, getOccupancyRate()));
        sb.append(String.format(" Currently Available  : %d\n", totalAvailable));
        sb.append("----------------------------------------------------\n");
        sb.append(" [Car Slots Breakdown]\n");
        sb.append(String.format("   Total: %d | Parked Cars: %d | Available: %d\n",
                carCapacity, carOccupied, carAvailable));
        sb.append(" [Bike Slots Breakdown]\n");
        sb.append(String.format("   Total: %d | Parked Bikes: %d | Available: %d\n",
                bikeCapacity, bikeOccupied, bikeAvailable));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" Total Vehicles Checked Out : %d\n", totalVehiclesServed));
        sb.append(String.format(" Total Revenue Collected    : %s\n",
                com.smartparking.service.ParkingFeeCalculator.formatInr(totalRevenue)));
        sb.append("====================================================\n");
        return sb.toString();
    }
}
