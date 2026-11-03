package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;
import java.util.Objects;

public record Transformer(String id, double lossKw) implements GridElement {
    public Transformer { Objects.requireNonNull(id); }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
