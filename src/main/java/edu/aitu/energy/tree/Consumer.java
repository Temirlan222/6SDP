package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;
import java.util.Objects;

public record Consumer(String id, double demandKw) implements GridElement {
    public Consumer { Objects.requireNonNull(id); }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
