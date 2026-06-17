/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
// new passengers boarding the plane before departure.
// runs after disembark, cleaning, supplies and refuelling are all complete.
// boarding is the final step before the plane undocks and heads to the runway.
import java.util.List;

public class PassengerEmbark implements Runnable {
    private final int planeId;
    private final String gate;
    private final List<Passenger> passengers;

    public PassengerEmbark(int planeId, String gate, List<Passenger> passengers) {
        this.planeId = planeId;
        this.gate = gate;
        this.passengers = passengers;
    }

    @Override
    public void run() {
        try {
            System.out.println("\t" + Thread.currentThread().getName() + " - " + passengers.size() + " passengers boarding Plane-" + planeId + " at " + gate);

            // passenger threads still created and run concurrently
            Thread[] passengerThreads = new Thread[passengers.size()];
            for (int i = 0; i < passengers.size(); i++) {
                passengerThreads[i] = new Thread(passengers.get(i), "Passenger-" + passengers.get(i).getPassengerId());
                
            }
            
            for (Thread t : passengerThreads) t.start();
            for (Thread t : passengerThreads) t.join();

            System.out.println(Thread.currentThread().getName() + " - Boarding completed for Plane-" + planeId);

        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            
        }
    }

    public int getPassengerCount() {
        return passengers.size();
        
    }
}