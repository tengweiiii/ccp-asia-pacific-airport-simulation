/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
// passengers leaving the plane after it docks.
// runs concurrently with Cleaning and RefillSupplies — all three happen at the gate simultaneously.
import java.util.List;

public class PassengerDisembark implements Runnable {
    private final int planeId;
    private final String gate;
    private final List<Passenger> passengers;

    public PassengerDisembark(int planeId, String gate, List<Passenger> passengers) {
        this.planeId = planeId;
        this.gate = gate;
        this.passengers = passengers;
        
    }

    @Override
    public void run() {
        try {
            System.out.println("\t" + Thread.currentThread().getName() + " - " + passengers.size() + " passengers disembarking from Plane-" + planeId + " at " + gate);

            // passenger threads still created and run concurrently
            Thread[] passengerThreads = new Thread[passengers.size()];
            for (int i = 0; i < passengers.size(); i++) {
                passengerThreads[i] = new Thread(passengers.get(i), "Passenger-" + passengers.get(i).getPassengerId());
                
            }
            
            for (Thread t : passengerThreads) t.start();
            for (Thread t : passengerThreads) t.join();

            System.out.println(Thread.currentThread().getName() + " - Disembarking completed for Plane-" + planeId);

        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            
        }
    }

    public int getPassengerCount() {
        return passengers.size();
        
    }
}