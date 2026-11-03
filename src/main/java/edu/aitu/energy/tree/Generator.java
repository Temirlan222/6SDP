package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;
import java.util.Objects;

public record Generator(String id, double outputKw) implements GridElement {
    public Generator { Objects.requireNonNull(id); }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
