package edu.aitu.energy.listener;
import edu.aitu.energy.event.EnergyEvent;
import edu.aitu.energy.event.Observer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventLog implements Observer<EnergyEvent> {
    private final ConcurrentLinkedQueue<EnergyEvent> events;
    public EventLog() { events = new ConcurrentLinkedQueue<>(); }
    public void onEvent(EnergyEvent event) { events.add(event); }
    public List<EnergyEvent> events() { return List.copyOf(events); }
}
