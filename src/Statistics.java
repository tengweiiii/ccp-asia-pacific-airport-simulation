/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {
    // AtomicInteger and AtomicLong are used to allow multiple plane threads
    // to update statistics concurrently without explicit synchronization
    private final AtomicInteger planesServed = new AtomicInteger(0);
    private final AtomicInteger passengersBoarded = new AtomicInteger(0);
    private final AtomicLong totalWaitingTime = new AtomicLong(0);
    private final AtomicLong maxWaitingTime = new AtomicLong(0);
    
    // Initialised to MAX_VALUE so any real waiting time will be smaller on first comparison
    private final AtomicLong minWaitingTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicInteger waitingSamples = new AtomicInteger(0);
    
    // =========================================================================
    // Plane Stats
    // =========================================================================
    
    public void incrementPlanesServed() {
        planesServed.incrementAndGet();
        
    }
    
    public int getPlanesServed() {
        return planesServed.get();
        
    }
    
    // =========================================================================
    // Passenger Stats
    // =========================================================================
    
    public void addPassengersBoarded(int passengers) {
        passengersBoarded.addAndGet(passengers);
        
    }
    
    public int getPassengersBoarded() {
        return passengersBoarded.get();
        
    }
    
    // =========================================================================
    // Waiting Time Stats
    // =========================================================================
    
    // synchronized here because updating max and min requires a read-then-write, which is not atomic even with AtomicLong  
    // a race condition could corrupt the values
    public synchronized void recordWaitingTime(long waitingTime) {
        totalWaitingTime.addAndGet(waitingTime);
        waitingSamples.incrementAndGet();
        
        if (waitingTime > maxWaitingTime.get()) {
            maxWaitingTime.set(waitingTime);
            
        }
        
        if (waitingTime < minWaitingTime.get()) {
            minWaitingTime.set(waitingTime);
            
        }
    }
    
    public long getMaxWaitingTime() {
        return maxWaitingTime.get();
        
    }
    
    // returns 0 if no waiting time was recorded to avoid returning MAX_VALUE
    public long getMinWaitingTime() {
        if (minWaitingTime.get() == Long.MAX_VALUE) {
            return 0;
            
        }
        
        return minWaitingTime.get();
    }
    
    public long getAverageWaitingTime() {
        int samples = waitingSamples.get();
        
        if (samples == 0) {
            return 0;
            
        }
        
        return totalWaitingTime.get()/ samples;
        
    }
}
