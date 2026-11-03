package edu.aitu.energy.listener;
import edu.aitu.energy.event.*;
import edu.aitu.energy.event.Observer;
import java.util.concurrent.atomic.AtomicReference;

public class UsageAggregator extends EventSubject<EnergyEvent> implements Observer<EnergyEvent> {
    private final AtomicReference<UsageSummary> summary;
    public UsageAggregator() { summary = new AtomicReference<>(new UsageSummary(0, 0)); }

    @Override
    public void onEvent(EnergyEvent event) {
        UsageSummary updated = summary.updateAndGet(previous -> new UsageSummary(
                previous.readings() + 1, previous.totalKwh() + event.value()));
        publish(new EnergyEvent(EventType.AGGREGATE, "usage-aggregator", event.subtree(),
                updated.totalKwh(), "readings=" + updated.readings()));
    }
    public UsageSummary summary() { return summary.get(); }
}
