package edu.aitu.energy.event;
import edu.aitu.energy.tree.Line;

public class MeterReadingSource extends EventSubject<EnergyEvent> {
    private final String id;
    public MeterReadingSource(String id) { this.id = id; }
    public void read(Line line, double deltaKwh) {
        publish(new EnergyEvent(EventType.METER_READING, id, line, deltaKwh, "Energy increment, kWh"));
    }
}
