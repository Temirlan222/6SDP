package edu.aitu.energy.listener;
import edu.aitu.energy.event.*;
import edu.aitu.energy.event.Observer;
import edu.aitu.energy.visitor.Visitor;

public class InspectionService implements Observer<EnergyEvent> {
    private final Visitor<Double> visitor;
    private final Subject<EnergyEvent> reports;

    public InspectionService(Visitor<Double> visitor, Subject<EnergyEvent> reports) {
        this.visitor = visitor;
        this.reports = reports;
    }
    @Override
    public void onEvent(EnergyEvent event) {
        double loadKw = event.subtree().accept(visitor);
        reports.publish(new EnergyEvent(EventType.INSPECTION_REPORT, "inspection", event.subtree(),
                loadKw, "Net load of the affected subtree, kW"));
    }
}
