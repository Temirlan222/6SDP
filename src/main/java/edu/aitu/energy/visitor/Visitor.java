package edu.aitu.energy.visitor;
import edu.aitu.energy.tree.*;

public interface Visitor<R> {
    R visit(Grid grid);
    R visit(Substation substation);
    R visit(Line line);
    R visit(Consumer consumer);
    R visit(Generator generator);
    R visit(Battery battery);
    R visit(SmartMeter meter);
}
