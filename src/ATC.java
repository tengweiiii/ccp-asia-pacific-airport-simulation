/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
import java.util.Date;

public class ATC {
    // all methods are synchronized to ensure log messages from different plane threads are printed atomically 
    // to prevent interleaving  
    private String getTime() {
        return "[" + new Date() + "]";
        
    }

    public synchronized void requestLanding(int planeId) {
        System.out.println(getTime() + " ATC : Landing request received from Plane-" + planeId);
        
    }
    
    public synchronized void grantLanding(int planeId) {
        System.out.println(getTime() + " ATC : Landing clearance granted to Plane-" + planeId);
        
    }
    
    public synchronized void assignGate(int planeId, int gateNumber) {
        System.out.println(getTime() + " ATC : Plane-" + planeId + " assigned to Gate " + gateNumber);
        
    }
    
    public synchronized void requestTakeOff(int planeId) {
        System.out.println(getTime() + " ATC : Plane-" + planeId + " requesting takeoff.");
        
    }
    
    public synchronized void grantTakeOff(int planeId) {
        System.out.println(getTime() + " ATC : Takeoff clearance granted to Plane-" + planeId);
        
    }
    
    // emergency landings bypass normal queuing — ATC immediately approves
    public synchronized void emergencyLanding(int planeId) {
        System.out.println(getTime() + " ATC : EMERGENCY LANDING APPROVED FOR PLANE " + planeId);
        
    }
}
