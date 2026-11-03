package edu.aitu.energy.tree;
import edu.aitu.energy.visitor.Visitor;

public interface GridElement {
    String id();
    <R> R accept(Visitor<R> visitor);
}
