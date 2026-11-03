package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;
import java.util.Objects;

public record Battery(String id, double chargingKw) implements GridElement {
    public Battery { Objects.requireNonNull(id); }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
