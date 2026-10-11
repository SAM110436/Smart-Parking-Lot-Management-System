# SMART PARKING LOT MANAGEMENT SYSTEM (v2.0)
**A Robust, Pure Object-Oriented Console Application in Java 17+ (INR Edition)**

---

## 1. Abstract
The **Smart Parking Lot Management System** is a pure console-based Java application designed to streamline vehicle parking, slot allocation, active vehicle tracking, and automated checkout billing. Built following senior-level software engineering practices, it cleanly demonstrates the **Four Pillars of Object-Oriented Programming (OOP)**—*Encapsulation, Inheritance, Polymorphism, and Abstraction*—while managing in-memory collections (`ArrayList`, `HashMap`, `HashSet`) with guaranteed state consistency. The system features custom exception handling, round-up ceiling billing in Indian Rupees (₹ / INR), and resilient user input validation with graceful crash recovery and exit confirmation.

---

## 2. Project Objectives
- **Automated Slot Allocation**: Dynamically allocate compatible parking slots for two-wheelers (`Bike`) and four-wheelers (`Car`).
- **Slot Reuse**: Instantly release and reuse vacated parking slots when a parked vehicle checks out.
- **OOP Demonstration**: Serve as a reference educational project highlighting real-world OOP paradigms without external frameworks.
- **Synchronized Data Structures**: Keep an `ArrayList` of physical slots, a `HashMap` of active vehicles, and a `HashSet` of active registration plates in 100% sync.
- **Custom Business Exceptions**: Enforce business rules (preventing overcapacity, duplicate entries, invalid vehicle types, and missing records) via custom exceptions.
- **Transparent Billing Engine**: Implement a ceiling/round-up-to-next-hour billing calculator in INR (₹) with configurable hourly tariffs, minimum charges, and negative-duration guardrails.
- **Zero-Dependency Portability**: Run seamlessly on any system with Java 17+ using standard JDK tools (`javac`, `java`).

---

## 3. System Requirements
- **Java Development Kit (JDK)**: Java 17 LTS or later (Compatible with OpenJDK / Oracle JDK).
- **Operating System**: Windows 10/11, Linux (Ubuntu/Debian/Fedora), or macOS.
- **Build Tools / Dependencies**: None! Zero external JARs, zero Maven/Gradle requirements, no database setup, and no GUI frameworks needed.

---

## 4. Architecture & Project Structure

### Package Hierarchy
```
d:/Smart-Parking-Lot-Management-System/
├── src/
│   └── com/
│       └── smartparking/
│           ├── exception/
│           │   ├── DuplicateVehicleException.java
│           │   ├── InvalidVehicleTypeException.java
│           │   ├── ParkingLotFullException.java
│           │   └── VehicleNotFoundException.java
│           ├── model/
│           │   ├── Bike.java
│           │   ├── Car.java
│           │   ├── ParkingReceipt.java
│           │   ├── ParkingSlot.java
│           │   ├── ParkingStatistics.java
│           │   ├── Vehicle.java
│           │   └── VehicleType.java
│           ├── service/
│           │   ├── ParkingFeeCalculator.java
│           │   └── ParkingLot.java
│           ├── test/
│           │   └── ParkingLotTest.java
│           └── Main.java
├── bin/                       # Compiled .class bytecode
├── run.bat                    # One-click Windows batch launcher (with UTF-8 codepage)
├── test.bat                   # One-click Windows test runner (with UTF-8 codepage)
├── run.sh                     # Unix/macOS launcher
├── test.sh                    # Unix/macOS test runner
├── PROJECT_REPORT.md          # College Mini-Project Formal Report
└── README.md                  # Comprehensive Documentation
```

