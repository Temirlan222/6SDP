package edu.aitu.energy;
import edu.aitu.energy.tree.*;
import edu.aitu.energy.visitor.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {
    @Test void numericVisitorMatchesHandCalculatedNetLoad() {
        // 10 - 4 - 2 + .1 + 20 - 5 + 3 + .2 = 22.3 kW.
        assertEquals(22.3, Fixtures.grid().accept(new NetLoadVisitor()), 0.000001);
    }
    @Test void numericVisitorCanVisitOnlyOneLine() {
        assertEquals(4.1, Fixtures.firstLine().accept(new NetLoadVisitor()), 0.000001);
    }
    @Test void dispatchUsesTheActualElementTypeThroughTheInterface() {
        GridElement element = new Generator("generator", 4);
        assertEquals(-4.0, element.accept(new NetLoadVisitor()));
    }
    @Test void statefulVisitorCountsTheEntireTree() {
        var counts = Fixtures.grid().accept(new InventoryVisitor());
        assertEquals(Map.of("Grid", 1, "Substation", 1, "Line", 2,
                "Consumer", 2, "Generator", 2, "Battery", 2, "SmartMeter", 2), counts);
    }
    @Test void stateAccumulatesAcrossVisitsOnTheSameVisitor() {
        var visitor = new InventoryVisitor();
        Fixtures.firstLine().accept(visitor);
        var counts = Fixtures.secondLine().accept(visitor);
        assertEquals(2, counts.get("Line"));
        assertEquals(2, counts.get("Consumer"));
    }
    @Test void filterReturnsExactlyTheDevicesAboveTheThreshold() {
        var devices = Fixtures.grid().accept(new HighLoadVisitor(10));
        assertEquals(List.of(new Consumer("shop", 20)), devices);
    }
    @Test void filterDoesNotIncludeTheThresholdItself() {
        assertTrue(new Consumer("home", 10).accept(new HighLoadVisitor(10)).isEmpty());
    }
    @Test void jsonExportHasExactNestedContainerStructure() {
        GridElement root = new Grid("g", List.of(new Substation("s", List.of(
                new Line("l", List.of(new Consumer("home", 10)))))));
        assertEquals("{\"type\":\"Grid\",\"id\":\"g\",\"children\":["
                + "{\"type\":\"Substation\",\"id\":\"s\",\"children\":["
                + "{\"type\":\"Line\",\"id\":\"l\",\"children\":["
                + "{\"type\":\"Consumer\",\"id\":\"home\",\"powerKw\":10.0}]}]}]}",
                root.accept(new JsonExportVisitor()));
    }
    @Test void jsonExportEscapesQuotesBackslashesAndControlCharacters() {
        var json = new Consumer("a\"b\\c\n\t" + (char) 1, 10).accept(new JsonExportVisitor());
        assertEquals("{\"type\":\"Consumer\",\"id\":\"a\\\"b\\\\c\\n\\t\\u0001\",\"powerKw\":10.0}", json);
    }
    @Test void jsonRejectsNonFiniteNumbersInsteadOfWritingInvalidJson() {
        assertThrows(IllegalArgumentException.class,
                () -> new Consumer("bad", Double.NaN).accept(new JsonExportVisitor()));
    }
    @Test void eventSubtreesCannotChangeThroughTheOriginalChildList() {
        var children = new ArrayList<GridElement>();
        children.add(new Consumer("home", 10));
        var line = new Line("line", children);
        children.clear();
        assertEquals(1, line.children().size());
        assertThrows(UnsupportedOperationException.class, () -> line.children().clear());
    }
}
