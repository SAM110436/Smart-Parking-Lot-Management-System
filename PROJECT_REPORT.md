# A MINI PROJECT REPORT
ON
## "SMART PARKING LOT MANAGEMENT SYSTEM"

**Submitted in partial fulfillment of the requirements for the award of the Degree of**
**Bachelor of Technology / Bachelor of Engineering / Bachelor of Computer Applications**
**in Computer Science & Engineering / Information Technology**

---

### ACADEMIC YEAR: 2025 – 2026

**Project Developed by:**
- **Student Name:** [Student Name]
- **University Roll No:** [Roll Number]
- **Department:** Computer Science and Engineering
- **Project Mentor / Guide:** Senior Project Faculty / Mentor

---

## CERTIFICATE OF APPROVAL

This is to certify that the mini-project titled **"SMART PARKING LOT MANAGEMENT SYSTEM"** submitted by **[Student Name]** (Roll No: **[Roll Number]**) is a bonafide work carried out in partial fulfillment of the requirements for the award of the degree in **Computer Science and Engineering**.

The project satisfies all academic specifications regarding Object-Oriented Software Engineering, algorithmic data structure utilization, robust exception management, and thorough software verification.

\
______________________________ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ______________________________
**Project Guide / Faculty Mentor** &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **Head of the Department (HOD)**
Department of Computer Science & Engg. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Department of Computer Science & Engg.

---

## DECLARATION

I hereby declare that the mini-project entitled **"SMART PARKING LOT MANAGEMENT SYSTEM"** submitted to the Department of Computer Science & Engineering is an original work designed and coded by me under the guidance of my project mentor. All software artifacts and documentation have been prepared in compliance with academic guidelines.

**Date:** 10 October 2026
**Place:** College Campus
**Signature of Candidate:** ____________________

---

## ACKNOWLEDGEMENTS

I express my deepest gratitude to my Project Mentor for offering continuous guidance, code review feedback, and invaluable architectural advice throughout the development of this Java mini-project.

I also extend my sincere thanks to the Head of Department and the faculty members of the Computer Science Department for providing the necessary computing resources and educational framework. Finally, I thank my peers and family for their unwavering support.

---

## ABSTRACT

The exponential growth in vehicular ownership in modern urban areas poses critical logistical challenges in parking management. Traditional manual parking operations suffer from inefficient slot hunting, delayed ticketing, human billing calculation errors, and the absence of real-time occupancy tracking.

This project, **"Smart Parking Lot Management System"**, presents a modular, robust, console-based Java application engineered with modern Java (Java 17 LTS). It models automated parking management through the four foundational pillars of Object-Oriented Programming (OOP): **Encapsulation**, **Inheritance**, **Polymorphism**, and **Abstraction**. The software orchestrates three core Java Collection data structures—`ArrayList<ParkingSlot>`, `HashMap<String, Vehicle>`, and `HashSet<String>`—maintaining strict mathematical and relational consistency across vehicle arrivals, slot reuse, and checkouts.

The application features custom domain exceptions (`ParkingLotFullException`, `DuplicateVehicleException`, `VehicleNotFoundException`, `InvalidVehicleTypeException`), a ceiling-based round-up hourly billing engine in Indian Rupees (₹ / INR), and resilient console input scanning that recovers gracefully from malformed user data. Complete verification is demonstrated via a built-in zero-dependency automated test suite (22 unit/integration tests with a 100% pass rate) and a comprehensive manual test plan.

---

## TABLE OF CONTENTS

1. **Chapter 1: Introduction**
   - 1.1 Overview
   - 1.2 Problem Statement
   - 1.3 Project Objectives
   - 1.4 Scope of the Project
2. **Chapter 2: System Analysis**
   - 2.1 Existing System vs. Proposed System
   - 2.2 Functional Requirements
   - 2.3 Non-Functional Requirements
   - 2.4 Software and Hardware Specifications
