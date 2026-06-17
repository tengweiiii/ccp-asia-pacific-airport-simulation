/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
// Represents a single gate at the airport.
// Gate objects are inside an ArrayBlockingQueue in Airport.java.
// A plane removes a Gate from the queue when it docks and returns it when it undocks.
public class Gate implements Runnable {
    private final int gateNumber;

    public Gate(int gateNumber) {
        this.gateNumber = gateNumber;
        
    }

    public int getGateNumber() {
        return gateNumber;
        
    }

    @Override
    public void run() {
        System.out.println("Gate Thread - Gate " + gateNumber + " is available");
        
    }
}
