package edu.aitu.energy;
import edu.aitu.energy.event.*;
import edu.aitu.energy.listener.*;
import edu.aitu.energy.tree.*;
import edu.aitu.energy.visitor.*;
import edu.aitu.energy.runtime.ProducerRunner;
import java.util.List;
import java.util.concurrent.Executors;

// The only composition root: constructs the object graph and connects subscriptions.
public class Main {
    public static void main(String[] args) throws Exception {
        int threads = args.length == 0 ? 4 : Integer.parseInt(args[0]);
        if (threads < 3) throw new IllegalArgumentException("Use at least 3 producer threads");
        Line first = new Line("line-1", List.of(new Consumer("home", 10), new Generator("solar", 4),
                new Battery("storage", -2), new SmartMeter("meter", 0.1), new Transformer("transformer", 0.5)));
        Line second = new Line("line-2", List.of(new Consumer("shop", 20), new Generator("wind", 5),
                new Battery("charge", 3), new SmartMeter("meter-2", 0.2)));
        Grid grid = new Grid("north", List.of(new Substation("sub-A", List.of(first, second))));

        var meters = new MeterReadingSource("meter-feed");
        var status = new GridStatusSource("grid-status");
        var reports = new EventSubject<EnergyEvent>();
        var dashboard = new GridDashboard();
        var alarm = new OutageAlarm();
        var aggregate = new UsageAggregator();
        var log = new EventLog();
        var inspection = new InspectionService(new NetLoadVisitor(), reports);
        var once = new OneShotNotice(meters);
        meters.subscribe(EventType.METER_READING, dashboard);
        meters.subscribe(EventType.METER_READING, aggregate);
        meters.subscribe(EventType.METER_READING, once);
        status.subscribe(EventType.OUTAGE, dashboard);
        status.subscribe(EventType.OUTAGE, alarm);
        status.subscribe(EventType.OUTAGE, inspection);
        aggregate.subscribe(EventType.AGGREGATE, log);
        reports.subscribe(EventType.INSPECTION_REPORT, log);

        System.out.println("Variant 12: Energy Grid; producer pool=" + threads);
        System.out.println("Net load, kW: " + grid.accept(new NetLoadVisitor()));
        System.out.println("Leaf count: " + grid.accept(new LeafCountVisitor()));
        System.out.println("Inventory: " + grid.accept(new InventoryVisitor()));
        System.out.println("Devices above 10 kW: " + grid.accept(new HighLoadVisitor(10)));
        System.out.println("JSON: " + grid.accept(new JsonExportVisitor()));
        try (var runner = new ProducerRunner(Executors.newFixedThreadPool(threads))) {
            runner.run(List.of(
                    () -> { for (int i = 0; i < 10; i++) meters.read(first, 1); },
                    () -> { for (int i = 0; i < 10; i++) meters.read(second, 2); },
                    () -> { for (int i = 0; i < 10; i++) status.outage(first); }));
        }
        System.out.println("Dashboard events: " + dashboard.count());
        System.out.println("Outages: " + alarm.count());
        System.out.println("Aggregated readings: " + aggregate.summary().readings());
        System.out.println("Total energy, kWh: " + aggregate.summary().totalKwh());
        System.out.println("Aggregate events + inspection reports: " + log.events().size());
        System.out.println("One-shot notices: " + once.count());
        log.events().stream().filter(event -> event.type() == EventType.INSPECTION_REPORT).findFirst()
                .ifPresent(event -> System.out.println("Inspected subtree: " + event.subtree().id()
                        + "; loadKw=" + event.value()));
        System.out.println("All producer work completed; executor terminated.");
    }
}