### Module Responsibilities
| Module / Class | Role & Responsibilities |
| :--- | :--- |
| `Vehicle.java` | **Abstract Base Class**: Defines common vehicle properties (`licensePlate`, `ownerName`, `entryTime`) and polymorphic abstract methods (`getHourlyRate()`, `getVehicleType()`, `getSpecificDetails()`). |
| `Car.java` | **Subclass**: Extends `Vehicle`, models four-wheelers with car-specific attributes (`carType`) and ₹40.00/hr (INR) tariff. |
| `Bike.java` | **Subclass**: Extends `Vehicle`, models two-wheelers with bike-specific attributes (`bikeType`) and ₹20.00/hr (INR) tariff. |
| `VehicleType.java` | **Enum**: Represents supported types (`CAR`, `BIKE`), slot prefixes, default rates in INR, and case-insensitive string parser. |
| `ParkingSlot.java` | **Slot Entity**: Encapsulates slot identity (`C-01`, `B-01`), compatibility checks, occupancy state, and vehicle docking/release. |
| `ParkingReceipt.java` | **Value Object**: Represents checkout invoice, calculating breakdown, billable hours, and formatted receipt in INR (₹). |
| `ParkingStatistics.java` | **Metrics Entity**: Real-time snapshot of occupancy %, breakdown per category (parked cars & bikes), total revenue collected in INR, and checkout volume. |
| `ParkingLot.java` | **Core Engine**: Orchestrates parking, vacating, searching, statistics, slot reuse, and ensures synchronized collection state. |
| `ParkingFeeCalculator.java`| **Billing Engine**: Implements round-up ceiling billing logic, hourly rates in INR (₹), negative duration safety, and tariff rules summary. |
| Custom Exceptions | **Error Domain**: Checked custom exceptions modeling domain-specific edge cases (`ParkingLotFullException`, `DuplicateVehicleException`, `VehicleNotFoundException`, `InvalidVehicleTypeException`). |
| `Main.java` | **Presentation Layer**: Interactive 8-option console UI with resilient input scanning, vehicle detail confirmation before checkout, and exit confirmation. |
| `ParkingLotTest.java` | **Automated Test Suite**: Zero-dependency automated unit and integration tests (22 automated test cases covering domain logic and all 8 menu options). |

---

## 5. Core Object-Oriented Concepts (Four Pillars)

### 1. Encapsulation
- All member variables across `Vehicle`, `ParkingSlot`, `ParkingLot`, and `ParkingReceipt` are declared `private` or `private final`.
- State access is regulated through validated getters and mutator methods.
- Slot occupancy cannot be mutated directly; clients must invoke `slot.park(vehicle)` or `slot.vacate()`, which enforce internal integrity rules.

### 2. Inheritance
- `Vehicle` serves as the generalized parent class for specific vehicles.
- `Car` and `Bike` inherit common attributes (`licensePlate`, `ownerName`, `entryTime`), validation logic, and equality definitions from `Vehicle`.
- Code duplication is eliminated while enabling extensible vehicle hierarchies.

### 3. Polymorphism
- Dynamic method dispatch via `Vehicle` reference:
  - Calling `vehicle.getHourlyRate()` automatically dispatches to `Car.getHourlyRate()` (₹40.00/hr) or `Bike.getHourlyRate()` (₹20.00/hr) at runtime.
  - Calling `vehicle.getSpecificDetails()` extracts specialized metadata without ugly `instanceof` cascades.
- Overridden `toString()`, `equals()`, and `hashCode()` methods ensure consistent object behavior.

### 4. Abstraction
- The `Vehicle` class is declared `abstract`; it cannot be directly instantiated without choosing a concrete category.
- The complex mathematics of round-up duration ceiling, negative timestamp clamping, and duration difference are abstracted inside `ParkingFeeCalculator`.
- `ParkingLot` provides high-level public operations (`parkVehicle`, `removeVehicle`, `searchVehicle`), completely hiding the underlying multi-collection synchronization details.

---

## 6. Collections Strategy & Guaranteed Consistency

The system concurrently uses three standard Java collections to achieve optimal time complexity and data integrity:

