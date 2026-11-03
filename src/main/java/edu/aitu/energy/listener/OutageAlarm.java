package edu.aitu.energy.listener;
import edu.aitu.energy.event.EnergyEvent;
import edu.aitu.energy.event.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class OutageAlarm implements Observer<EnergyEvent> {
    private final AtomicInteger outages;
    public OutageAlarm() { outages = new AtomicInteger(); }
    public void onEvent(EnergyEvent event) { outages.incrementAndGet(); }
    public int count() { return outages.get(); }
}
