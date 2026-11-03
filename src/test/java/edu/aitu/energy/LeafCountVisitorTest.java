package edu.aitu.energy;
import edu.aitu.energy.visitor.LeafCountVisitor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeafCountVisitorTest {
    @Test void countsEightDevicesWithoutCountingContainers() {
        assertEquals(8, Fixtures.grid().accept(new LeafCountVisitor()));
    }
    @Test void canCountOnlyTheRelatedLine() {
        assertEquals(4, Fixtures.firstLine().accept(new LeafCountVisitor()));
    }
}