3. **Chapter 3: System Design & Architecture**
   - 3.1 High-Level Architecture
   - 3.2 Modular Class Design
   - 3.3 The Four OOP Pillars in Design
   - 3.4 Multi-Collection Consistency Strategy
4. **Chapter 4: Implementation Details**
   - 4.1 Vehicle Hierarchy & Polymorphic Rates in INR
   - 4.2 Slot Allocation, Vacating & Slot Reuse Workflow
   - 4.3 Custom Exception Handling
   - 4.4 Round-Up Ceiling Billing Algorithm (INR Tariff)
   - 4.5 Line-based UTF-8 Console Input (no Scanner nextInt/nextLine mixing) & Exit Confirmation
5. **Chapter 5: Testing & Quality Assurance**
   - 5.1 Test Methodology
   - 5.2 Manual Test Cases Matrix (16 Scenarios)
   - 5.3 Automated Test Execution Results (22 Verified Tests)
6. **Chapter 6: Results & Screenshots**
   - 6.1 Sample Terminal Runs
   - 6.2 Generated ASCII Invoices & Tickets in INR (₹)
7. **Chapter 7: Limitations & Future Enhancements**
   - 7.1 Current System Limitations
   - 7.2 Future Scope
8. **Chapter 8: Conclusion**
9. **References**

---

## CHAPTER 1: INTRODUCTION

### 1.1 Overview
Urban parking facilities require systematic software tools to optimize space utilization, reduce motorist queuing time, and guarantee transparent financial audit trails. The **Smart Parking Lot Management System** provides an automated console interface for parking operators to register incoming vehicles, allocate dedicated parking bays according to vehicle category (Car vs. Bike), locate active vehicles instantaneously, and process checkouts using standardized hourly billing policies denominated in Indian Rupees (₹ / INR).

### 1.2 Problem Statement
Manual parking registries rely on paper logs or unstructured spreadsheets. This causes:
1. Inability to instantly verify whether a parking bay is unoccupied.
2. Inadvertent issuance of duplicate tickets to vehicles already parked.
3. Billing inaccuracies caused by manual rounding of fractional parking hours.
4. Absence of instant operational metrics (occupancy percentages and revenue analytics).

### 1.3 Project Objectives
- **Standardized Object Model**: Develop an abstract `Vehicle` class extended by `Car` and `Bike` concrete models.
- **Dedicated Slot Allocation & Immediate Reuse**: Map two-wheelers to Bike slots (`B-xx`) and four-wheelers to Car slots (`C-xx`), automatically reusing released slots.
- **Synchronized Data Structures**: Concurrently operate `ArrayList`, `HashMap`, and `HashSet` without state discrepancies.
- **Robust Exception Handling**: Protect business logic integrity using customized checked exceptions.
- **Transparent Billing in INR**: Implement ceiling billing where fractional hours round up to the next full billing hour at ₹40.00/hr for cars and ₹20.00/hr for bikes.
- **Console Stability**: Ensure zero terminal crashes when users enter letters instead of numbers, empty strings, or cancel exit dialogs.

### 1.4 Scope of the Project
The application is intentionally scoped as a **pure console application in Java 17+** with zero third-party dependencies, databases, or web servers. This ensures maximum portability, rapid evaluation on any student or faculty computer, and focus on core Java language features and OOP mechanics.

---

## CHAPTER 2: SYSTEM ANALYSIS

### 2.1 Existing System vs. Proposed System

| Parameter | Existing Manual / Naive System | Proposed Smart Parking System |
| :--- | :--- | :--- |
| **Slot Allocation** | Manual inspection by parking guard. | Automatic lookup of next available compatible slot. |
| **Slot Reuse** | Slow, error-prone manual bookkeeping. | Immediate release and reuse upon checkout. |
| **Duplicate Entry** | No validation; duplicates cause conflicts. | $O(1)$ rejection via `HashSet` and custom exception. |
| **Search Time** | Linear paper scan ($O(N)$ or slower). | Instant $O(1)$ hash map lookup by license plate (case-insensitive). |
| **Billing Accuracy** | Human calculation prone to errors. | Automated round-up ceiling algorithm in INR (₹). |
| **Fault Tolerance** | Invalid inputs cause software crashes. | Graceful input validation loop with user feedback. |

