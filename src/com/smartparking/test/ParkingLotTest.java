package com.smartparking.test;

import com.smartparking.Main;
import com.smartparking.exception.DuplicateVehicleException;
import com.smartparking.exception.InvalidVehicleTypeException;
import com.smartparking.exception.ParkingLotFullException;
import com.smartparking.exception.VehicleNotFoundException;
import com.smartparking.model.*;
import com.smartparking.service.ParkingFeeCalculator;
import com.smartparking.service.ParkingLot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Lightweight, zero-dependency Automated Test Suite for Smart Parking Lot.
 * Executes automated test cases covering:
 *  - Normal parking operations (Car & Bike)
 *  - Duplicate vehicle rejection
 *  - Lot full handling
 *  - Search vehicle (hit & miss)
 *  - Removal and slot vacancy
 *  - Round-up ceiling billing calculations
 *  - Internal collection consistency (ArrayList, HashMap, HashSet)
 *  - Invalid vehicle type parsing
 *  - OOP Polymorphic rate dispatch
 */
public class ParkingLotTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   RUNNING AUTOMATED UNIT & INTEGRATION TESTS (JAVA 17 LTS)    ");
        System.out.println("================================================================\n");

        runTest("1. Car Parking & Slot Allocation", ParkingLotTest::testCarParkingAllocation);
        runTest("2. Bike Parking & Slot Allocation", ParkingLotTest::testBikeParkingAllocation);
        runTest("3. Duplicate Vehicle Prevention (DuplicateVehicleException)", ParkingLotTest::testDuplicateVehiclePrevention);
        runTest("4. Parking Lot Full Handling (ParkingLotFullException)", ParkingLotTest::testParkingLotFullHandling);
        runTest("5. Search Vehicle Success & VehicleNotFoundException", ParkingLotTest::testSearchVehicle);
        runTest("6. Vehicle Removal & Slot Vacating", ParkingLotTest::testVehicleRemovalAndVacating);
        runTest("7. Round-Up To Next Hour Ceiling Billing", ParkingLotTest::testRoundUpCeilingBilling);
        runTest("8. Minimum 1-Hour Billing Rule", ParkingLotTest::testMinimumOneHourBilling);
        runTest("9. Collection Consistency Synchronization Check", ParkingLotTest::testCollectionConsistency);
        runTest("10. Invalid Vehicle Type Validation", ParkingLotTest::testInvalidVehicleType);
        runTest("11. Polymorphic Hourly Rates Verification", ParkingLotTest::testPolymorphicRates);
        runTest("12. Slot Reuse After Vehicle Departure", ParkingLotTest::testSlotReuseAfterRemoval);
        runTest("13. Case-Insensitive Search & Removal with Trimming", ParkingLotTest::testCaseInsensitiveSearchAndRemoval);
        runTest("14. Invalid Input Rejection (Null & Empty Values)", ParkingLotTest::testInvalidInputRejection);
        runTest("15. Parking Statistics Accuracy & Revenue Tracking", ParkingLotTest::testParkingStatisticsAccuracyAndRevenue);
        runTest("16. Negative & Inverted Duration Handling", ParkingLotTest::testNegativeDurationGracefulHandling);
        runTest("17. Menu Options 1-8 End-to-End Script (Return to Menu)", ParkingLotTest::testMenuOptionsEndToEnd);
        runTest("18. Invalid / Non-Numeric Menu Input Recovery", ParkingLotTest::testInvalidNonNumericMenuInput);
        runTest("19. Option 7 Tariff Output in INR", ParkingLotTest::testOption7TariffOutputInInr);
        runTest("20. Option 8 Exit Confirmation Y/N", ParkingLotTest::testOption8ExitAndCancellation);
        runTest("21. Search Blank Input Does Not Crash", ParkingLotTest::testSearchBlankInputDoesNotCrash);
        runTest("22. Menu Duplicate Registration And Full Capacity", ParkingLotTest::testMenuDuplicateAndFullCapacity);

        System.out.println("\n================================================================");
        System.out.printf("   TEST EXECUTION SUMMARY: %d Run | %d Passed | %d Failed%n", testsRun, testsPassed, testsFailed);
        System.out.println("================================================================");

        if (testsFailed > 0) {
            System.err.println("[FAILED] Some tests failed! Please check logs above.");
            System.exit(1);
        } else {
            System.out.println("[SUCCESS] ALL AUTOMATED TESTS PASSED SUCCESSFULLY (100% PASS RATE)!");
        }
    }

    private static void runTest(String testName, RunnableWithException testRunnable) {
        testsRun++;
        try {
            testRunnable.run();
            testsPassed++;
            System.out.printf("  [PASS] %s%n", testName);
        } catch (Throwable t) {
            testsFailed++;
            System.out.printf("  [FAIL] %s - Cause: %s%n", testName, t.getMessage());
            t.printStackTrace(System.out);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion Failed: " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("Assertion Failed [%s]: Expected <%s> but got <%s>", message, expected, actual));
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(String.format("Assertion Failed [%s]: Expected <%.2f> but got <%.2f>", message, expected, actual));
        }
    }

    // -------------------------------------------------------------
    // Test Implementations
    // -------------------------------------------------------------

    private static void testCarParkingAllocation() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        Car car = new Car("KA-01-AB-1234", "Alice Smith", LocalDateTime.now(), "Sedan");

        ParkingSlot slot = lot.parkVehicle(car);
        assertTrue(slot != null, "Allocated slot should not be null");
        assertEquals("C-01", slot.getSlotId(), "Car should be assigned to first car slot C-01");
        assertTrue(slot.isOccupied(), "Slot must be marked occupied");
        assertEquals(car, slot.getParkedVehicle(), "Slot must hold the car");
        assertTrue(lot.isVehicleParked("KA-01-AB-1234"), "Vehicle must be registered as parked");
    }

    private static void testBikeParkingAllocation() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        Bike bike = new Bike("KA-05-XY-9999", "Bob Miller", LocalDateTime.now(), "Sports");

        ParkingSlot slot = lot.parkVehicle(bike);
        assertTrue(slot != null, "Allocated slot should not be null");
        assertEquals("B-01", slot.getSlotId(), "Bike should be assigned to first bike slot B-01");
        assertTrue(slot.isOccupied(), "Slot must be marked occupied");
        assertEquals(bike, slot.getParkedVehicle(), "Slot must hold the bike");
        assertTrue(lot.isVehicleParked("KA-05-XY-9999"), "Bike must be registered as parked");
    }

    private static void testDuplicateVehiclePrevention() {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        Car car1 = new Car("DL-01-AA-1111", "Charlie", LocalDateTime.now(), "Hatchback");
        Car car2 = new Car("DL-01-AA-1111", "Charlie Duplicate", LocalDateTime.now(), "Sedan");

        try {
            lot.parkVehicle(car1);
            lot.parkVehicle(car2);
            throw new AssertionError("Expected DuplicateVehicleException was not thrown!");
        } catch (DuplicateVehicleException e) {
            assertTrue(e.getMessage().contains("ALREADY PARKED"), "Exception message should indicate vehicle is already parked");
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception thrown: " + e.getClass().getSimpleName());
        }
    }

    private static void testParkingLotFullHandling() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 1, 1, new ParkingFeeCalculator());
        Car car1 = new Car("MH-12-QQ-1001", "David", LocalDateTime.now(), "SUV");
        Car car2 = new Car("MH-12-QQ-1002", "Elena", LocalDateTime.now(), "Sedan");

        lot.parkVehicle(car1);

        try {
            lot.parkVehicle(car2);
            throw new AssertionError("Expected ParkingLotFullException was not thrown!");
        } catch (ParkingLotFullException e) {
            assertTrue(e.getMessage().contains("FULL"), "Exception message should mention lot is full");
        }
    }

    private static void testSearchVehicle() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        Car car = new Car("TS-09-ZZ-5555", "Franklin", LocalDateTime.now(), "EV");
        lot.parkVehicle(car);

        // Search hit
        Vehicle found = lot.searchVehicle("TS-09-ZZ-5555");
        assertEquals(car, found, "Searched vehicle must match parked vehicle");

        // Search miss
        try {
            lot.searchVehicle("UNKNOWN-999");
            throw new AssertionError("Expected VehicleNotFoundException was not thrown!");
        } catch (VehicleNotFoundException e) {
            assertTrue(e.getMessage().contains("No active vehicle found"), "Exception message should describe missing vehicle");
        }
    }

    private static void testVehicleRemovalAndVacating() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        LocalDateTime entryTime = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime exitTime = LocalDateTime.of(2026, 1, 1, 12, 30); // 2.5 hours -> 3 billable hours

        Car car = new Car("TN-02-MM-4444", "Grace", entryTime, "Sedan");
        ParkingSlot slot = lot.parkVehicle(car);

        ParkingReceipt receipt = lot.removeVehicle("TN-02-MM-4444", exitTime);

        // Check slot freed
        assertTrue(slot.isAvailable(), "Slot must be available after vehicle removal");
        assertTrue(!lot.isVehicleParked("TN-02-MM-4444"), "Vehicle must no longer be registered as parked");

        // Check receipt metrics (2.5 hours = 150 mins -> 3 billable hours @ INR 40 = INR 120)
        assertEquals(150L, receipt.getTotalMinutes(), "Total duration should be 150 minutes");
        assertEquals(3L, receipt.getBillableHours(), "Billable hours must round up to 3");
        assertEquals(120.0, receipt.getTotalFee(), 0.001, "Total fee should be 3 * \u20B940 = \u20B9120.00 (INR)");
    }

    private static void testRoundUpCeilingBilling() {
        ParkingFeeCalculator calc = new ParkingFeeCalculator(40.0, 20.0);

        // 15 minutes -> 1 hour
        assertEquals(1L, calc.calculateBillableHours(15), "15 mins should be 1 billable hour");

        // 60 minutes -> 1 hour
        assertEquals(1L, calc.calculateBillableHours(60), "60 mins should be 1 billable hour");

        // 61 minutes -> 2 hours
        assertEquals(2L, calc.calculateBillableHours(61), "61 mins should round up to 2 billable hours");

        // 120 minutes -> 2 hours
        assertEquals(2L, calc.calculateBillableHours(120), "120 mins should be 2 billable hours");

        // 121 minutes -> 3 hours
        assertEquals(3L, calc.calculateBillableHours(121), "121 mins should round up to 3 billable hours");
    }

    private static void testMinimumOneHourBilling() {
        ParkingFeeCalculator calc = new ParkingFeeCalculator(40.0, 20.0);
        LocalDateTime now = LocalDateTime.now();
        Car car = new Car("TEST-CAR-1", "Test", now, "Sedan");

        // Instant checkout (same minute)
        double fee = calc.calculateFee(car, now, now);
        assertEquals(40.0, fee, 0.001, "Immediate checkout must bill minimum 1 hour (\u20B940.00 INR)");
    }

    private static void testCollectionConsistency() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 3, 3, new ParkingFeeCalculator());

        // Step 1: Initial empty state
        assertTrue(lot.isCollectionConsistencyValid(), "Empty lot must be collection-consistent");

        // Step 2: Park 3 vehicles
        Car c1 = new Car("REG-01", "User1", LocalDateTime.now(), "Sedan");
        Car c2 = new Car("REG-02", "User2", LocalDateTime.now(), "SUV");
        Bike b1 = new Bike("REG-03", "User3", LocalDateTime.now(), "Scooter");

        lot.parkVehicle(c1);
        assertTrue(lot.isCollectionConsistencyValid(), "Lot must remain consistent after park 1");
        lot.parkVehicle(c2);
        assertTrue(lot.isCollectionConsistencyValid(), "Lot must remain consistent after park 2");
        lot.parkVehicle(b1);
        assertTrue(lot.isCollectionConsistencyValid(), "Lot must remain consistent after park 3");

        // Step 3: Remove 1 vehicle
        lot.removeVehicle("REG-02", LocalDateTime.now().plusHours(1));
        assertTrue(lot.isCollectionConsistencyValid(), "Lot must remain consistent after unpark");

        // Step 4: Park another vehicle into freed slot
        Car c3 = new Car("REG-04", "User4", LocalDateTime.now(), "EV");
        lot.parkVehicle(c3);
        assertTrue(lot.isCollectionConsistencyValid(), "Lot must remain consistent after re-parking");

        // Step 5: Remove remaining vehicles
        lot.removeVehicle("REG-01", LocalDateTime.now().plusHours(1));
        lot.removeVehicle("REG-03", LocalDateTime.now().plusHours(1));
        lot.removeVehicle("REG-04", LocalDateTime.now().plusHours(1));
        assertTrue(lot.isCollectionConsistencyValid(), "Lot must be consistent after all removals");
        assertEquals(0, lot.getParkedVehicles().size(), "Active vehicles should be 0");
    }

    private static void testInvalidVehicleType() {
        try {
            VehicleType.fromString("TRUCK");
            throw new AssertionError("Expected InvalidVehicleTypeException was not thrown!");
        } catch (InvalidVehicleTypeException e) {
            assertTrue(e.getMessage().contains("Invalid vehicle type"), "Exception should note invalid vehicle type");
        }
    }

    private static void testPolymorphicRates() {
        LocalDateTime now = LocalDateTime.now();
        Vehicle carRef = new Car("CAR-1", "Owner A", now, "SUV");
        Vehicle bikeRef = new Bike("BIKE-1", "Owner B", now, "Scooter");

        // Polymorphic dynamic method dispatch
        assertEquals(40.0, carRef.getHourlyRate(), 0.001, "Car polymorphic rate should be \u20B940.00 (INR)");
        assertEquals(20.0, bikeRef.getHourlyRate(), 0.001, "Bike polymorphic rate should be \u20B920.00 (INR)");
        assertEquals(VehicleType.CAR, carRef.getVehicleType(), "Car polymorphic type should be CAR");
        assertEquals(VehicleType.BIKE, bikeRef.getVehicleType(), "Bike polymorphic type should be BIKE");
    }

    private static void testSlotReuseAfterRemoval() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        Car car1 = new Car("KA-01-A-1001", "Driver One", LocalDateTime.now(), "Sedan");
        Car car2 = new Car("KA-01-B-2002", "Driver Two", LocalDateTime.now(), "SUV");

        // Park car1 into C-01
        ParkingSlot slot1 = lot.parkVehicle(car1);
        assertEquals("C-01", slot1.getSlotId(), "Car 1 should occupy C-01");

        // Remove car1 -> slot C-01 is vacated
        lot.removeVehicle("KA-01-A-1001", LocalDateTime.now().plusHours(1));
        assertTrue(slot1.isAvailable(), "Slot C-01 must be available after removal");

        // Park car2 -> slot C-01 should be reused!
        ParkingSlot slot2 = lot.parkVehicle(car2);
        assertEquals("C-01", slot2.getSlotId(), "Vacated slot C-01 should be reused for Car 2");
        assertEquals(car2, slot2.getParkedVehicle(), "Car 2 must be parked in C-01");

        // Similar test for Bike slot B-01 reuse
        Bike bike1 = new Bike("DL-01-B-5001", "Rider One", LocalDateTime.now(), "Scooter");
        Bike bike2 = new Bike("DL-01-B-6002", "Rider Two", LocalDateTime.now(), "Cruiser");

        ParkingSlot bikeSlot1 = lot.parkVehicle(bike1);
        assertEquals("B-01", bikeSlot1.getSlotId(), "Bike 1 should occupy B-01");

        lot.removeVehicle("DL-01-B-5001", LocalDateTime.now().plusHours(1));
        assertTrue(bikeSlot1.isAvailable(), "Slot B-01 must be available after removal");

        ParkingSlot bikeSlot2 = lot.parkVehicle(bike2);
        assertEquals("B-01", bikeSlot2.getSlotId(), "Vacated slot B-01 should be reused for Bike 2");
    }

    private static void testCaseInsensitiveSearchAndRemoval() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        Car car = new Car("MH-04-AZ-7777", "Rohan", LocalDateTime.now(), "Sedan");
        lot.parkVehicle(car);

        // Search with lowercase and extra leading/trailing whitespace
        Vehicle found = lot.searchVehicle("   mh-04-az-7777   ");
        assertEquals(car, found, "Search must be case-insensitive and trim spaces");
        assertTrue(lot.isVehicleParked("  mh-04-az-7777 "), "isVehicleParked must be case-insensitive");

        // Remove with mixed case and whitespace
        ParkingReceipt receipt = lot.removeVehicle("  Mh-04-Az-7777  ", LocalDateTime.now().plusHours(1));
        assertTrue(receipt != null, "Vehicle should be successfully removed with mixed case and whitespace");
        assertTrue(!lot.isVehicleParked("MH-04-AZ-7777"), "Vehicle must be removed from lot");
    }

    private static void testInvalidInputRejection() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Vehicle with empty license plate
        try {
            new Car("   ", "Alice", now, "Sedan");
            throw new AssertionError("Expected IllegalArgumentException for blank registration number!");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Registration number"), "Message should mention registration number");
        }

        // 2. Vehicle with empty owner name
        try {
            new Bike("KA-01-AB-1234", "   ", now, "Scooter");
            throw new AssertionError("Expected IllegalArgumentException for blank owner name!");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Owner name"), "Message should mention owner name");
        }

        // 3. Search with blank registration plate
        ParkingLot lot = new ParkingLot("Test Lot", 2, 2, new ParkingFeeCalculator());
        try {
            lot.searchVehicle("   ");
            throw new AssertionError("Expected IllegalArgumentException for blank search!");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Registration number"), "Message should mention registration number");
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception type: " + e.getClass().getSimpleName());
        }
    }

    private static void testParkingStatisticsAccuracyAndRevenue() throws Exception {
        ParkingLot lot = new ParkingLot("Test Lot", 3, 3, new ParkingFeeCalculator(40.0, 20.0));

        // Initial state
        ParkingStatistics initialStats = lot.getStatistics();
        assertEquals(0, initialStats.getTotalOccupied(), "Initially occupied should be 0");
        assertEquals(0.0, initialStats.getTotalRevenue(), 0.001, "Initially revenue should be 0.0");
        assertEquals(0, initialStats.getTotalVehiclesServed(), "Initially checkouts served should be 0");

        // Park 2 cars and 1 bike
        LocalDateTime entryTime = LocalDateTime.of(2026, 1, 1, 10, 0);
        Car car1 = new Car("CAR-101", "User A", entryTime, "Sedan");
        Car car2 = new Car("CAR-102", "User B", entryTime, "SUV");
        Bike bike1 = new Bike("BIKE-101", "User C", entryTime, "Scooter");

        lot.parkVehicle(car1);
        lot.parkVehicle(car2);
        lot.parkVehicle(bike1);

        ParkingStatistics parkedStats = lot.getStatistics();
        assertEquals(3, parkedStats.getTotalOccupied(), "Occupied slots should be 3");
        assertEquals(2, parkedStats.getParkedCarCount(), "Parked cars count should be 2");
        assertEquals(1, parkedStats.getParkedBikeCount(), "Parked bikes count should be 1");
        assertEquals(0.0, parkedStats.getTotalRevenue(), 0.001, "Revenue must remain 0.0 while vehicles are still parked");

        // Checkout Car 1 after 2 hours (2 hours * ₹40 = ₹80.00)
        LocalDateTime exitTime1 = entryTime.plusHours(2);
        lot.removeVehicle("CAR-101", exitTime1);

        ParkingStatistics checkout1Stats = lot.getStatistics();
        assertEquals(2, checkout1Stats.getTotalOccupied(), "Occupied slots should be 2");
        assertEquals(1, checkout1Stats.getParkedCarCount(), "Parked cars should now be 1");
        assertEquals(1, checkout1Stats.getParkedBikeCount(), "Parked bikes should still be 1");
        assertEquals(1, checkout1Stats.getTotalVehiclesServed(), "Vehicles served should be 1");
        assertEquals(80.0, checkout1Stats.getTotalRevenue(), 0.001, "Total collected revenue should be \u20B980.00");

        // Checkout Bike 1 after 1 hour (1 hour * ₹20 = ₹20.00)
        LocalDateTime exitTime2 = entryTime.plusHours(1);
        lot.removeVehicle("BIKE-101", exitTime2);

        ParkingStatistics checkout2Stats = lot.getStatistics();
        assertEquals(1, checkout2Stats.getTotalOccupied(), "Occupied slots should be 1");
        assertEquals(1, checkout2Stats.getParkedCarCount(), "Parked cars should be 1");
        assertEquals(0, checkout2Stats.getParkedBikeCount(), "Parked bikes should be 0");
        assertEquals(2, checkout2Stats.getTotalVehiclesServed(), "Vehicles served should be 2");
        assertEquals(100.0, checkout2Stats.getTotalRevenue(), 0.001, "Total collected revenue should be \u20B980 + \u20B920 = \u20B9100.00");
    }

    private static void testNegativeDurationGracefulHandling() {
        ParkingFeeCalculator calc = new ParkingFeeCalculator(40.0, 20.0);
        LocalDateTime entryTime = LocalDateTime.of(2026, 1, 1, 14, 0);
        LocalDateTime invertedExitTime = LocalDateTime.of(2026, 1, 1, 12, 0); // 2 hours before entry!

        long minutes = calc.calculateDurationMinutes(entryTime, invertedExitTime);
        assertEquals(1L, minutes, "Inverted exit timestamp should be safely clamped to minimum 1 minute");

        Car car = new Car("TEST-CAR", "Driver", entryTime, "Sedan");
        double fee = calc.calculateFee(car, entryTime, invertedExitTime);
        assertEquals(40.0, fee, 0.001, "Inverted duration fee should safely bill minimum 1 hour (\u20B940.00)");
    }

    private static void testMenuOptionsEndToEnd() {
        String script = String.join("\n",
                "1",
                "1",
                "KA-01-AB-1234",
                "Alice Smith",
                "Sedan",
                "1",
                "2",
                "MH-12-CD-5678",
                "Bob Miller",
                "Scooter",
                "3",
                "4",
                "  ka-01-ab-1234  ",
                "5",
                "6",
                "7",
                "2",
                "KA-01-AB-1234",
                "2",
                "2",
                "15",
                "8",
                "Y"
        ) + "\n";

        String output = runMenuScript(new ParkingLot("Menu Lot", 5, 5, new ParkingFeeCalculator()), script);

        assertTrue(output.contains("PARK A NEW VEHICLE"), "Option 1 should run park flow");
        assertTrue(output.contains("[SUCCESS] Vehicle has been successfully parked."), "Car parking should succeed");
        assertTrue(output.contains("C-01"), "Car should be assigned slot C-01");
        assertTrue(output.contains("B-01"), "Bike should be assigned slot B-01");
        assertTrue(output.contains("PARKING SLOTS LAYOUT"), "Option 3 should display slots");
        assertTrue(output.contains("OCCUPIED"), "Slot table should show occupied status after parking");
        assertTrue(output.contains("SEARCH RESULT") && output.contains("Vehicle Found"), "Option 4 should find the parked car");
        assertTrue(output.contains("CURRENTLY PARKED VEHICLES"), "Option 5 should list parked vehicles");
        assertTrue(output.contains("PARKING LOT REAL-TIME METRICS"), "Option 6 should show statistics");
        assertTrue(output.contains("PARKING FEE RULES"), "Option 7 should show fee rules");
        assertTrue(output.contains("\u20B940.00"), "Car rate must appear in INR");
        assertTrue(output.contains("\u20B920.00"), "Bike rate must appear in INR");
        assertTrue(output.contains("REMOVE VEHICLE"), "Option 2 should run checkout");
        assertTrue(output.contains("SMART PARKING LOT RECEIPT"), "Checkout should print a receipt");
        assertTrue(output.contains("\u20B9120.00"), "2h 15m car stay should bill 3 hours at \u20B940 = \u20B9120");
        assertTrue(output.contains("MAIN MENU"), "Control must return to the main menu between options");
        assertTrue(output.contains("Thank you for using the Smart Parking Lot Management System"), "Option 8 Y should exit cleanly");
        assertTrue(!output.contains("Input ended. Returning to Main Menu."), "Scripted input must not be consumed out of order");
    }

    private static void testInvalidNonNumericMenuInput() {
        String script = String.join("\n",
                "abc",
                "99",
                "-1",
                "",
                "7",
                "8",
                "Y"
        ) + "\n";

        String output = runMenuScript(new ParkingLot("Menu Lot", 2, 2, new ParkingFeeCalculator()), script);
        assertTrue(output.contains("Input must be an integer"), "Letters at the menu must be rejected");
        assertTrue(output.contains("Invalid choice"), "Out-of-range numbers must be rejected");
        assertTrue(output.contains("Choice cannot be empty"), "Blank menu lines must be rejected without hanging");
        assertTrue(output.contains("PARKING FEE RULES"), "Valid option 7 must still run after invalid input");
        assertTrue(output.contains("Thank you for using"), "Application must still exit cleanly");
    }

    private static void testOption7TariffOutputInInr() {
        ParkingFeeCalculator calc = new ParkingFeeCalculator();
        String summary = calc.getFeeRulesSummary();
        assertTrue(summary.contains("\u20B940.00"), "Fee rules must show car rate \u20B940.00");
        assertTrue(summary.contains("\u20B920.00"), "Fee rules must show bike rate \u20B920.00");
        assertTrue(summary.contains("INR"), "Fee rules must mention INR");
        assertTrue(summary.contains("1 Hour minimum"), "Fee rules must explain the minimum charge");
        assertTrue(summary.contains("Round-up-to-next-hour") || summary.contains("Ceiling"), "Fee rules must explain rounding");

        String output = runMenuScript(new ParkingLot("Menu Lot", 2, 2, calc), "7\n8\nY\n");
        assertTrue(output.contains("PARKING FEE RULES"), "Option 7 must display fee rules");
        assertTrue(output.contains("MAIN MENU"), "Option 7 must return to the main menu");
        assertTrue(output.contains("\u20B940.00") && output.contains("\u20B920.00"), "Option 7 output must include both INR rates");
    }

    private static void testOption8ExitAndCancellation() {
        String script = String.join("\n",
                "8",
                "N",
                "7",
                "8",
                "maybe",
                "Y"
        ) + "\n";

        String output = runMenuScript(new ParkingLot("Menu Lot", 2, 2, new ParkingFeeCalculator()), script);
        assertTrue(output.contains("Exit cancelled. Returning to Main Menu."), "N must cancel exit and return to the menu");
        assertTrue(output.contains("PARKING FEE RULES"), "After cancelling exit, other options must still work");
        assertTrue(output.contains("Please enter 'Y' for Yes or 'N' for No."), "Invalid Y/N answers must be rejected");
        assertTrue(output.contains("Thank you for using"), "Y must exit after confirmation");
        int thankYouCount = output.split("Thank you for using", -1).length - 1;
        assertEquals(1, thankYouCount, "Session summary should print exactly once on confirmed exit");
    }

    private static void testSearchBlankInputDoesNotCrash() {
        String script = String.join("\n",
                "4",
                "",
                "4",
                "UNKNOWN-999",
                "8",
                "Y"
        ) + "\n";

        String output = runMenuScript(new ParkingLot("Menu Lot", 2, 2, new ParkingFeeCalculator()), script);
        assertTrue(output.contains("[NOT FOUND]"), "Blank and missing plates must produce a not-found message");
        assertTrue(output.contains("Registration number cannot be blank") || output.contains("cannot be blank"),
                "Blank search must not crash; it should return to the menu");
        assertTrue(output.contains("No active vehicle found"), "Unknown registration should report not found");
        assertTrue(output.contains("Thank you for using"), "Application must continue after a blank search");
    }

    private static void testMenuDuplicateAndFullCapacity() {
        ParkingLot lot = new ParkingLot("Tiny Lot", 1, 1, new ParkingFeeCalculator());
        String script = String.join("\n",
                "1",
                "1",
                "KA-01-AB-1234",
                "Alice Smith",
                "Sedan",
                "1",
                "1",
                "KA-01-AB-1234",
                "1",
                "1",
                "KA-09-ZZ-9999",
                "Second Car",
                "Hatchback",
                "8",
                "Y"
        ) + "\n";

        String output = runMenuScript(lot, script);
        assertTrue(output.contains("already parked") || output.contains("ALREADY PARKED"),
                "Duplicate active registration must be rejected");
        assertTrue(output.contains("FULL") || output.contains("Parking Lot Full"),
                "Parking a second unique car into a 1-car lot must report full capacity");
        assertTrue(lot.isVehicleParked("KA-01-AB-1234"), "Original vehicle must remain parked after failed attempts");
        assertEquals(1, lot.getParkedVehicles().size(), "Failed parks must not corrupt occupancy");
        assertTrue(lot.isCollectionConsistencyValid(), "Collections must stay consistent after rejected parks");
    }

    private static String runMenuScript(ParkingLot lot, String script) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        System.setOut(capture);
        System.setErr(capture);
        try {
            Main.run(lot, new BufferedReader(new StringReader(script)));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
            capture.close();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface RunnableWithException {
        void run() throws Throwable;
    }
}