```
                      +-----------------------------+
                      |       ParkingLot Service    |
                      +-----------------------------+
                        /            |            \
                       /             |             \
                      v              v              v
            +----------------+ +---------------+ +---------------+
            |   ArrayList    | |    HashMap    | |    HashSet    |
            | <ParkingSlot>  | |<String,Vehicle>| |   <String>    |
            +----------------+ +---------------+ +---------------+
            | Ordered slots  | | O(1) Fast     | | O(1) Duplicate|
            | representation | | lookup by reg | | check on reg  |
            +----------------+ +---------------+ +---------------+
```

1. **`ArrayList<ParkingSlot>`**:
   - Preserves physical order of parking slots (`C-01` to `C-05`, `B-01` to `B-05`).
   - Supports indexed linear scans to allocate the first available compatible slot.
2. **`HashMap<String, Vehicle>`**:
   - Maps normalized license plates (e.g., `"KA-01-AB-1234"`) to the corresponding `Vehicle` object.
   - Provides $O(1)$ constant-time lookup for searching vehicles and checkout processing.
3. **`HashSet<String>`**:
   - Maintains the unique set of active parked registration numbers.
   - Performs instantaneous $O(1)$ duplicate verification before initiating parking.
4. **Collection Consistency Guarantee**:
   - In `parkVehicle()`: The slot is marked occupied, the vehicle is added to `activeVehicleMap`, and the plate is added to `activeRegistrationNumbers`.
   - In `removeVehicle()`: The slot is vacated, the vehicle is removed from `activeVehicleMap`, and the plate is removed from `activeRegistrationNumbers`.
   - Verified via `parkingLot.isCollectionConsistencyValid()`, guaranteeing that `slots.occupiedCount == activeVehicleMap.size() == activeRegistrationNumbers.size()` at all times.

---

## 7. Custom Exceptions
The application introduces four custom checked exceptions extending `java.lang.Exception`:

| Exception Class | When Thrown | Message Format Example |
| :--- | :--- | :--- |
| `ParkingLotFullException` | Attempting to park when all compatible slots for a vehicle type are occupied. | `"Parking lot is FULL! No available slots for vehicle type: Car (Capacity: 5)."` |
| `DuplicateVehicleException` | Attempting to park a vehicle whose registration number is already actively parked. | `"Vehicle with registration number 'KA-01-AB-1234' is ALREADY PARKED in slot 'C-01'."` |
| `VehicleNotFoundException` | Searching for or checking out a vehicle not currently registered in the lot. | `"No active vehicle found with registration number 'KA-99-ZZ-0000'."` |
| `InvalidVehicleTypeException`| User inputs an unsupported vehicle type or invalid shortcut. | `"Invalid vehicle type 'TRUCK'. Supported types are: CAR, BIKE."` |

---

## 8. Billing Logic & Round-Up Ceiling Rule (INR Tariff)
The billing calculation uses ceiling round-up logic:
$$\text{Duration Minutes} = \max(1, \text{Minutes between Entry and Exit})$$
$$\text{Billable Hours} = \max\left(1, \left\lceil \frac{\text{Duration Minutes}}{60.0} \right\rceil\right)$$
$$\text{Total Fee} = \text{Billable Hours} \times \text{Hourly Rate}$$

### Tariff Schedule (Indian Rupees - INR)
- **Car Rate**: ₹40.00 / hour
- **Bike Rate**: ₹20.00 / hour
- **Minimum Billing**: 1 Hour minimum charge applies.
- **Round-Up Rule**: 1–60 mins $\rightarrow$ 1 hr; 61–120 mins $\rightarrow$ 2 hrs; 121–180 mins $\rightarrow$ 3 hrs.
- **Negative Duration Guard**: If an inverted exit timestamp is provided, duration is clamped to minimum 1 minute (1 billable hour) to avoid negative billing.

