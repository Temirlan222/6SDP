package edu.aitu.energy.visitor;
import edu.aitu.energy.tree.*;
import java.util.ArrayList;
import java.util.List;

public class HighLoadVisitor implements Visitor<List<GridElement>> {
    private final double thresholdKw;
    public HighLoadVisitor(double thresholdKw) { this.thresholdKw = thresholdKw; }
    public List<GridElement> visit(Grid grid) { return collect(grid); }
    public List<GridElement> visit(Substation substation) { return collect(substation); }
    public List<GridElement> visit(Line line) { return collect(line); }
    public List<GridElement> visit(Consumer consumer) { return select(consumer, consumer.demandKw()); }
    public List<GridElement> visit(Generator generator) { return select(generator, generator.outputKw()); }
    public List<GridElement> visit(Battery battery) { return select(battery, Math.abs(battery.chargingKw())); }
    public List<GridElement> visit(SmartMeter meter) { return select(meter, meter.standbyKw()); }
    public List<GridElement> visit(Transformer transformer) { return select(transformer, transformer.lossKw()); }

    private List<GridElement> collect(GridContainer container) {
        List<GridElement> matches = new ArrayList<>();
        for (GridElement child : container.children()) matches.addAll(child.accept(this));
        return List.copyOf(matches);
    }
    private List<GridElement> select(GridElement element, double powerKw) {
        return powerKw > thresholdKw ? List.of(element) : List.of();
    }
}
