/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ArrayBlockingQueue;

public class Airport {
    
    // only 1 runway available
    // fair mode make sure planes queue in sequential order to prevent starvation
    private final Semaphore runway = new Semaphore(1, true);
    
    // maximum of 3 planes on the ground 
    // prevent overcrowd and simulate real life space constraints
    private final Semaphore groundCapacity = new Semaphore(3, true);
    
    // only 1 refuelling truck avaiable
    // must be used exclusively by one plane at a time
    private final Semaphore fuelTruck = new Semaphore(1, true);
    
    // allGates keeps a reference for status reporting
    // gates is the blocking queue normal planes draw from -  blocks if all 3 are occupied
    private final List<Gate> allGates = new ArrayList<>();
    private final ArrayBlockingQueue<Gate> gates = new ArrayBlockingQueue<>(3);
    
    // emergencyLock guards the emergencyWaiting flag with mutual exclusion
    // semaphore is used to stay consistent
    private final Semaphore emergencyLock = new Semaphore(1, true);
    private boolean emergencyWaiting = false;
    
    private final ATC atc = new ATC();
    private final Statistics statistics;
    
    public Airport(Statistics statistics) {
        this.statistics = statistics;
        
        // Create 3 gates and add them to both the reference list and the available queue
        Gate gate1 = new Gate(1);
        Gate gate2 = new Gate(2);
        Gate gate3 = new Gate(3);
        
        allGates.add(gate1);
        allGates.add(gate2);
        allGates.add(gate3);

        gates.add(gate1);
        gates.add(gate2);
        gates.add(gate3);
        
    }
    
    // =========================================================================
    // Airport Grounds Control
    // =========================================================================
    
    // called before a plane lands
    // block if 3 planes are already on the ground
    public void enterGrounds() throws InterruptedException {
        groundCapacity.acquire();
        
    }
    
    // called after a plane takes off
    // frees up one ground slot
    public void leaveGrounds() {
        groundCapacity.release();
        
    }
    
    // =========================================================================
    // Runway Control
    // =========================================================================
    
    // acquire exclusive runway access
    // only one plane can use the runway at a time
    public void acquireRunway() throws InterruptedException {
        runway.acquire();
        
    }
    
    // release the runway after landing or takeoff is complete
    public void releaseRunway() {
        runway.release();
        
    }
    
    // =========================================================================
    // Gate Control 
    // =========================================================================
    
    // removes a gate from the available queue
    // block the calling thread if all gatea are occupied
    public Gate assignGate() throws InterruptedException {
        return gates.take(); // thread-safe and handles the waiting automatically
        
    }
    
    // returns a gate to the avaiable queue once a plane undocks
    public void releaseGate(Gate gate) throws InterruptedException {
        gates.put(gate); // wakes up any thread blocked waiting for a gate
        
    }
    
    // =========================================================================
    // Refuelling Truck
    // =========================================================================
    
    // acquires the single fuel truck  
    // other planes must wait if it is in use
    public void acquireFuelTruck() throws InterruptedException {
        fuelTruck.acquire();
        
    }
    
    // releases the fuel truck so the next waiting plane can refuel
    public void releaseFuelTruck() {
        fuelTruck.release();
        
    }
    
    // =========================================================================
    // Sanity Check 
    // =========================================================================
    
    // returns true if all 3 gates are back in the available queue
    public boolean allGatesEmpty() {
        return gates.size() == 3;
        
    }
    
    public void printGateStatus() {
        System.out.println("\n================== GATE STATUS ==================");
        
        for (Gate gate: allGates) {
            // a gate present in the queue means it is not occupied by any plane
            boolean available = gates.contains(gate);
            System.out.println("Gate " + gate.getGateNumber() + " : " + (available ? "EMPTY" : "OCCUPIED"));
            
        }
        
        System.out.println("=================================================\n");
    }
    
    // =========================================================================
    // Emergency Landing 
    // =========================================================================
    
    public void acquireEmergencyLock() throws InterruptedException {
        emergencyLock.acquire();
        
    }

    public void releaseEmergencyLock() {
        emergencyLock.release();
        
    }
    
    // sets the emergency flag
    // guarded by emergencyLock to prevent race conditions
    // uses try finally to guarantee the lock is always released even error occurs
    public void declareEmergency() throws InterruptedException {
        emergencyLock.acquire();
        
        try {
            emergencyWaiting = true;
            
        } finally {
            emergencyLock.release();
            
        }
    }

    // clears the emergency flag once the emergency plane has landed
    public void clearEmergency() throws InterruptedException {
        emergencyLock.acquire();
        
        try {
            emergencyWaiting = false;
            
        } finally {
            emergencyLock.release();
            
        }
    }

    // reads the emergency flag safely under the lock
    // non-emergency planes check this before attempting to land
    public boolean isEmergencyWaiting() throws InterruptedException {
        emergencyLock.acquire();
        
        try {
            return emergencyWaiting;
            
        } finally {
            emergencyLock.release();
            
        }
    }
    
    // =========================================================================
    // Aircraft Traffic Controller 
    // =========================================================================
    
    public ATC getATC() {
        return atc;
        
    }
    
    // =========================================================================
    // Statistics
    // =========================================================================
    
    public Statistics getStatistics() {
        return statistics;
        
    }
    
    // =========================================================================
    // Final Report 
    // =========================================================================
    
    public void printFinalReport(long totalTime) {
        System.out.println("========== ASIA PACIFIC AIRPORT REPORT ==========");
        System.out.println("All Gates Empty         : " + (allGatesEmpty() ? "YES" : "NO"));
        System.out.println("Total Simulation Time   : " + totalTime + " seconds");
        System.out.println("Total Planes Served     : " + statistics.getPlanesServed());
        System.out.println("Total Passengers Boarded: " + statistics.getPassengersBoarded());
        System.out.printf("Maximum Waiting Time    : %.2f seconds%n",
            statistics.getMaxWaitingTime() / 1000.0);
        System.out.printf("Average Waiting Time    : %.2f seconds%n",
            statistics.getAverageWaitingTime() / 1000.0);
        System.out.printf("Minimum Waiting Time    : %.2f seconds%n",
            statistics.getMinWaitingTime() / 1000.0);
        System.out.println("=================================================");
        
    }
}
