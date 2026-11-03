package edu.aitu.energy.listener;
import edu.aitu.energy.event.*;
import edu.aitu.energy.event.Observer;
import java.util.concurrent.atomic.AtomicInteger;

public class OneShotNotice implements Observer<EnergyEvent> {
    private final Subject<EnergyEvent> source;
    private final AtomicInteger handled;
    public OneShotNotice(Subject<EnergyEvent> source) {
        this.source = source;
        this.handled = new AtomicInteger();
    }
    @Override
    public void onEvent(EnergyEvent event) {
        if (handled.compareAndSet(0, 1)) source.unsubscribe(event.type(), this);
    }
    public int count() { return handled.get(); }
}
