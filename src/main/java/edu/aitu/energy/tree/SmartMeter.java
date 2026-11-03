package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;
import java.util.Objects;

public record SmartMeter(String id, double standbyKw) implements GridElement {
    public SmartMeter { Objects.requireNonNull(id); }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
