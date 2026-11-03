package edu.aitu.energy.event;
public interface Subject<E extends TypedEvent> {
    void subscribe(EventType type, Observer<? super E> observer);
    void unsubscribe(EventType type, Observer<? super E> observer);
    void publish(E event);
}
