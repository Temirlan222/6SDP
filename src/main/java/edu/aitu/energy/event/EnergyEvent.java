package edu.aitu.energy.event;
import edu.aitu.energy.tree.GridElement;
import java.util.Objects;

public record EnergyEvent(EventType type, String sourceId, GridElement subtree,
                          double value, String details) implements TypedEvent {
    public EnergyEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(sourceId);
        Objects.requireNonNull(subtree);
        Objects.requireNonNull(details);
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Event value must be finite");
    }
}
