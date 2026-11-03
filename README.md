# Energy Grid — Assignment 6, variant 12

Separate Java 17 project. Runtime dependencies: standard library only; tests: JUnit 5.
Build: `mvn clean verify`. Run: `java -jar target/energy-grid.jar 4` (pool size >=3).
Open pom.xml as a Maven project in IntelliJ and run edu.aitu.energy.Main.

## Baseline design

* Two sources: MeterReadingSource and GridStatusSource.
* Typed immutable EnergyEvent with immutable Grid/Substation/Line/device data.
* Generic Subject/Observer; per-type CopyOnWriteArraySet snapshots; synchronous push delivery.
* Five required listener roles: GridDashboard, OutageAlarm, UsageAggregator, InspectionService, EventLog.
  OneShotNotice additionally demonstrates unsubscribing inside its own callback.
* UsageAggregator is both observer and subject; its atomic summary is republished as AGGREGATE.
* Four visitor operations: NetLoadVisitor, InventoryVisitor, HighLoadVisitor, JsonExportVisitor.
* Visitors own traversal; accept only selects the appropriate visit overload through double dispatch.
* An OUTAGE event triggers NetLoadVisitor on only its related line; an INSPECTION_REPORT event returns the result.
* Main is the composition root. Dependencies are passed in constructors. No lazy component initialization.
* Three producer tasks use an injected ExecutorService. Futures are awaited, then shutdown/awaitTermination closes it.

Manual values: first line=10-4-2+.1=4.1 kW; second=20-5+3+.2=18.2 kW; whole grid=22.3 kW.
25 tests include 4 threads x1000 events =4000 exact deliveries/aggregates, and concurrent subscription changes.

## Planned trade-off commits

Add a new visitor without editing elements, then add a new element and update the visitor family.
The actual development dates are retained. This local session cannot demonstrate two weeks of development.

## Trade-off 1: new visitor

Changed files: src/main/java/edu/aitu/energy/visitor/LeafCountVisitor.java,
src/test/java/edu/aitu/energy/LeafCountVisitorTest.java, README.md.
No tree element or Visitor interface changed. A new operation implements the existing visitor contract.

## Trade-off 2: new element

Transformer adds 0.5 kW of losses. Updated files: tree/Transformer.java, visitor/Visitor.java,
all five concrete visitor files, Main.java, TransformerTest.java, README.md (Java paths are below edu/aitu/energy).
Unlike adding an operation, adding an element changes the common visitor contract and every visitor.
The final demo contains 9 leaves, first-line net load 4.6 kW and whole-grid net load 22.8 kW.
Baseline fixtures still contain the original eight devices and their hand-calculated 22.3 kW.