### 2.2 Functional Requirements
1. **Park Vehicle (Option 1)**: Accepts vehicle type, registration plate, driver name, and optional model. Allocates slot, marks state, records entry timestamp, and prints ticket.
2. **Remove Vehicle (Option 2)**: Accepts registration plate, displays vehicle details and elapsed duration, frees slot, charges hourly fee in INR, and prints receipt.
3. **Display Slots Layout (Option 3)**: Renders tabular map of all slots, showing slot ID, slot type, status, parked vehicle registration, and parked vehicle type.
4. **Search Vehicle (Option 4)**: Queries active vehicle by license plate (case-insensitively, trimming spaces) and shows parked slot and duration.
5. **Display Parked Vehicles (Option 5)**: Enumerates all currently docked vehicles in tabular format.
6. **Parking Statistics (Option 6)**: Displays total capacity, occupancy rate, breakdown per type (parked cars & bikes), checkouts served, and total collected revenue in INR.
7. **Parking Fee Rules (Option 7)**: Explains billing rules, ceiling hours, and tariff table in INR (₹).
8. **Exit (Option 8)**: Confirms user intent (Y/N) before closing session with cumulative totals.

### 2.3 Non-Functional Requirements
- **Reliability**: Collections never drift out of sync (`isCollectionConsistencyValid() == true`).
- **Performance**: Vehicle queries and duplicate checks execute in $O(1)$ time complexity.
- **Portability**: Runs on Windows, Linux, and macOS without compiling native binaries.
- **Maintainability**: High cohesion and loose coupling; business logic isolated from presentation (`Main.java`).

### 2.4 Software and Hardware Specifications
- **Programming Language**: Java 17 LTS (Java Standard Edition).
- **Compiler**: `javac` version 17.0+.
- **Runtime**: OpenJDK / Oracle JRE 17.0+.
- **Hardware Requirement**: Any x86/x64/ARM processor with >= 512 MB RAM and 50 MB free disk space.

---

## CHAPTER 3: SYSTEM DESIGN & ARCHITECTURE

### 3.1 High-Level Architecture
The system follows a three-tier separation of concerns tailored for console software:

```
[ Console User Interface Layer (Main.java) ]
                   │
                   ▼  Invokes Methods & Catches Custom Exceptions
[ Business Logic & Service Layer (ParkingLot.java, ParkingFeeCalculator.java) ]
                   │
                   ▼  Manipulates Entities & Enforces Invariants
[ Domain Model Layer (Vehicle, Car, Bike, ParkingSlot, ParkingReceipt, ParkingStatistics) ]
```

### 3.2 Modular Class Design
```
                       ┌─────────────────────────┐
                       │     «abstract»          │
                       │       Vehicle           │
                       ├─────────────────────────┤
                       │ - licensePlate: String  │
                       │ - ownerName: String     │
                       │ - entryTime: LocalTime  │
                       ├─────────────────────────┤
                       │ + getHourlyRate()*      │
                       │ + getVehicleType()*     │
                       │ + getSpecificDetails()* │
                       └────────────▲────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
        ┌───────────────────────┐       ┌───────────────────────┐
        │         Car           │       │         Bike          │
        ├───────────────────────┤       ├───────────────────────┤
        │ - carType: String     │       │ - bikeType: String    │
        ├───────────────────────┤       ├───────────────────────┤
        │ + getHourlyRate()     │       │ + getHourlyRate()     │
        │   (₹40.00/hr INR)     │       │   (₹20.00/hr INR)     │
        └───────────────────────┘       └───────────────────────┘
```

