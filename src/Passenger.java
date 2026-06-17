/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
public class Passenger implements Runnable {
    private final int passengerId;
    private final int planeId;
    private final String action;

    public Passenger(int passengerId, int planeId, String action) {
        this.passengerId = passengerId;
        this.planeId = planeId;
        this.action = action;
        
    }

    public int getPassengerId() {
        return passengerId;
        
    }

    public int getPlaneId() {
        return planeId;
        
    }

    @Override
    public void run() {
        System.out.println("\tPassenger-" + passengerId + " is " + action + " Plane-" + planeId);
        
    }
}
