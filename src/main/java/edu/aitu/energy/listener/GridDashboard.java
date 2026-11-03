package edu.aitu.energy.listener;
import edu.aitu.energy.event.EnergyEvent;
import edu.aitu.energy.event.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class GridDashboard implements Observer<EnergyEvent> {
    private final AtomicInteger received;
    public GridDashboard() { received = new AtomicInteger(); }
    public void onEvent(EnergyEvent event) { received.incrementAndGet(); }
    public int count() { return received.get(); }
}
