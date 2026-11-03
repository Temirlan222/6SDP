package edu.aitu.energy;
import edu.aitu.energy.event.*;
import edu.aitu.energy.event.Observer;
import edu.aitu.energy.listener.OneShotNotice;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class EventSubjectTest {
    @Test void deliversOnlyTheSubscribedType() {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        subject.subscribe(EventType.OUTAGE, event -> count.incrementAndGet());
        subject.publish(Fixtures.reading());
        assertEquals(0, count.get());
        subject.publish(new EnergyEvent(EventType.OUTAGE, "grid", Fixtures.firstLine(), 0, "outage"));
        assertEquals(1, count.get());
    }
    @Test void duplicateSubscriptionDoesNotDuplicateDelivery() {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        Observer<EnergyEvent> listener = event -> count.incrementAndGet();
        subject.subscribe(EventType.METER_READING, listener);
        subject.subscribe(EventType.METER_READING, listener);
        subject.publish(Fixtures.reading());
        assertEquals(1, count.get());
    }
    @Test void unsubscribeStopsSubsequentEvents() {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        Observer<EnergyEvent> listener = event -> count.incrementAndGet();
        subject.subscribe(EventType.METER_READING, listener);
        subject.unsubscribe(EventType.METER_READING, listener);
        subject.publish(Fixtures.reading());
        assertEquals(0, count.get());
    }
    @Test void listenerCanUnsubscribeInsideItsHandler() {
        var subject = new EventSubject<EnergyEvent>();
        var once = new OneShotNotice(subject);
        subject.subscribe(EventType.METER_READING, once);
        subject.publish(Fixtures.reading());
        subject.publish(Fixtures.reading());
        assertEquals(1, once.count());
    }
    @Test void listenerAddedInsideAHandlerStartsWithTheNextSnapshot() {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        Observer<EnergyEvent> added = event -> count.incrementAndGet();
        subject.subscribe(EventType.METER_READING, event -> subject.subscribe(EventType.METER_READING, added));
        subject.publish(Fixtures.reading());
        assertEquals(0, count.get());
        subject.publish(Fixtures.reading());
        assertEquals(1, count.get());
    }
    @Test void aFailingListenerDoesNotPreventOtherDeliveries() {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        subject.subscribe(EventType.METER_READING, event -> { throw new IllegalArgumentException("failure"); });
        subject.subscribe(EventType.METER_READING, event -> count.incrementAndGet());
        var failure = assertThrows(IllegalStateException.class, () -> subject.publish(Fixtures.reading()));
        assertEquals(1, count.get());
        assertEquals(1, failure.getSuppressed().length);
    }
    @Test void sourcesPublishDistinctDomainEventTypes() {
        var meters = new MeterReadingSource("meters");
        var status = new GridStatusSource("status");
        var count = new AtomicInteger();
        meters.subscribe(EventType.METER_READING, event -> count.incrementAndGet());
        status.subscribe(EventType.OUTAGE, event -> count.incrementAndGet());
        meters.read(Fixtures.firstLine(), 2);
        status.outage(Fixtures.firstLine());
        assertEquals(2, count.get());
    }
    @Test void recursivePublishingOfAnotherEventTypeIsSafe() {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        subject.subscribe(EventType.METER_READING, e -> subject.publish(
                new EnergyEvent(EventType.AGGREGATE, "aggregate", e.subtree(), e.value(), "summary")));
        subject.subscribe(EventType.AGGREGATE, event -> count.incrementAndGet());
        subject.publish(Fixtures.reading());
        assertEquals(1, count.get());
    }
}