### Billing Examples
| Actual Duration | Billable Hours | Vehicle Type | Hourly Rate | Total Amount (INR) |
| :--- | :--- | :--- | :--- | :--- |
| 1 minute (Instant) | 1 Hour | Bike | ₹20.00/hr | **₹20.00** |
| 45 minutes | 1 Hour | Car | ₹40.00/hr | **₹40.00** |
| 60 minutes | 1 Hour | Car | ₹40.00/hr | **₹40.00** |
| 61 minutes | 2 Hours | Car | ₹40.00/hr | **₹80.00** |
| 2 hours 15 minutes | 3 Hours | Car | ₹40.00/hr | **₹120.00** |
| 5 hours 01 minute | 6 Hours | Bike | ₹20.00/hr | **₹120.00** |

---

## 9. Setup & Execution Guide (Windows PowerShell & CMD)

### Prerequisite Check
Ensure Java 17+ is installed:
```powershell
java -version
javac -version
```

### Method 1: Using One-Click Scripts (Recommended)
- **Windows (Command Prompt / PowerShell)**:
  ```cmd
  run.bat     :: Compiles and launches Main console UI (with UTF-8 codepage)
  test.bat    :: Compiles and runs automated test suite
  ```
- **Linux / macOS / Git Bash**:
  ```bash
  chmod +x run.sh test.sh
  ./run.sh    # Compiles and launches Main console UI
  ./test.sh   # Compiles and runs automated test suite
  ```

### Method 2: Manual Windows PowerShell Commands

> **Note**: PowerShell's `Out-File` produces a BOM-prefixed file that confuses `javac`. Use the CMD-based commands below from a PowerShell terminal, or run them directly from Command Prompt.

#### 1. Compile Project (run from project root)
```powershell
# PowerShell – invoke cmd for reliable ASCII source list
cmd /c "if not exist bin mkdir bin & dir /s /b src\*.java > sources.txt & javac -encoding UTF-8 -d bin @sources.txt & del sources.txt"
```

#### 2. Run Main Application
```powershell
java "-Dfile.encoding=UTF-8" -cp bin com.smartparking.Main
```

#### 3. Run Automated Tests
```powershell
java "-Dfile.encoding=UTF-8" -cp bin com.smartparking.test.ParkingLotTest
```

---

## 10. Menu Options & Sample Console Output

### Main Menu Interface
```
+======================================================================+
|            SMART PARKING LOT MANAGEMENT SYSTEM (v2.0)                |
|        Robust Console Application - Java 17 - Pure OOP Design        |
+======================================================================+
+-------------------------- MAIN MENU ---------------------------------+
|  1. Park Vehicle                                                     |
|  2. Remove Vehicle                                                   |
|  3. Display Parking Slots                                            |
|  4. Search Vehicle                                                   |
|  5. Display Parked Vehicles                                          |
|  6. Parking Statistics                                               |
|  7. Parking Fee Rules                                                |
|  8. Exit                                                             |
+----------------------------------------------------------------------+
>> Enter your choice (1-8):
```

### 1. Parking a Vehicle (Option 1)
```
------------- [1. PARK A NEW VEHICLE] -------------
Select Vehicle Type [1. Car, 2. Bike] (or type CAR / BIKE): 1
Enter Vehicle Registration Number (e.g., KA-01-AB-1234 or DL01C1234): KA-01-AB-1234
Enter Vehicle Owner Name: Alice Smith
Enter Car Body Type (e.g., Sedan, SUV, Hatchback, EV) [Default: Sedan]: Sedan

[SUCCESS] Vehicle has been successfully parked.
+---------------- PARKING TICKET ----------------+
| Assigned Slot : C-01                           |
| Vehicle Reg   : KA-01-AB-1234                  |
| Vehicle Type  : Car                            |
| Owner Name    : Alice Smith                    |
| Entry Time    : 2026-10-10 14:17:01            |
| Hourly Rate   : ₹40.00 (INR)                   |
+------------------------------------------------+
```

