package edu.aitu.energy;
import edu.aitu.energy.tree.*;
import edu.aitu.energy.event.*;
import java.util.List;

final class Fixtures {
    static Line firstLine() {
        return new Line("line-1", List.of(new Consumer("home", 10), new Generator("solar", 4),
                new Battery("storage", -2), new SmartMeter("meter", 0.1)));
    }
    static Line secondLine() {
        return new Line("line-2", List.of(new Consumer("shop", 20), new Generator("wind", 5),
                new Battery("charge", 3), new SmartMeter("meter-2", 0.2)));
    }
    static Grid grid() { return new Grid("north", List.of(new Substation("sub-A", List.of(firstLine(), secondLine())))); }
    static EnergyEvent reading() { return new EnergyEvent(EventType.METER_READING, "meter", firstLine(), 1, "reading"); }
}
