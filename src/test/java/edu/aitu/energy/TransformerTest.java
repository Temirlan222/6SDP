package edu.aitu.energy;
import edu.aitu.energy.tree.*;
import edu.aitu.energy.visitor.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {
    @Test void everyVisitorSupportsTheNewElement() {
        GridElement transformer = new Transformer("transformer", 0.5);
        assertEquals(0.5, transformer.accept(new NetLoadVisitor()));
        assertEquals(1, transformer.accept(new LeafCountVisitor()));
        assertEquals(Map.of("Transformer", 1), transformer.accept(new InventoryVisitor()));
        assertEquals(List.of(transformer), transformer.accept(new HighLoadVisitor(0.25)));
        assertEquals("{\"type\":\"Transformer\",\"id\":\"transformer\",\"powerKw\":0.5}",
                transformer.accept(new JsonExportVisitor()));
    }
}
