package edu.aitu.energy.visitor;
import edu.aitu.energy.tree.*;
import java.util.stream.Collectors;

public class JsonExportVisitor implements Visitor<String> {
    public String visit(Grid grid) { return container("Grid", grid); }
    public String visit(Substation substation) { return container("Substation", substation); }
    public String visit(Line line) { return container("Line", line); }
    public String visit(Consumer consumer) { return leaf("Consumer", consumer, consumer.demandKw()); }
    public String visit(Generator generator) { return leaf("Generator", generator, generator.outputKw()); }
    public String visit(Battery battery) { return leaf("Battery", battery, battery.chargingKw()); }
    public String visit(SmartMeter meter) { return leaf("SmartMeter", meter, meter.standbyKw()); }

    private String container(String type, GridContainer container) {
        String children = container.children().stream().map(child -> child.accept(this))
                .collect(Collectors.joining(","));
        return "{\"type\":" + quote(type) + ",\"id\":" + quote(container.id())
                + ",\"children\":[" + children + "]}";
    }
    private String leaf(String type, GridElement element, double powerKw) {
        if (!Double.isFinite(powerKw)) throw new IllegalArgumentException("JSON requires a finite number");
        return "{\"type\":" + quote(type) + ",\"id\":" + quote(element.id())
                + ",\"powerKw\":" + Double.toString(powerKw) + "}";
    }
    private String quote(String value) {
        StringBuilder text = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> text.append("\\\"");
                case '\\' -> text.append("\\\\");
                case '\b' -> text.append("\\b");
                case '\f' -> text.append("\\f");
                case '\n' -> text.append("\\n");
                case '\r' -> text.append("\\r");
                case '\t' -> text.append("\\t");
                default -> {
                    if (c < 0x20) text.append(String.format("\\u%04x", (int) c));
                    else text.append(c);
                }
            }
        }
        return text.append('"').toString();
    }
}
