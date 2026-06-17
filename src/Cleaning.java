/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
// cabin cleaning crew servicing a plane at its gate.
// runs concurrently with PassengerDisembark and RefillSupplies on separate threads.
public class Cleaning implements Runnable {
    private final int planeId;
    private final String gate;

    public Cleaning(int planeId, String gate) {
        this.planeId = planeId;
        this.gate = gate;
        
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Cleaning crew is coming to " + gate + "!");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Cleaning crew is cleaning the plane at " + gate + " - 30%");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Cleaning crew is cleaning the plane at " + gate + " - 60%");
            Thread.sleep(1500);

            System.out.println(Thread.currentThread().getName() + " : Plane-" + planeId + " - Cleaning crew finished cleaning at " + gate + " - 100%");

        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            
        }
    }
}
