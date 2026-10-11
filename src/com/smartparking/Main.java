package com.smartparking;

import com.smartparking.exception.DuplicateVehicleException;
import com.smartparking.exception.InvalidVehicleTypeException;
import com.smartparking.exception.ParkingLotFullException;
import com.smartparking.exception.VehicleNotFoundException;
import com.smartparking.model.*;
import com.smartparking.service.ParkingLot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Console-based User Interface for Smart Parking Lot Management System.
 *
 * Responsibilities:
 * - Presents the 8-option interactive console menu.
 * - Handles and validates all user inputs (with graceful recovery).
 * - Delegates all business logic to ParkingLot and model classes.
 * - Displays formatted receipts, tables, and statistics.
 */
public class Main {

    // Regular expression for validating registration numbers: letters, digits, spaces and hyphens (4 to 15 chars)
    private static final Pattern REG_PLATE_PATTERN = Pattern.compile("^[A-Z0-9 -]{4,15}$");

    // Regular expression for validating names: alphabets, spaces, apostrophes and dots (2 to 40 chars)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z .'-]{2,40}$");

    /**
     * Sentinel used when the input stream ends (piped EOF or closed console).
     */
    private static final class InputAbortedException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static void main(String[] args) {
        // Force System.out and System.err to UTF-8 so the ₹ (U+20B9) rupee symbol
        // always renders correctly regardless of the JVM launch flags or OS locale.
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            // If re-wrapping fails, continue with the default stream — functional at minimum.
        }

