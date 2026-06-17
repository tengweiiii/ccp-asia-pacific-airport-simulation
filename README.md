# Asia Pacific Airport Simulation
---
<img width="773" height="581" alt="image" src="https://github.com/user-attachments/assets/a1c156aa-c96b-4484-ac66-5426240109dc" />

## Overview

This project simulates a small airport environment using **Java multithreading and concurrency concepts**. The simulation models the interaction between airplanes, air traffic control (ATC), gates, passengers, and refueling services while enforcing airport resource constraints.

The objective is to demonstrate the use of:

* Threads
* Synchronization
* Semaphores
* Locks/Monitors
* Thread Priorities
* Concurrent Execution
* Resource Sharing

---

# Airport Constraints

## Runway

* The airport has **only one runway**.
* All aircraft must use the same runway for:

  * Landing
  * Take-off

Only one aircraft may occupy the runway at any given time.

---

## Ground Capacity

* The airport can accommodate a maximum of **3 airplanes on the ground**, including:

  * Aircraft on the runway
  * Aircraft parked at gates

This restriction prevents collisions and congestion.

---

## Gates

* The airport has **3 gates**.
* There is **no waiting area on the ground**.
* An aircraft may only land if:

  * Ground capacity allows it.
  * A gate will be available after landing.

---

# Aircraft Life Cycle

After receiving landing permission from ATC, an aircraft must complete the following sequence:

1. Land on runway
2. Coast to assigned gate
3. Dock at gate
4. Passengers disembark
5. Refill supplies and clean aircraft
6. Refuel aircraft
7. Passengers embark
8. Undock from gate
9. Coast to runway
10. Take off

Each step must take a random amount of time to simulate real airport operations.

Example:

```java
Thread.sleep(rand.nextInt(2000) + 1000);
```

---

# Simulation Requirements

## Aircraft Arrival

* Total aircraft: **6 planes**
* New aircraft arrive every:

```java
sleep(rand.nextInt(2000));
```

which represents:

* 0 seconds
* 1 second
* 2 seconds

between arrivals.

---

## Passenger Capacity

Each aircraft can carry:

```text
Maximum 50 passengers
```

Passengers are always available in the terminal.

No passenger terminal capacity limitations need to be considered.

---

# Concurrency Requirements

## Concurrent Activities

The following activities must occur concurrently:

### Passenger Operations

At all gates simultaneously:

* Passenger disembarkation
* Passenger embarkation

### Ground Services

At all gates simultaneously:

* Aircraft cleaning
* Supply replenishment

These operations should be implemented using separate threads.

---

## Exclusive Activity

### Refueling

The airport has:

```text
1 Refueling Truck
```

Only one aircraft may refuel at a time.

Example:

```java
Semaphore fuelTruck = new Semaphore(1);
```

---

# Congested Airport Scenario

A special congestion scenario must be simulated.

## Scenario

1. Two aircraft are currently occupying both gates.
2. Two aircraft are waiting in the air to land.
3. A third incoming aircraft reports fuel shortage.
4. The emergency aircraft must receive landing priority.

---

## Emergency Landing Assumptions

### Assumption 1

Emergency aircraft are assigned:

```java
HIGH_PRIORITY
```

### Assumption 2

ATC immediately places the emergency aircraft at the front of the landing queue.

### Assumption 3

Normal aircraft continue circling until clearance is granted.

---

## Additional Assumptions

You are encouraged to add your own assumptions, such as:

### Weather Delay

Random delay before landing.

### Maintenance Delay

Unexpected service delay at gate.

### Fuel Consumption

Aircraft lose fuel while circling.

### Landing Queue

Aircraft waiting in air are stored in a priority queue.

Example:

```java
PriorityBlockingQueue<Plane>
```

---

# Thread Responsibilities

## ATC Thread

Responsible for:

* Landing permissions
* Runway allocation
* Emergency handling
* Queue management

---

## Plane Thread

Responsible for:

* Requesting landing
* Landing
* Gate operations
* Departure

---

## Passenger Thread

Responsible for:

* Boarding
* Disembarking

Passengers should perform actions independently.

---

## Service Thread

Responsible for:

* Cleaning
* Supply replenishment

---

## Fuel Truck Thread

Responsible for:

* Refueling aircraft

Only one aircraft may use it at any time.

---

# Output Requirements

The simulation output must clearly indicate which thread performs each action.

## Correct Example

```text
[Plane-1] Requesting permission to land

[ATC] Landing permission granted to Plane-1

[Passenger-15] Boarding Plane-1

[FuelTruck] Refueling Plane-1
```

---

## Incorrect Example

```text
[ATC] Plane-1 requesting permission to land

[MainThread] Plane-1 boarding passengers

[Passenger-8] ATC granted landing permission
```

A thread must never perform actions that belong to another thread.

---

# Statistics and Sanity Checks

After all aircraft have departed, the ATC manager performs a final airport inspection.

## Gate Verification

Verify:

```text
All gates are empty
```

---

## Waiting Time Statistics

Calculate:

### Maximum Waiting Time

```text
Maximum waiting time among all planes
```

### Minimum Waiting Time

```text
Minimum waiting time among all planes
```

### Average Waiting Time

```text
Average waiting time among all planes
```

Waiting time is measured from:

```text
Landing request → Landing permission granted
```

---

## Airport Statistics

Print:

```text
Total planes served
```

and

```text
Total passengers boarded
```

---

# Simulation Duration

The entire simulation must:

```text
Complete within 60 seconds
```

All aircraft must:

* Arrive
* Land
* Be serviced
* Depart

before the simulation terminates.

---

# Suggested Java Concurrency Tools

| Requirement                | Suggested Tool             |
| -------------------------- | -------------------------- |
| Single Runway              | Semaphore(1)               |
| Refueling Truck            | Semaphore(1)               |
| Ground Capacity (3 Planes) | Semaphore(3)               |
| Gate Allocation            | Semaphore(2)               |
| Emergency Queue            | PriorityBlockingQueue      |
| Statistics Collection      | AtomicInteger / AtomicLong |
| Thread Synchronization     | CountDownLatch             |

---

Airport Air Traffic Control (ATC) Simulation using Java Multithreading and Concurrency.
