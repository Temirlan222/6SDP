package edu.aitu.energy;
import edu.aitu.energy.event.*;
import edu.aitu.energy.event.Observer;
import edu.aitu.energy.listener.*;
import edu.aitu.energy.runtime.ProducerRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {
    @Test @Timeout(15)
    void stressFourThreadsTimesOneThousandHasAnExactCount() throws Exception {
        var meters = new MeterReadingSource("meters");
        var aggregate = new UsageAggregator();
        var dashboard = new GridDashboard();
        var log = new EventLog();
        meters.subscribe(EventType.METER_READING, dashboard);
        meters.subscribe(EventType.METER_READING, aggregate);
        aggregate.subscribe(EventType.AGGREGATE, log);
        var line = Fixtures.firstLine();
        var executor = Executors.newFixedThreadPool(4);
        try (var runner = new ProducerRunner(executor)) {
            runner.run(IntStream.range(0, 4).mapToObj(i -> (Runnable) () -> {
                for (int event = 0; event < 1000; event++) meters.read(line, 1);
            }).toList());
        }
        assertEquals(4000, dashboard.count());
        assertEquals(4000, aggregate.summary().readings());
        assertEquals(4000, aggregate.summary().totalKwh());
        assertEquals(4000, log.events().size());
        assertTrue(executor.isTerminated());
    }
    @Test @Timeout(15)
    void concurrentSubscriptionChangesDoNotLoseStableListenerEvents() throws Exception {
        var subject = new EventSubject<EnergyEvent>();
        var count = new AtomicInteger();
        Observer<EnergyEvent> temporary = event -> { };
        subject.subscribe(EventType.METER_READING, event -> count.incrementAndGet());
        var reading = Fixtures.reading();
        var executor = Executors.newFixedThreadPool(4);
        try (var runner = new ProducerRunner(executor)) {
            runner.run(List.of(
                    () -> { for (int i = 0; i < 1000; i++) subject.publish(reading); },
                    () -> { for (int i = 0; i < 1000; i++) subject.publish(reading); },
                    () -> { for (int i = 0; i < 1000; i++) subject.publish(reading); },
                    () -> { for (int i = 0; i < 1000; i++) {
                        subject.subscribe(EventType.METER_READING, temporary);
                        subject.unsubscribe(EventType.METER_READING, temporary);
                    }}));
        }
        assertEquals(3000, count.get());
        assertTrue(executor.isTerminated());
    }
    @Test @Timeout(15)
    void producerFailureIsReportedAfterOtherAcceptedWorkCompletes() {
        var count = new AtomicInteger();
        var executor = Executors.newFixedThreadPool(3);
        assertThrows(ExecutionException.class, () -> {
            try (var runner = new ProducerRunner(executor)) {
                runner.run(List.of(() -> { throw new IllegalStateException("producer failed"); },
                        () -> count.addAndGet(100), () -> count.addAndGet(100)));
            }
        });
        assertEquals(200, count.get());
        assertTrue(executor.isTerminated());
    }
}
