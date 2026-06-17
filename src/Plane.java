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
import java.util.Random;

public class Plane implements Runnable {
    
    private final int planeId;
    private final Airport airport;
    private boolean isEmergency;
    
    private Gate assignedGate;
    
    private final Random rd = new Random();
    
    public Plane(int planeId, Airport airport, boolean isEmergency) {
        this.planeId = planeId;
        this.airport = airport;
        this.isEmergency = isEmergency;
        
    }
    
    public int getPlaneId() {
        return planeId;
        
    }
    
    @Override
    public void run() {
        // record the time this plane requests to land
        // used to calculate how long it waited before landing
        long requestTime = System.currentTimeMillis();
        long planeStartTime = System.currentTimeMillis();
        
        try {
            requestLanding(); 
            
            // block until a ground slot is available
            airport.enterGrounds();
            
            landing();
            
            // time from request to successful landing
            long waitingTime = System.currentTimeMillis() - requestTime;
            airport.getStatistics().recordWaitingTime(waitingTime);
            
            coastToGate();
            
            dockToGate();
            
            // disembark, cleaning and refill supplies happen concurrently
            // each is its own Runnable running on a separate thread
            // generate a batch of passenger objects to disembark
            // disembark batch
            List<Passenger> disembarkBatch = new ArrayList<>();
            int disembarkCount = 30 + (rd.nextInt(20) + 1);
            for (int i = 1; i <= disembarkCount; i++) {
                disembarkBatch.add(new Passenger(i, planeId, "disembarking from")); // action string
                
            }
            
            String gateName = "Gate-" + assignedGate.getGateNumber();

            Thread disembark = new Thread(new PassengerDisembark(planeId, gateName, disembarkBatch), "Disembark-Thread-" + planeId);
            Thread cleaning = new Thread(new Cleaning(planeId, gateName), "Cleaning-Thread-" + planeId);
            Thread supplies = new Thread(new RefillSupplies(planeId, gateName), "Supplies-Thread-" + planeId);

            disembark.start();
            cleaning.start();
            supplies.start();
            
            // wait for all three concurrent tasks to finish before refuelling
            disembark.join();
            cleaning.join();
            supplies.join();
            
            // refuelling is exclusive — only one plane can use the fuel truck at a time
            refuel();
            
            // boarding after refuelling is done
            // generate a fresh batch of passengers to board
            List<Passenger> embarkBatch = new ArrayList<>();
            int embarkCount = 30 + (rd.nextInt(20) + 1);
            for (int i = 1; i <= embarkCount; i++) {
                embarkBatch.add(new Passenger(i, planeId, "boarding")); // action string
                
            }

            Thread embark = new Thread(new PassengerEmbark(planeId, "Gate-" + assignedGate.getGateNumber(), embarkBatch), "Embark-Thread-" + planeId);
            
            embark.start();
            embark.join();
            
            airport.getStatistics().addPassengersBoarded(embarkBatch.size());
            
            undock();
            coastToRunway();
            takeOff();
            
            // release the ground slot so a waiting plane can now enter
            airport.leaveGrounds();
            airport.getStatistics().incrementPlanesServed();
            
            // calculate and print total time this plane spent in simulation
            long totalTime = System.currentTimeMillis() - planeStartTime;
            double totalSeconds = totalTime / 1000.0;
            System.out.println("ATC : Plane-" + planeId + " - Total used time: " + totalSeconds + " seconds");

        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted.");
            
        }
    }
    
    // =========================================================================
    // Landing Request
    // =========================================================================
    
    private void requestLanding() throws InterruptedException {
        airport.getATC().requestLanding(planeId);
        
        if (isEmergency) {
            // declare emergency before anything else so normal planes yield immediately
            airport.declareEmergency();
            System.out.println(Thread.currentThread().getName() + " : Pilot of Plane-" + planeId + " REQUEST FOR EMERGENCY LANDING!");
            
        } else {
            System.out.println(Thread.currentThread().getName() + " : Pilot of Plane-" + planeId + " request for landing!");
            
        }
        
        Thread.sleep(1500);
    }
    
    // =========================================================================
    // Landing 
    // =========================================================================
    
    private void landing() throws InterruptedException {
        if (!isEmergency) {
            // normal planes spin-wait if an emergency is declared
            // gives emergency plane priority access to the runway
            while (airport.isEmergencyWaiting()) {  
                Thread.sleep(100);
                
            }
            
            airport.getATC().grantLanding(planeId);
            
        } else {
            airport.getATC().emergencyLanding(planeId);
            
        }

        // acquire exclusive runway access — 
        // only one plane lands or departs at a time
        airport.acquireRunway();

        try {
            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " landing on runway!");
            Thread.sleep(2000);
            
        } finally {
            //  release runway in finally block to avoid deadlock if interrupted
            airport.releaseRunway();
            
            if (isEmergency) {
                // clear the emergency flag so normal planes can resume landing
                airport.clearEmergency(); 
                
            }
        }
    }
    
    // =========================================================================
    // Coast to Gate
    // =========================================================================
    
    private void coastToGate() throws InterruptedException {
        // assignGate() blocks if all 3 gates are occupied  
        // plane waits on the taxiway
        assignedGate = airport.assignGate();

        airport.getATC().assignGate(planeId, assignedGate.getGateNumber());

        System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " coasting to Gate-" + assignedGate.getGateNumber());

        Thread.sleep(1500);
        
    }
    
    // =========================================================================
    // Dock to Gate
    // =========================================================================
    
    private void dockToGate() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " docked at Gate-" + assignedGate.getGateNumber());
        Thread.sleep(1500);
        
    }
    
    // =========================================================================
    // Refuel
    // =========================================================================
    
    private void refuel() throws InterruptedException {
        airport.acquireFuelTruck();

        try {
            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Refilling fuel crew is coming to Gate-" + assignedGate.getGateNumber() + "!");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Fuel truck is refilling fuel at Gate-" + assignedGate.getGateNumber() + " - 30%");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Fuel truck is refilling fuel at Gate-" + assignedGate.getGateNumber() + " - 60%");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Fuel truck finished refilling fuel at Gate-" + assignedGate.getGateNumber() + " - 100%");

        } finally {
            airport.releaseFuelTruck();
            
        }
    }
    
    // =========================================================================
    // Undock
    // =========================================================================
    
    private void undock() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " undocking from Gate-" + assignedGate.getGateNumber());
        Thread.sleep(1500);
        
        // return the gate to the queue 
        // unblocks any plane waiting for a gate
        airport.releaseGate(assignedGate);
        
    }
    
    // =========================================================================
    // Coast to Runway
    // =========================================================================
    
    private void coastToRunway() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " coasting to runway.");
        Thread.sleep(1500);
        
    } 
    
    // =========================================================================
    // Take Off
    // =========================================================================
    
    private void takeOff() throws InterruptedException {
        airport.getATC().requestTakeOff(planeId);
        airport.getATC().grantTakeOff(planeId);

        // acquire runway exclusively for takeoff  
        // same resource shared with landing
        airport.acquireRunway();

        try {
            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId  + " taking off.");

            Thread.sleep(2000);

        } finally {
            airport.releaseRunway();
        }

        System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " has departed.");
        
    }
}