### 3.3 The Four OOP Pillars in Design
1. **Encapsulation**:
   - Variables in `ParkingSlot`, `Vehicle`, `ParkingLot`, etc., are marked `private` or `private final`.
   - Modifying a slot's occupancy is restricted to `park()` and `vacate()` member methods.
2. **Inheritance**:
   - `Car` and `Bike` extend `Vehicle`.
   - Common fields (`licensePlate`, `ownerName`, `entryTime`) and behaviors (`getFormattedEntryTime()`, `equals()`, `hashCode()`) are defined once in `Vehicle`.
3. **Polymorphism**:
   - `Vehicle` defines `public abstract double getHourlyRate();`.
   - `Car` overrides it to return `₹40.00`; `Bike` overrides it to return `₹20.00`.
   - When calculating fees in `ParkingFeeCalculator`, invoking `vehicle.getHourlyRate()` executes dynamic method dispatch without needing `if (vehicle instanceof Car)`.
4. **Abstraction**:
   - The user interface interacts with `parkingLot.parkVehicle(vehicle)` without knowing how slots are organized or how hash maps are updated.
   - The billing engine hides mathematical ceiling formulas and negative-duration guardrails behind `feeCalculator.calculateFee(...)`.

### 3.4 Multi-Collection Consistency Strategy
To combine fast indexing with strict integrity, `ParkingLot` synchronizes three collections:
- `ArrayList<ParkingSlot> slots`: Holds physical slots in deterministic layout order.
- `HashMap<String, Vehicle> activeVehicleMap`: Offers $O(1)$ vehicle retrieval by normalized license plate.
- `HashSet<String> activeRegistrationNumbers`: Offers $O(1)$ duplicate checking.
- `HashMap<String, ParkingSlot> vehicleToSlotMap`: Provides $O(1)$ lookup from license plate to allocated slot.

```
Integrity Invariant:
slots.count(isOccupied == true) == activeVehicleMap.size() == activeRegistrationNumbers.size()
```

---

## CHAPTER 4: IMPLEMENTATION DETAILS

### 4.1 Vehicle Hierarchy & Polymorphic Rates in INR
```java
public abstract class Vehicle {
    private final String licensePlate;
    private final String ownerName;
    private final LocalDateTime entryTime;

    public Vehicle(String licensePlate, String ownerName, LocalDateTime entryTime) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be null or empty.");
        }
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.ownerName = ownerName.trim();
        this.entryTime = (entryTime != null) ? entryTime : LocalDateTime.now();
    }

    public abstract double getHourlyRate();
    public abstract VehicleType getVehicleType();
    public abstract String getSpecificDetails();
}
```

### 4.2 Custom Exception Hierarchy
All domain exceptions inherit from standard `java.lang.Exception`:
```java
public class DuplicateVehicleException extends Exception { ... }
public class ParkingLotFullException extends Exception { ... }
public class VehicleNotFoundException extends Exception { ... }
public class InvalidVehicleTypeException extends Exception { ... }
```

### 4.3 Round-Up Ceiling Billing Algorithm (INR Tariff)
```java
public long calculateDurationMinutes(LocalDateTime entryTime, LocalDateTime exitTime) {
    if (entryTime == null || exitTime == null) {
        throw new IllegalArgumentException("Entry time and exit time must not be null.");
    }
    if (exitTime.isBefore(entryTime)) {
        return 1; // Negative duration guard
    }
    long minutes = Duration.between(entryTime, exitTime).toMinutes();
    return Math.max(1, minutes);
}

public long calculateBillableHours(long durationMinutes) {
    if (durationMinutes <= 0) return 1;
    return (long) Math.ceil(durationMinutes / 60.0);
}

public double calculateFee(Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime) {
    long minutes = calculateDurationMinutes(entryTime, exitTime);
    long billableHours = calculateBillableHours(minutes);
    return billableHours * vehicle.getHourlyRate();
}
```