### 2. Display Parking Slots Layout (Option 3)
```
------------- [3. PARKING SLOTS LAYOUT] -------------
+---------+--------------+--------------+------------------+------------------+----------------------+
| Slot ID | Slot Type    | Status       | Reg Number       | Vehicle Type     | Owner Name           |
+---------+--------------+--------------+------------------+------------------+----------------------+
| C-01    | Car          | OCCUPIED     | KA-01-AB-1234    | Car              | Alice Smith          |
| C-02    | Car          | AVAILABLE    | -                | -                | -                    |
| C-03    | Car          | AVAILABLE    | -                | -                | -                    |
| C-04    | Car          | AVAILABLE    | -                | -                | -                    |
| C-05    | Car          | AVAILABLE    | -                | -                | -                    |
| B-01    | Bike         | OCCUPIED     | MH-12-CD-5678    | Bike             | Bob Miller           |
| B-02    | Bike         | AVAILABLE    | -                | -                | -                    |
| B-03    | Bike         | AVAILABLE    | -                | -                | -                    |
| B-04    | Bike         | AVAILABLE    | -                | -                | -                    |
| B-05    | Bike         | AVAILABLE    | -                | -                | -                    |
+---------+--------------+--------------+------------------+------------------+----------------------+
Summary: Available: 8 / 10 slots (Cars: 4 available, Bikes: 4 available)
```

### 3. Search Vehicle (Option 4)
```
------------- [4. SEARCH VEHICLE] -------------
Enter Vehicle Registration Number (e.g., KA-01-AB-1234 or DL01C1234): ka-01-ab-1234

[SEARCH RESULT] Vehicle Found:
+====================================================+
| Slot Number   : C-01                               |
| Reg Number    : KA-01-AB-1234                      |
| Vehicle Type  : Car                                |
| Owner Name    : Alice Smith                        |
| Entry Time    : 2026-10-10 14:17:01                |
| Specifics     : Type: Sedan (Rate: ₹40.00/hr)      |
+====================================================+
```

### 4. Remove Vehicle & Billing Receipt in INR (Option 2)
```
------------- [2. REMOVE VEHICLE & CHECKOUT] -------------
Enter Vehicle Registration Number (e.g., KA-01-AB-1234 or DL01C1234): KA-01-AB-1234

[PARKED VEHICLE DETAILS]
  Registration No : KA-01-AB-1234
  Owner Name      : Alice Smith
  Vehicle Type    : Car
  Current Slot    : C-01
  Entry Timestamp : 2026-10-10 14:17:01
  Hourly Tariff   : ₹40.00 / hour (INR)

Choose Checkout Duration Mode:
  1. Instant Checkout (Use current real-time clock)
  2. Simulated Duration (Specify hours & minutes to test billing calculation)
>> Select mode (1 or 2): 2
Enter simulated hours elapsed (e.g., 0, 1, 2, 5): 2
Enter simulated additional minutes elapsed (0-59): 15

[SUCCESS] Vehicle checked out successfully! Slot C-01 is now RELEASED and available for reuse.
Final Parking Fee Calculated: ₹120.00 (INR)

====================================================
             SMART PARKING LOT RECEIPT
====================================================
 Receipt ID      : RCP-1001
 Registration No : KA-01-AB-1234
 Vehicle Type    : Car
 Owner Name      : Alice Smith
 Slot Released   : C-01
----------------------------------------------------
 Entry Time      : 2026-10-10 14:17:01
 Exit Time       : 2026-10-10 16:32:01
 Actual Duration : 2 hours 15 minutes (135 total mins)
 Billable Hours  : 3 hour(s) [Round-up ceiling]
 Hourly Rate     : ₹40.00 / hour (INR)
----------------------------------------------------
 TOTAL FEE DUE   : ₹120.00 (INR)
====================================================
         Thank You For Parking With Us!
====================================================
```

