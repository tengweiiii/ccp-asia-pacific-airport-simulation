/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
// ground crew restocking food, drinks and supplies.
// runs concurrently with PassengerDisembark and Cleaning on separate threads.
public class RefillSupplies implements Runnable {
    private final int planeId;
    private final String gate;

    public RefillSupplies(int planeId, String gate) {
        this.planeId = planeId;
        this.gate = gate;
        
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Refilling supplies crew is coming to " + gate + "!");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Refilling supplies crew is refilling supplies at " + gate + " - 30%");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Refilling supplies crew is refilling supplies at " + gate + " - 60%");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Refilling supplies crew finished refilling supplies at " + gate + " - 100%");

        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            
        }
    }
}