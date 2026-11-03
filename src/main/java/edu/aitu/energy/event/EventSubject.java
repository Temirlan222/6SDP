package edu.aitu.energy.event;
import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventSubject<E extends TypedEvent> implements Subject<E> {
    private final EnumMap<EventType, CopyOnWriteArraySet<Observer<? super E>>> listeners;

    public EventSubject() {
        listeners = new EnumMap<>(EventType.class);
        for (EventType type : EventType.values()) listeners.put(type, new CopyOnWriteArraySet<>());
    }

    @Override
    public void subscribe(EventType type, Observer<? super E> observer) {
        listeners.get(Objects.requireNonNull(type)).add(Objects.requireNonNull(observer));
    }

    @Override
    public void unsubscribe(EventType type, Observer<? super E> observer) {
        listeners.get(Objects.requireNonNull(type)).remove(observer);
    }

    @Override
    public void publish(E event) {
        Objects.requireNonNull(event);
        IllegalStateException failures = null;
        // The iterator is a snapshot: a callback may safely change subscriptions.
        for (Observer<? super E> observer : listeners.get(event.type())) {
            try {
                observer.onEvent(event);
            } catch (RuntimeException failure) {
                if (failures == null) failures = new IllegalStateException("Listener delivery failed");
                failures.addSuppressed(failure);
            }
        }
        if (failures != null) throw failures;
    }
}
