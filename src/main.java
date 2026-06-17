/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
// entry point for the Asia Pacific Airport simulation.
// 6 plane threads — 5 normal and 1 emergency with random arrival intervals.
// waits for all planes to depart before printing the final statistics report.
import java.util.Random;

public class main {

    public static void main(String[] args) {

        Statistics statistics = new Statistics();
        Airport airport = new Airport(statistics);

        Random rd = new Random();
        long startTime = System.currentTimeMillis();

        try {
            // print gate availability at startup
            Thread gate1 = new Thread(new Gate(1), "Gate-Thread-1");
            Thread gate2 = new Thread(new Gate(2), "Gate-Thread-2");
            Thread gate3 = new Thread(new Gate(3), "Gate-Thread-3");

            gate1.start();
            gate2.start();
            gate3.start();

            // small pause so gate status prints before planes arrive
            try {
                Thread.sleep(500);
                
            } catch (InterruptedException iex) {
                Thread.currentThread().interrupt();
                
            }
            
            // --- PHASE 1: Seed the airport with 2 planes to occupy gates ---
            // Plane 1 and 2 arrive quickly so they land and dock before the congestion begins

            Thread plane1 = new Thread(new Plane(1, airport, false), "Plane-Thread-1");
            plane1.setPriority(Thread.NORM_PRIORITY);
            plane1.start();
            Thread.sleep(rd.nextInt(2000)); // plane 2 arrives almost immediately after

            Thread plane2 = new Thread(new Plane(2, airport, false), "Plane-Thread-2");
            plane2.setPriority(6);
            plane2.start();
            Thread.sleep(rd.nextInt(2000)); // plane 1 and 2 are now landing to gates

            // --- PHASE 2: Congestion — planes 3 and 4 arrive while gates are occupied ---
            // At this point plane 1 and 2 are docked and running their service threads
            // Plane 3 and 4 will hit the groundCapacity or gate queue and be forced to wait

            Thread plane3 = new Thread(new Plane(3, airport, false), "Plane-Thread-3");
            plane3.setPriority(7);
            plane3.start();
            Thread.sleep(rd.nextInt(2000)); // plane 4 arrives almost simultaneously

            Thread plane4 = new Thread(new Plane(4, airport, false), "Plane-Thread-4");
            plane4.setPriority(8);
            plane4.start();
            Thread.sleep(rd.nextInt(2000)); // planes 3 and 4 are now queued and waiting

            // --- PHASE 3: Emergency plane arrives during peak congestion ---
            // Plane 6 declares an emergency while planes 3 and 4 are still waiting
            // It should jump ahead of them and get runway priority

            Thread plane6 = new Thread(new Plane(6, airport, true), "Plane-Thread-6");
            plane6.setPriority(Thread.MAX_PRIORITY);
            plane6.start();
            Thread.sleep(rd.nextInt(2000)); // give the emergency scenario time to play out visibly

            // --- PHASE 4: Final normal plane arrives after congestion eases ---
            Thread plane5 = new Thread(new Plane(5, airport, false), "Plane-Thread-5");
            plane5.setPriority(9);
            plane5.start();

            // Main thread waits for every plane to fully depart before printing the report
            plane1.join();
            plane2.join();
            plane3.join();
            plane4.join();
            plane5.join();
            plane6.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            
        }

        System.out.println("\n=================================================");
        System.out.println("           - ALL PLANES HAVE DEPARTED -            ");
        System.out.println("=================================================\n");

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        airport.printFinalReport(totalTime);
        
    }
}