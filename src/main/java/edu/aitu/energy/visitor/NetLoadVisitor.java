package edu.aitu.energy.visitor;
import edu.aitu.energy.tree.*;

public class NetLoadVisitor implements Visitor<Double> {
    public Double visit(Grid grid) { return sum(grid); }
    public Double visit(Substation substation) { return sum(substation); }
    public Double visit(Line line) { return sum(line); }
    public Double visit(Consumer consumer) { return consumer.demandKw(); }
    public Double visit(Generator generator) { return -generator.outputKw(); }
    public Double visit(Battery battery) { return battery.chargingKw(); }
    public Double visit(SmartMeter meter) { return meter.standbyKw(); }
    public Double visit(Transformer transformer) { return transformer.lossKw(); }

    private double sum(GridContainer container) {
        double total = 0;
        for (GridElement child : container.children()) total += child.accept(this);
        return total;
    }
}