### 5. Real-Time Statistics (Option 6)
```
------------- [6. PARKING LOT STATISTICS] -------------
====================================================
            PARKING LOT REAL-TIME METRICS
====================================================
 Total Slots Capacity : 10
 Currently Occupied   : 1 (10.0% Occupancy)
 Currently Available  : 9
----------------------------------------------------
 [Car Slots Breakdown]
   Total: 5 | Parked Cars: 0 | Available: 5
 [Bike Slots Breakdown]
   Total: 5 | Parked Bikes: 1 | Available: 4
----------------------------------------------------
 Total Vehicles Checked Out : 1
 Total Revenue Collected    : ₹120.00 (INR)
====================================================
```

### 6. Exit with Confirmation (Option 8)
```
>> Enter your choice (1-8): 8
Are you sure you want to exit the system? (Y/N): Y
========================================================================
  Thank you for using the Smart Parking Lot Management System!
  Final Session Summary: 1 checkouts served, ₹120.00 (INR) collected.
========================================================================
```

---

## 11. Automated Test Results (22 Tests Verified)
All 22 unit and integration tests execute natively without external dependencies:

```
================================================================
   RUNNING AUTOMATED UNIT & INTEGRATION TESTS (JAVA 17 LTS)
================================================================

  [PASS] 1. Car Parking & Slot Allocation
  [PASS] 2. Bike Parking & Slot Allocation
  [PASS] 3. Duplicate Vehicle Prevention (DuplicateVehicleException)
  [PASS] 4. Parking Lot Full Handling (ParkingLotFullException)
  [PASS] 5. Search Vehicle Success & VehicleNotFoundException
  [PASS] 6. Vehicle Removal & Slot Vacating
  [PASS] 7. Round-Up To Next Hour Ceiling Billing
  [PASS] 8. Minimum 1-Hour Billing Rule
  [PASS] 9. Collection Consistency Synchronization Check
  [PASS] 10. Invalid Vehicle Type Validation
  [PASS] 11. Polymorphic Hourly Rates Verification
  [PASS] 12. Slot Reuse After Vehicle Departure
  [PASS] 13. Case-Insensitive Search & Removal with Trimming
  [PASS] 14. Invalid Input Rejection (Null & Empty Values)
  [PASS] 15. Parking Statistics Accuracy & Revenue Tracking
  [PASS] 16. Negative & Inverted Duration Handling
  [PASS] 17. Menu Options 1-8 End-to-End Script (Return to Menu)
  [PASS] 18. Invalid / Non-Numeric Menu Input Recovery
  [PASS] 19. Option 7 Tariff Output in INR
  [PASS] 20. Option 8 Exit Confirmation Y/N
  [PASS] 21. Search Blank Input Does Not Crash
  [PASS] 22. Menu Duplicate Registration And Full Capacity

================================================================
   TEST EXECUTION SUMMARY: 22 Run | 22 Passed | 0 Failed
================================================================
[SUCCESS] ALL AUTOMATED TESTS PASSED SUCCESSFULLY (100% PASS RATE)!
```

---

## 12. Limitations
- **In-Memory Volatility**: Data is retained in memory during the active session. When the application terminates, slot records and session revenue reset.
- **Single Level**: Models a single physical parking facility without multi-floor or zone elevator routing.
- **Console Interface**: Designed exclusively as a terminal application with standard ASCII output (no graphical UI).

---

## 13. Future Scope
- **File / Database Persistence**: Add JDBC or file-based serialization (JSON/CSV) to save parking logs across application restarts.
- **Multi-Level / Multi-Floor Expansion**: Support multi-tier parking lots (e.g., Basement 1, Floor 1-4) with VIP and EV charging bays.
- **Barcode / QR Code Generation**: Output ASCII QR codes or alphanumeric ticket tokens for automated gate integration.
- **RESTful API Wrapper**: Expose parking operations as Spring Boot REST endpoints or lightweight HTTP microservices.
