package org.example;

import org.example.config.GeneratorConfig;
import org.example.generator.DataGenerator;
import org.example.model.Publication;
import org.example.model.Subscription;

import java.util.List;
import java.util.Map;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=== TESTING SUBSCRIPTION & PUBLICATION GENERATOR ===\n");

        testSmallDataset();

        testPerformanceComparison();

        testDifferentFrequencies();
    }

    private static void testSmallDataset() {
        System.out.println("--- TEST 1: Small Dataset (1000 items each) ---");

        GeneratorConfig config = new GeneratorConfig();
        config.setTotalPublications(1000);
        config.setTotalSubscriptions(1000);

        try {
            DataGenerator generator = new DataGenerator(config, 2);
            DataGenerator.GenerationResult result = generator.generate();

            System.out.println("✓ Generated " + result.getPublications().size() + " publications");
            System.out.println("✓ Generated " + result.getSubscriptions().size() + " subscriptions");
            System.out.println("✓ Generation time: " + result.getGenerationTimeMs() + " ms\n");

            System.out.println("Sample publications (first 3):");
            result.getPublications().stream().limit(3).forEach(p ->
                    System.out.println("  " + p));

            System.out.println("\nSample subscriptions (first 3):");
            result.getSubscriptions().stream().limit(3).forEach(s ->
                    System.out.println("  " + s));

            System.out.println();

        } catch (Exception e) {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testPerformanceComparison() {
        System.out.println("--- TEST 2: Performance Comparison ---");

        int[] parallelismLevels = {1, 2, 4};
        int numMessages = 50000;

        System.out.println("Generating " + numMessages + " publications and " +
                numMessages + " subscriptions\n");

        for (int parallelism : parallelismLevels) {
            GeneratorConfig config = new GeneratorConfig();
            config.setTotalPublications(numMessages);
            config.setTotalSubscriptions(numMessages);

            try {
                // Run multiple times for average
                long totalTime = 0;
                int runs = 2;

                for (int i = 0; i < runs; i++) {
                    DataGenerator generator = new DataGenerator(config, parallelism);
                    long startTime = System.currentTimeMillis();
                    DataGenerator.GenerationResult result = generator.generate();
                    totalTime += result.getGenerationTimeMs();
                }

                long avgTime = totalTime / runs;

                System.out.printf("Parallelism %d: %d ms (avg over %d runs)%n",
                        parallelism, avgTime, runs);

            } catch (Exception e) {
                System.err.println("✗ Test failed for parallelism " + parallelism);
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    private static void testDifferentFrequencies() {
        System.out.println("--- TEST 3: Different Field Frequency Configurations ---");

        // Test configuration with 100% company frequency
        System.out.println("\nScenario 1: Company field in 100% of subscriptions");
        GeneratorConfig config1 = new GeneratorConfig();
        config1.setTotalSubscriptions(5000);
        config1.getFieldFrequencies().clear();
        config1.getFieldFrequencies().put("company", 100.0);

        testFrequencyScenario(config1, "Scenario 1");

        // Test configuration with custom equality operator frequency
        System.out.println("\nScenario 2: Company field with 90% equality operator");
        GeneratorConfig config2 = new GeneratorConfig();
        config2.setTotalSubscriptions(5000);
        config2.getFieldFrequencies().clear();
        config2.getFieldFrequencies().put("company", 100.0);
        config2.getEqualityOperatorFrequencies().put("company", 90.0);

        testFrequencyScenario(config2, "Scenario 2");
    }

    private static void testFrequencyScenario(GeneratorConfig config, String scenarioName) {
        try {
            DataGenerator generator = new DataGenerator(config, 2);
            DataGenerator.GenerationResult result = generator.generate();

            // Calculate actual frequencies
            int totalSubs = result.getSubscriptions().size();
            Map<String, Integer> fieldCounts = new java.util.HashMap<>();
            Map<String, Integer> equalityCounts = new java.util.HashMap<>();

            for (Subscription sub : result.getSubscriptions()) {
                for (Map.Entry<String, Subscription.FieldCondition> entry :
                        sub.getConditions().entrySet()) {
                    String field = entry.getKey();
                    fieldCounts.put(field, fieldCounts.getOrDefault(field, 0) + 1);

                    if (entry.getValue().getOperator().getSymbol().equals("=")) {
                        equalityCounts.put(field, equalityCounts.getOrDefault(field, 0) + 1);
                    }
                }
            }

            System.out.println("  Target frequencies: " + config.getFieldFrequencies());
            System.out.println("  Actual frequencies:");
            for (Map.Entry<String, Double> target : config.getFieldFrequencies().entrySet()) {
                int actualCount = fieldCounts.getOrDefault(target.getKey(), 0);
                double actualPercent = (double) actualCount / totalSubs * 100;
                System.out.printf("    %s: %.2f%% (target: %.2f%%)%n",
                        target.getKey(), actualPercent, target.getValue());
            }

            // Check equality operator frequencies
            if (!config.getEqualityOperatorFrequencies().isEmpty()) {
                System.out.println("  Equality operator frequencies:");
                for (Map.Entry<String, Double> target :
                        config.getEqualityOperatorFrequencies().entrySet()) {
                    int totalWithField = fieldCounts.getOrDefault(target.getKey(), 0);
                    int equalityCount = equalityCounts.getOrDefault(target.getKey(), 0);
                    if (totalWithField > 0) {
                        double actualPercent = (double) equalityCount / totalWithField * 100;
                        System.out.printf("    %s: %.2f%% (target: %.2f%%)%n",
                                target.getKey(), actualPercent, target.getValue());
                    }
                }
            }

            System.out.println("  ✓ Test completed successfully");

        } catch (Exception e) {
            System.err.println("  ✗ Test failed: " + e.getMessage());
        }
    }
}