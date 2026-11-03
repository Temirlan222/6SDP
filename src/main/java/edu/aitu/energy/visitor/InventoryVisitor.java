package edu.aitu.energy.visitor;
import edu.aitu.energy.tree.*;
import java.util.LinkedHashMap;
import java.util.Map;

// Operation-local state. Create one visitor per inspection; do not share it between producers.
public class InventoryVisitor implements Visitor<Map<String, Integer>> {
    private final Map<String, Integer> counts;
    public InventoryVisitor() { counts = new LinkedHashMap<>(); }

    public Map<String, Integer> visit(Grid grid) { return collect("Grid", grid); }
    public Map<String, Integer> visit(Substation substation) { return collect("Substation", substation); }
    public Map<String, Integer> visit(Line line) { return collect("Line", line); }
    public Map<String, Integer> visit(Consumer consumer) { return record("Consumer"); }
    public Map<String, Integer> visit(Generator generator) { return record("Generator"); }
    public Map<String, Integer> visit(Battery battery) { return record("Battery"); }
    public Map<String, Integer> visit(SmartMeter meter) { return record("SmartMeter"); }

    private Map<String, Integer> collect(String type, GridContainer container) {
        record(type);
        for (GridElement child : container.children()) child.accept(this);
        return Map.copyOf(counts);
    }
    private Map<String, Integer> record(String type) {
        counts.merge(type, 1, Integer::sum);
        return Map.copyOf(counts);
    }
}
