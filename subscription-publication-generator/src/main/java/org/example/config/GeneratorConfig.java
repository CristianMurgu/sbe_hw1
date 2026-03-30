package org.example.config;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class GeneratorConfig {
    // publication config
    private int totalPublications;
    private int totalSubscriptions;

    // publication field ranges
    private String[] companyValues = {"Google", "Microsoft", "Amazon", "Apple", "Facebook",
            "Netflix", "Tesla", "IBM", "Oracle", "SAP"};
    private double minValue = 10.0;
    private double maxValue = 500.0;
    private double minDrop = 0.0;
    private double maxDrop = 50.0;
    private double minVariation = -2.0;
    private double maxVariation = 2.0;
    private LocalDate minDate = LocalDate.of(2020, 1, 1);
    private LocalDate maxDate = LocalDate.of(2024, 12, 31);

    // Subscription configuration - field frequencies
    private Map<String, Double> fieldFrequencies = new HashMap<>();
    private Map<String, Double> equalityOperatorFrequencies = new HashMap<>();

    public GeneratorConfig() {
        // def field frequencies (sum = 100%)
        fieldFrequencies.put("company", 90.0);
        fieldFrequencies.put("value", 80.0);
        fieldFrequencies.put("variation", 70.0);

        // def equality operator frequencies company field
        equalityOperatorFrequencies.put("company", 70.0);
    }

    public int getTotalPublications() { return totalPublications; }
    public void setTotalPublications(int totalPublications) {
        this.totalPublications = totalPublications;
    }

    public int getTotalSubscriptions() { return totalSubscriptions; }
    public void setTotalSubscriptions(int totalSubscriptions) {
        this.totalSubscriptions = totalSubscriptions;
    }

    public String[] getCompanyValues() { return companyValues; }
    public void setCompanyValues(String[] companyValues) {
        this.companyValues = companyValues;
    }

    public double getMinValue() { return minValue; }
    public void setMinValue(double minValue) { this.minValue = minValue; }

    public double getMaxValue() { return maxValue; }
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }

    public double getMinDrop() { return minDrop; }
    public void setMinDrop(double minDrop) { this.minDrop = minDrop; }

    public double getMaxDrop() { return maxDrop; }
    public void setMaxDrop(double maxDrop) { this.maxDrop = maxDrop; }

    public double getMinVariation() { return minVariation; }
    public void setMinVariation(double minVariation) {
        this.minVariation = minVariation;
    }

    public double getMaxVariation() { return maxVariation; }
    public void setMaxVariation(double maxVariation) {
        this.maxVariation = maxVariation;
    }

    public LocalDate getMinDate() { return minDate; }
    public void setMinDate(LocalDate minDate) { this.minDate = minDate; }

    public LocalDate getMaxDate() { return maxDate; }
    public void setMaxDate(LocalDate maxDate) { this.maxDate = maxDate; }

    public Map<String, Double> getFieldFrequencies() { return fieldFrequencies; }
    public void setFieldFrequencies(Map<String, Double> fieldFrequencies) {
        this.fieldFrequencies = fieldFrequencies;
    }

    public Map<String, Double> getEqualityOperatorFrequencies() {
        return equalityOperatorFrequencies;
    }
    public void setEqualityOperatorFrequencies(Map<String, Double> equalityOperatorFrequencies) {
        this.equalityOperatorFrequencies = equalityOperatorFrequencies;
    }
}