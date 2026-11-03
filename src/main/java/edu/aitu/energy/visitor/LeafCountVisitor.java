package edu.aitu.energy.visitor;
import edu.aitu.energy.tree.*;

// A new operation: all existing element classes remain unchanged.
public class LeafCountVisitor implements Visitor<Integer> {
    public Integer visit(Grid grid) { return count(grid); }
    public Integer visit(Substation substation) { return count(substation); }
    public Integer visit(Line line) { return count(line); }
    public Integer visit(Consumer consumer) { return 1; }
    public Integer visit(Generator generator) { return 1; }
    public Integer visit(Battery battery) { return 1; }
    public Integer visit(SmartMeter meter) { return 1; }
    public Integer visit(Transformer transformer) { return 1; }

    private int count(GridContainer container) {
        int total = 0;
        for (GridElement child : container.children()) total += child.accept(this);
        return total;
    }
}
