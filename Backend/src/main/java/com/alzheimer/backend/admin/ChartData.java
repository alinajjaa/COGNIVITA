package com.alzheimer.backend.admin;

public class ChartData {
    private String label;
    private long value;
    private String color;

    public ChartData(String label, long value, String color) {
        this.label = label;
        this.value = value;
        this.color = color;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public long getValue() { return value; }
    public void setValue(long value) { this.value = value; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