        // Initialize Parking Lot with 5 Car slots and 5 Bike slots for easy testing/demo
        ParkingLot parkingLot = new ParkingLot("Apex Smart Parking Facility", 5, 5, null);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        printWelcomeBanner();
        run(parkingLot, reader);
    }

    /**
     * Drives the main menu until the user exits or the input stream ends.
     * Package-visible so automated tests can supply scripted input.
     */
    public static void run(ParkingLot parkingLot, BufferedReader reader) {
        boolean exitRequested = false;
        while (!exitRequested) {
            displayMenu();
            int choice;
            try {
                choice = readMenuChoice(reader);
            } catch (InputAbortedException e) {
                handleExit(parkingLot);
                break;
            }

            System.out.println();
            try {
                switch (choice) {
                    case 1 -> handleParkVehicle(parkingLot, reader);
                    case 2 -> handleRemoveVehicle(parkingLot, reader);
                    case 3 -> handleDisplayParkingSlots(parkingLot);
                    case 4 -> handleSearchVehicle(parkingLot, reader);
                    case 5 -> handleDisplayParkedVehicles(parkingLot);
                    case 6 -> handleParkingStatistics(parkingLot);
                    case 7 -> handleParkingFeeRules(parkingLot);
                    case 8 -> {
                        if (confirmExit(reader)) {
                            exitRequested = true;
                            handleExit(parkingLot);
                        }
                    }
                    default -> System.out.println("[!] Invalid selection. Please select an option between 1 and 8.");
                }
            } catch (InputAbortedException e) {
                System.out.println("[INFO] Input ended. Returning to Main Menu.");
            }
            System.out.println();
        }
    }

    private static void printWelcomeBanner() {
        System.out.println("+======================================================================+");
        System.out.println("|            SMART PARKING LOT MANAGEMENT SYSTEM (v2.0)                |");
        System.out.println("|        Robust Console Application - Java 17 - Pure OOP Design        |");
        System.out.println("+======================================================================+");
    }

    private static void displayMenu() {
        System.out.println("+-------------------------- MAIN MENU ---------------------------------+");
        System.out.println("|  1. Park Vehicle                                                     |");
        System.out.println("|  2. Remove Vehicle                                                   |");
        System.out.println("|  3. Display Parking Slots                                            |");
        System.out.println("|  4. Search Vehicle                                                   |");
        System.out.println("|  5. Display Parked Vehicles                                          |");
        System.out.println("|  6. Parking Statistics                                               |");
        System.out.println("|  7. Parking Fee Rules                                                |");
        System.out.println("|  8. Exit                                                             |");
        System.out.println("+----------------------------------------------------------------------+");
        System.out.print(">> Enter your choice (1-8): ");
    }

    /**
     * Reads a valid menu choice from console, recovering gracefully from invalid characters.
     */
    private static int readMenuChoice(BufferedReader reader) throws InputAbortedException {
        while (true) {
            String input = readRawLine(reader);
            if (input == null) {
                throw new InputAbortedException();
            }
            input = input.trim();
            if (input.isEmpty()) {
                System.out.print("[!] Choice cannot be empty. Please enter a number from 1 to 8: ");
                continue;
            }
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 8) {
                    return choice;
                }
                System.out.print("[!] Invalid choice! Please enter a number from 1 to 8: ");
            } catch (NumberFormatException e) {
                System.out.print("[!] Input must be an integer. Please enter a number (1-8): ");
            }
        }
    }

    /**
     * Handles parking a new vehicle into an appropriate slot.
     */
    private static void handleParkVehicle(ParkingLot parkingLot, BufferedReader reader) throws InputAbortedException {
        System.out.println("------------- [1. PARK A NEW VEHICLE] -------------");

        VehicleType type = readVehicleType(reader);
        String licensePlate = readValidLicensePlate(reader);

        if (parkingLot.isVehicleParked(licensePlate)) {
            System.out.printf("[ERROR] Vehicle with registration '%s' is already parked in the lot!%n", licensePlate);
            return;
        }

        String ownerName = readValidOwnerName(reader);

        Vehicle vehicle;
        LocalDateTime entryTime = LocalDateTime.now();

        if (type == VehicleType.CAR) {
            System.out.print("Enter Car Body Type (e.g., Sedan, SUV, Hatchback, EV) [Default: Sedan]: ");
            String carModel = readOptionalLine(reader, "Sedan");
            vehicle = new Car(licensePlate, ownerName, entryTime, carModel);
        } else {
            System.out.print("Enter Bike Type (e.g., Scooter, Cruiser, Sports) [Default: Scooter]: ");
            String bikeModel = readOptionalLine(reader, "Scooter");
            vehicle = new Bike(licensePlate, ownerName, entryTime, bikeModel);
        }

        try {
            ParkingSlot allocatedSlot = parkingLot.parkVehicle(vehicle);
            System.out.println("\n[SUCCESS] Vehicle has been successfully parked.");
            System.out.println("+---------------- PARKING TICKET ----------------+");
            System.out.printf("| Assigned Slot : %-30s |%n", allocatedSlot.getSlotId());
            System.out.printf("| Vehicle Reg   : %-30s |%n", vehicle.getLicensePlate());
            System.out.printf("| Vehicle Type  : %-30s |%n", vehicle.getVehicleType().getDisplayName());
            System.out.printf("| Owner Name    : %-30s |%n", vehicle.getOwnerName());
            System.out.printf("| Entry Time    : %-30s |%n", vehicle.getFormattedEntryTime());
            System.out.printf("| Hourly Rate   : \u20B9%-23.2f (INR) |%n", vehicle.getHourlyRate());
            System.out.println("+------------------------------------------------+");
        } catch (DuplicateVehicleException e) {
            System.out.printf("[FAILED] Parking Failed: %s%n", e.getMessage());
        } catch (ParkingLotFullException e) {
            System.out.printf("[FULL] Parking Lot Full: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("[ERROR] Unexpected error while parking: %s%n", e.getMessage());
        }
    }

    /**
     * Handles removing an active vehicle, releasing its slot, and printing its billing receipt in INR.
     */
    private static void handleRemoveVehicle(ParkingLot parkingLot, BufferedReader reader) throws InputAbortedException {
        System.out.println("------------- [2. REMOVE VEHICLE & CHECKOUT] -------------");

        if (parkingLot.getParkedVehicles().isEmpty()) {
            System.out.println("[INFO] The parking lot is currently empty. No vehicles to remove.");
            return;
        }

        String licensePlate = readLookupLicensePlate(reader);
        if (licensePlate.isEmpty()) {
            System.out.println("[ERROR] Registration number cannot be blank. Returning to Main Menu.");
            return;
        }

        Vehicle vehicle;
        ParkingSlot slot;
        try {
            vehicle = parkingLot.searchVehicle(licensePlate);
            slot = parkingLot.getSlotForVehicle(licensePlate);
        } catch (VehicleNotFoundException | IllegalArgumentException e) {
            System.out.printf("[ERROR] %s%n", e.getMessage());
            return;
        }

        System.out.println("\n[PARKED VEHICLE DETAILS]");
        System.out.printf("  Registration No : %s%n", vehicle.getLicensePlate());
        System.out.printf("  Owner Name      : %s%n", vehicle.getOwnerName());
        System.out.printf("  Vehicle Type    : %s%n", vehicle.getVehicleType().getDisplayName());
        System.out.printf("  Current Slot    : %s%n", slot.getSlotId());
        System.out.printf("  Entry Timestamp : %s%n", vehicle.getFormattedEntryTime());
        System.out.printf("  Hourly Tariff   : \u20B9%.2f / hour (INR)%n", vehicle.getHourlyRate());

        System.out.println("\nChoose Checkout Duration Mode:");
        System.out.println("  1. Instant Checkout (Use current real-time clock)");
        System.out.println("  2. Simulated Duration (Specify hours & minutes to test billing calculation)");
        System.out.print(">> Select mode (1 or 2): ");
        int mode = readIntRange(reader, 1, 2);

        LocalDateTime exitTime;
        if (mode == 1) {
            exitTime = LocalDateTime.now();
        } else {
            System.out.print("Enter simulated hours elapsed (e.g., 0, 1, 2, 5): ");
            int hours = readIntRange(reader, 0, 500);
            System.out.print("Enter simulated additional minutes elapsed (0-59): ");
            int minutes = readIntRange(reader, 0, 59);
            exitTime = vehicle.getEntryTime().plusHours(hours).plusMinutes(minutes);
        }

        try {
            ParkingReceipt receipt = parkingLot.removeVehicle(licensePlate, exitTime);
            System.out.printf("%n[SUCCESS] Vehicle checked out successfully! Slot %s is now RELEASED and available for reuse.%n", receipt.getSlotId());
            System.out.printf("Final Parking Fee Calculated: \u20B9%.2f (INR)%n%n", receipt.getTotalFee());
            System.out.print(receipt.toFormattedReceipt());
        } catch (VehicleNotFoundException e) {
            System.out.printf("[FAILED] Removal Failed: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("[ERROR] Unexpected error during checkout: %s%n", e.getMessage());
        }
    }

    /**
     * Displays all parking slots in an organized tabular format including vehicle type.
     */
    private static void handleDisplayParkingSlots(ParkingLot parkingLot) {
        System.out.println("------------- [3. PARKING SLOTS LAYOUT] -------------");
        List<ParkingSlot> slots = parkingLot.getAllSlots();

        System.out.println("+---------+--------------+--------------+------------------+------------------+----------------------+");
        System.out.println("| Slot ID | Slot Type    | Status       | Reg Number       | Vehicle Type     | Owner Name           |");
        System.out.println("+---------+--------------+--------------+------------------+------------------+----------------------+");

        for (ParkingSlot slot : slots) {
            String status = slot.isOccupied() ? "OCCUPIED" : "AVAILABLE";
            String reg = slot.isOccupied() ? slot.getParkedVehicle().getLicensePlate() : "-";
            String vehType = slot.isOccupied() ? slot.getParkedVehicle().getVehicleType().getDisplayName() : "-";
            String owner = slot.isOccupied() ? slot.getParkedVehicle().getOwnerName() : "-";
            if (owner.length() > 20) owner = owner.substring(0, 17) + "...";

            System.out.printf("| %-7s | %-12s | %-12s | %-16s | %-16s | %-20s |%n",
                    slot.getSlotId(),
                    slot.getSupportedType().getDisplayName(),
                    status,
                    reg,
                    vehType,
                    owner);
        }

        System.out.println("+---------+--------------+--------------+------------------+------------------+----------------------+");
        ParkingStatistics stats = parkingLot.getStatistics();
        System.out.printf("Summary: Available: %d / %d slots (Cars: %d available, Bikes: %d available)%n",
                stats.getTotalAvailable(), stats.getTotalCapacity(),
                stats.getCarAvailable(), stats.getBikeAvailable());
    }

    /**
     * Searches for a vehicle by registration number and displays its details.
     */
    private static void handleSearchVehicle(ParkingLot parkingLot, BufferedReader reader) throws InputAbortedException {
        System.out.println("------------- [4. SEARCH VEHICLE] -------------");
        String licensePlate = readLookupLicensePlate(reader);

        if (licensePlate.isEmpty()) {
            System.out.println("[NOT FOUND] Registration number cannot be blank. Returning to Main Menu.");
            return;
        }

        try {
            Vehicle vehicle = parkingLot.searchVehicle(licensePlate);
            ParkingSlot slot = parkingLot.getSlotForVehicle(licensePlate);

            System.out.println("\n[SEARCH RESULT] Vehicle Found:");
            System.out.println("+====================================================+");
            System.out.printf("| Slot Number   : %-34s |%n", slot.getSlotId());
            System.out.printf("| Reg Number    : %-34s |%n", vehicle.getLicensePlate());
            System.out.printf("| Vehicle Type  : %-34s |%n", vehicle.getVehicleType().getDisplayName());
            System.out.printf("| Owner Name    : %-34s |%n", vehicle.getOwnerName());
            System.out.printf("| Entry Time    : %-34s |%n", vehicle.getFormattedEntryTime());
            System.out.printf("| Specifics     : %-34s |%n", vehicle.getSpecificDetails());
            System.out.println("+====================================================+");
        } catch (VehicleNotFoundException | IllegalArgumentException e) {
            System.out.printf("[NOT FOUND] Search Result: %s%n", e.getMessage());
        }
    }

    /**
     * Displays all vehicles currently parked in the lot.
     */
    private static void handleDisplayParkedVehicles(ParkingLot parkingLot) {
        System.out.println("------------- [5. CURRENTLY PARKED VEHICLES] -------------");
        Collection<Vehicle> vehicles = parkingLot.getParkedVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("[INFO] No vehicles are currently parked in the lot.");
            return;
        }

        System.out.println("+--------------+--------------+------------------+----------------------+---------------------+");
        System.out.println("| Reg Number   | Type         | Slot ID          | Owner Name           | Entry Timestamp     |");
        System.out.println("+--------------+--------------+------------------+----------------------+---------------------+");

        for (Vehicle v : vehicles) {
            String slotId = "UNKNOWN";
            try {
                slotId = parkingLot.getSlotForVehicle(v.getLicensePlate()).getSlotId();
            } catch (VehicleNotFoundException ignored) {}

            String owner = v.getOwnerName();
            if (owner.length() > 20) owner = owner.substring(0, 17) + "...";

            System.out.printf("| %-12s | %-12s | %-16s | %-20s | %-19s |%n",
                    v.getLicensePlate(),
                    v.getVehicleType().getDisplayName(),
                    slotId,
                    owner,
                    v.getFormattedEntryTime());
        }
        System.out.println("+--------------+--------------+------------------+----------------------+---------------------+");
        System.out.printf("Total Active Vehicles: %d%n", vehicles.size());
    }

    /**
     * Displays real-time operational and financial statistics.
     */
    private static void handleParkingStatistics(ParkingLot parkingLot) {
        System.out.println("------------- [6. PARKING LOT STATISTICS] -------------");
        ParkingStatistics stats = parkingLot.getStatistics();
        System.out.print(stats.toFormattedSummary());
    }

    /**
     * Displays parking fee calculation rules and tariff table.
     */
    private static void handleParkingFeeRules(ParkingLot parkingLot) {
        System.out.println("------------- [7. PARKING FEE RULES & TARIFF] -------------");
        System.out.print(parkingLot.getFeeCalculator().getFeeRulesSummary());
    }

    /**
     * Confirms whether user really wants to exit the application.
     */
    private static boolean confirmExit(BufferedReader reader) {
        while (true) {
            System.out.print("Are you sure you want to exit the system? (Y/N): ");
            String ans;
            try {
                ans = readRawLine(reader);
            } catch (InputAbortedException e) {
                return true;
            }
            if (ans == null) {
                return true;
            }
            ans = ans.trim().toUpperCase();
            if (ans.equals("Y") || ans.equals("YES")) {
                return true;
            } else if (ans.equals("N") || ans.equals("NO")) {
                System.out.println("[INFO] Exit cancelled. Returning to Main Menu.");
                return false;
            } else {
                System.out.println("[!] Please enter 'Y' for Yes or 'N' for No.");
            }
        }
    }

    /**
     * Handles graceful exit.
     */
    private static void handleExit(ParkingLot parkingLot) {
        System.out.println("========================================================================");
        System.out.println("  Thank you for using the Smart Parking Lot Management System!");
        System.out.printf("  Final Session Summary: %d checkouts served, \u20B9%.2f (INR) collected.%n",
                parkingLot.getStatistics().getTotalVehiclesServed(),
                parkingLot.getStatistics().getTotalRevenue());
        System.out.println("========================================================================");
    }

    // =========================================================================
    // Safe Input Helper Methods with Robust Validation & Graceful Recovery
    // =========================================================================

    private static String readRawLine(BufferedReader reader) throws InputAbortedException {
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new InputAbortedException();
            }
            return line;
        } catch (IOException e) {
            throw new InputAbortedException();
        }
    }

    /**
     * Optional field: blank uses the provided default. EOF aborts the current operation.
     */
    private static String readOptionalLine(BufferedReader reader, String defaultValue) throws InputAbortedException {
        String input = readRawLine(reader).trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Lookup plate (search/remove): trim + uppercase, allow blank so the caller can return to the menu.
     */
    private static String readLookupLicensePlate(BufferedReader reader) throws InputAbortedException {
        System.out.print("Enter Vehicle Registration Number (e.g., KA-01-AB-1234 or DL01C1234): ");
        return readRawLine(reader).trim().toUpperCase();
    }

    /**
     * Validates and reads vehicle type from user with recovery on invalid input.
     */
    private static VehicleType readVehicleType(BufferedReader reader) throws InputAbortedException {
        while (true) {
            System.out.print("Select Vehicle Type [1. Car, 2. Bike] (or type CAR / BIKE): ");
            String input = readRawLine(reader).trim();
            if (input.isEmpty()) {
                System.out.println("[!] Vehicle type cannot be empty. Please enter 1, 2, CAR, or BIKE.");
                continue;
            }
            try {
                return VehicleType.fromString(input);
            } catch (InvalidVehicleTypeException e) {
                System.out.printf("[!] %s Please enter 1, 2, CAR, or BIKE.%n", e.getMessage());
            }
        }
    }

    /**
     * Reads and validates a vehicle registration plate number (alphanumeric with hyphens/spaces).
     */
    private static String readValidLicensePlate(BufferedReader reader) throws InputAbortedException {
        while (true) {
            System.out.print("Enter Vehicle Registration Number (e.g., KA-01-AB-1234 or DL01C1234): ");
            String input = readRawLine(reader).trim().toUpperCase();

            if (input.isEmpty()) {
                System.out.println("[!] Registration number cannot be empty. Please re-enter.");
                continue;
            }

            if (REG_PLATE_PATTERN.matcher(input).matches()) {
                return input;
            }

            System.out.println("[!] Invalid format! Registration number must be 4 to 15 alphanumeric characters (letters, numbers, spaces, or hyphens).");
        }
    }

    /**
     * Reads and validates an owner/driver name.
     */
    private static String readValidOwnerName(BufferedReader reader) throws InputAbortedException {
        while (true) {
            System.out.print("Enter Vehicle Owner Name: ");
            String input = readRawLine(reader).trim();

            if (input.isEmpty()) {
                System.out.println("[!] Owner name cannot be empty. Please enter a valid name.");
                continue;
            }

            if (NAME_PATTERN.matcher(input).matches()) {
                return input;
            }

            System.out.println("[!] Invalid name! Please use only letters and spaces (between 2 and 40 characters).");
        }
    }

    /**
     * Reads an integer within a specified range [min, max], recovering on bad input.
     */
    private static int readIntRange(BufferedReader reader, int min, int max) throws InputAbortedException {
        while (true) {
            String input = readRawLine(reader).trim();
            if (input.isEmpty()) {
                System.out.printf("[!] Value cannot be empty. Please enter a number between %d and %d: ", min, max);
                continue;
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("[!] Out of bounds! Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("[!] Invalid number! Please enter an integer between %d and %d: ", min, max);
            }
        }
    }
}
