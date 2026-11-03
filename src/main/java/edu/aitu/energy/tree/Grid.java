package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;
import java.util.List;
import java.util.Objects;

public record Grid(String id, List<GridElement> children) implements GridContainer {
    public Grid {
        Objects.requireNonNull(id);
        children = List.copyOf(children);
    }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visit(this); }
}
