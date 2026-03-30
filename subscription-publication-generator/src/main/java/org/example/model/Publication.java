package org.example.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Publication {
    private String company;
    private double value;
    private double drop;
    private double variation;
    private LocalDate date;

    public Publication(String company, double value, double drop,
                       double variation, LocalDate date) {
        this.company = company;
        this.value = value;
        this.drop = drop;
        this.variation = variation;
        this.date = date;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("company", company);
        map.put("value", value);
        map.put("drop", drop);
        map.put("variation", variation);
        map.put("date", date);
        return map;
    }

    // Getters
    public String getCompany() { return company; }
    public double getValue() { return value; }
    public double getDrop() { return drop; }
    public double getVariation() { return variation; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return String.format("Publication{company='%s', value=%.2f, drop=%.2f, variation=%.2f, date=%s}",
                company, value, drop, variation, date);
    }
}