### 4.4 Line-based UTF-8 Console Input & Exit Confirmation
All console reads use `BufferedReader.readLine()` (UTF-8). Menu and numeric prompts never call `Scanner.nextInt()`, so leftover newlines cannot skip the next field. Empty lines reprint a prompt instead of hanging. End-of-input (EOF / piped script end) is signalled via a private `InputAbortedException` sentinel, which aborts the current operation or exits cleanly instead of inventing default values.
```java
private static boolean confirmExit(BufferedReader reader) {
    while (true) {
        System.out.print("Are you sure you want to exit the system? (Y/N): ");
        String ans;
        try {
            ans = readRawLine(reader);   // throws InputAbortedException on EOF
        } catch (InputAbortedException e) {
            return true;   // treat EOF as confirmed exit
        }
        if (ans == null) return true;
        ans = ans.trim().toUpperCase();
        if (ans.equals("Y") || ans.equals("YES")) return true;
        if (ans.equals("N") || ans.equals("NO")) {
            System.out.println("[INFO] Exit cancelled. Returning to Main Menu.");
            return false;
        }
        System.out.println("[!] Please enter 'Y' for Yes or 'N' for No.");
    }
}
```

---

## CHAPTER 5: TESTING & QUALITY ASSURANCE

### 5.1 Test Methodology
Testing was conducted using two complementary approaches:
1. **Lightweight Automated Unit & Integration Tests**: Executed programmatically via `com.smartparking.test.ParkingLotTest` (zero-dependency, pure Java assertions).
2. **Manual Functional & Boundary Test Cases**: Executed through interactive console sequences covering edge conditions.

### 5.2 Manual Test Cases Matrix (16 Scenarios)

| Test ID | Test Scenario | Input Data | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Normal Car Parking | Type: 1, Plate: `KA-01-AB-1234`, Owner: `Alice`, Model: `Sedan` | Allocated slot `C-01`, Ticket printed in INR. | Allocated `C-01`, Ticket printed. | **PASS** |
| **TC-02** | Normal Bike Parking | Type: 2, Plate: `MH-12-CD-5678`, Owner: `Bob`, Model: `Cruiser` | Allocated slot `B-01`, Ticket printed in INR. | Allocated `B-01`, Ticket printed. | **PASS** |
| **TC-03** | Duplicate Active Vehicle | Type: 1, Plate: `KA-01-AB-1234` | Rejection with `DuplicateVehicleException`. | Intercepted; error message displayed. | **PASS** |
| **TC-04** | Lot Full Condition | Park 6th car in a 5-car capacity lot | Throws `ParkingLotFullException`. | Graceful notice: Lot Full for Cars. | **PASS** |
| **TC-05** | Search Existing Vehicle | Plate: `ka-01-ab-1234` | Case-insensitive lookup; displays slot `C-01`, owner. | Found accurately. | **PASS** |
| **TC-06** | Search Non-Existent | Plate: `UNKNOWN-999` | Throws `VehicleNotFoundException`. | Reported: Vehicle not found. | **PASS** |
| **TC-07** | Ceiling Billing (135 min) | 2 hours 15 minutes elapsed | Billed as 3 hours @ ₹40 = ₹120.00. | Total fee calculated: ₹120.00. | **PASS** |
| **TC-08** | Minimum Billing (1 min) | Instant checkout | Billed as 1 hour (₹40.00 / ₹20.00). | Minimum 1 hr charged. | **PASS** |
| **TC-09** | Invalid Menu Option | Input: `abc`, `99`, `-1` | Loop recovers, prompts for valid 1-8. | Re-prompted cleanly. | **PASS** |
| **TC-10** | Invalid Reg Number | Input: `!@#$`, empty line | Rejection, prompts for alphanumeric. | Validated and re-prompted. | **PASS** |
| **TC-11** | Collection Consistency | Multiple parks and unparks | Invariant check returns `true`. | Invariant holds 100%. | **PASS** |
| **TC-12** | Slot Reuse Verification | Unpark Car 1 from `C-01`, park Car 2 | Car 2 assigned to reused slot `C-01`. | Reassigned `C-01` accurately. | **PASS** |
| **TC-13** | Case-Insensitive Removal | Input: `  ka-01-ab-1234  ` | Trims, upper-cases, and processes checkout. | Successfully removed. | **PASS** |
| **TC-14** | Empty Name / Plate Rejection | Input: blank string | Rejected; prompts user again. | Rejected cleanly. | **PASS** |
| **TC-15** | Revenue Separation | Park 2 vehicles; verify revenue is ₹0 | Revenue updates only upon checkout. | Revenue remains ₹0.00 until unpark. | **PASS** |
| **TC-16** | Exit Confirmation Dialog | Option 8 $\rightarrow$ Enter 'N', then 'Y' | 'N' returns to menu; 'Y' exits. | Handled gracefully. | **PASS** |

