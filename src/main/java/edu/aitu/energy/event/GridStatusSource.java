package edu.aitu.energy.event;
import edu.aitu.energy.tree.Line;

public class GridStatusSource extends EventSubject<EnergyEvent> {
    private final String id;
    public GridStatusSource(String id) { this.id = id; }
    public void outage(Line line) {
        publish(new EnergyEvent(EventType.OUTAGE, id, line, 0, "Line outage"));
    }
}
