package edu.aitu.energy;
import edu.aitu.energy.event.*;
import edu.aitu.energy.listener.*;
import edu.aitu.energy.visitor.NetLoadVisitor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    @Test void aggregatorRepublishesCoherentTotalsAsANewEvent() {
        var source = new MeterReadingSource("meter");
        var aggregate = new UsageAggregator();
        var log = new EventLog();
        source.subscribe(EventType.METER_READING, aggregate);
        aggregate.subscribe(EventType.AGGREGATE, log);
        source.read(Fixtures.firstLine(), 2);
        source.read(Fixtures.firstLine(), 3);
        assertEquals(2, aggregate.summary().readings());
        assertEquals(5, aggregate.summary().totalKwh());
        assertEquals(2, log.events().size());
        assertEquals(EventType.AGGREGATE, log.events().get(1).type());
        assertEquals(5, log.events().get(1).value());
    }
    @Test void outageInspectsOnlyItsRelatedLineAndPublishesAReport() {
        var status = new GridStatusSource("grid");
        var reports = new EventSubject<EnergyEvent>();
        var log = new EventLog();
        var inspection = new InspectionService(new NetLoadVisitor(), reports);
        reports.subscribe(EventType.INSPECTION_REPORT, log);
        status.subscribe(EventType.OUTAGE, inspection);
        status.outage(Fixtures.firstLine());
        assertEquals(1, log.events().size());
        var report = log.events().get(0);
        assertEquals(EventType.INSPECTION_REPORT, report.type());
        assertEquals("line-1", report.subtree().id());
        assertEquals(4.1, report.value(), 0.000001); // Whole grid is 22.3; the second line is excluded.
    }
    @Test void separateListenersReceiveTheirOwnSubscribedEvents() {
        var meters = new MeterReadingSource("meters");
        var status = new GridStatusSource("status");
        var dashboard = new GridDashboard();
        var alarm = new OutageAlarm();
        meters.subscribe(EventType.METER_READING, dashboard);
        status.subscribe(EventType.OUTAGE, dashboard);
        status.subscribe(EventType.OUTAGE, alarm);
        meters.read(Fixtures.firstLine(), 1);
        status.outage(Fixtures.firstLine());
        assertEquals(2, dashboard.count());
        assertEquals(1, alarm.count());
    }
}