### 5.3 Automated Test Execution Results (22 Verified Tests)
Execution of `java -cp bin com.smartparking.test.ParkingLotTest` produces the verified output below:

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

## CHAPTER 6: RESULTS & SAMPLE OUTPUTS

### 6.1 Sample Parking Receipt in INR
```
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

### 6.2 Real-Time Statistics Output in INR
```
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

---

## CHAPTER 7: LIMITATIONS & FUTURE ENHANCEMENTS

### 7.1 Limitations
1. **Volatile Storage**: The system operates strictly with in-memory collections; closing the console process resets slot occupancy and financial logs.
2. **Single-Level Topology**: Does not model multi-level basement layouts or navigation between multiple physical ramps.
3. **Manual Operator Model**: Requires human console entry at arrival and checkout rather than automatic number-plate recognition (ANPR).

### 7.2 Future Scope
1. **Database Integration**: Introduce JDBC / Hibernate for MySQL/PostgreSQL persistence.
2. **Computer Vision ANPR**: Connect camera feeds using OpenCV for automated number plate detection.
3. **Web / Mobile Dashboard**: Wrap core business logic with a Spring Boot REST API and React dashboard.
4. **IoT Sensor Integration**: Connect ultrasonic sensors (ESP32/Arduino) to trigger automated slot occupancy updates.

---

## CHAPTER 8: CONCLUSION

The **Smart Parking Lot Management System** successfully fulfills all academic and functional requirements of a senior-level Java mini-project. It offers an elegant, production-grade example of core Java programming, showcasing how **Encapsulation**, **Inheritance**, **Polymorphism**, and **Abstraction** translate into clear, maintainable software design.

Through synchronized usage of `ArrayList`, `HashMap`, and `HashSet`, the system achieves $O(1)$ lookup, duplicate prevention, and slot reuse while maintaining physical order of parking slots. The inclusion of custom checked exceptions, ceiling billing algorithms in Indian Rupees (₹ / INR), and crash-resilient input scanning ensures enterprise-grade robustness. With an automated test suite achieving a 100% pass rate across 22 test cases, the project serves as an exemplary college project benchmark in Object-Oriented Software Engineering.

---

## REFERENCES

1. Schildt, Herbert. *Java: The Complete Reference*, 12th Edition, McGraw Hill, 2021.
2. Bloch, Joshua. *Effective Java*, 3rd Edition, Addison-Wesley, 2018.
3. Oracle Corporation. *Java Platform, Standard Edition 17 API Specification*, Java SE 17 Documentation.
4. Gamma, Erich, et al. *Design Patterns: Elements of Reusable Object-Oriented Software*, Addison-Wesley, 1994.
5. Martin, Robert C. *Clean Code: A Handbook of Agile Software Craftsmanship*, Prentice Hall, 2008.
