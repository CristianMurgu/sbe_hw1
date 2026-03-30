package org.example;

import org.example.config.GeneratorConfig;
import org.example.generator.DataGenerator;
import org.example.model.Publication;
import org.example.model.Subscription;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            GeneratorConfig config = new GeneratorConfig();
            config.setTotalPublications(100000);
            config.setTotalSubscriptions(100000);

            config.getFieldFrequencies().put("company", 90.0);
            config.getFieldFrequencies().put("value", 80.0);
            config.getFieldFrequencies().put("variation", 70.0);

            config.getEqualityOperatorFrequencies().put("company", 70.0);

            int[] parallelismLevels = {1, 4};

            for (int parallelism : parallelismLevels) {
                System.out.println("\n=== Testing with parallelism = " + parallelism + " ===");
                System.out.println("CPU Cores available: " + Runtime.getRuntime().availableProcessors());
                System.out.println("Max memory: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");

                DataGenerator generator = new DataGenerator(config, parallelism);
                DataGenerator.GenerationResult result = generator.generate();

                verifyStatistics(result.getSubscriptions(), config);

                System.out.println("Generated " + result.getPublications().size() + " publications");
                System.out.println("Generated " + result.getSubscriptions().size() + " subscriptions");
                System.out.println("Generation time: " + result.getGenerationTimeMs() + " ms");
                System.out.println("Publications file: " + result.getPublicationsFile());
                System.out.println("Subscriptions file: " + result.getSubscriptionsFile());

                printSamples(result.getPublications(), result.getSubscriptions());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void verifyStatistics(List<Subscription> subscriptions, GeneratorConfig config) {
        int totalWithCompany = 0;
        int totalWithCompanyEquality = 0;

        for (Subscription sub : subscriptions) {
            if (sub.getConditions().containsKey("company")) {
                totalWithCompany++;
                if (sub.getConditions().get("company").getOperator().getSymbol().equals("=")) {
                    totalWithCompanyEquality++;
                }
            }
        }

        double companyFrequency = (double) totalWithCompany / subscriptions.size() * 100;
        double equalityFrequency = (double) totalWithCompanyEquality / totalWithCompany * 100;

        System.out.println("\nStatistics verification:");
        System.out.printf("Company field frequency: %.2f%% (target: %.2f%%)\n",
                companyFrequency, config.getFieldFrequencies().get("company"));
        System.out.printf("Company equality operator frequency: %.2f%% (target: %.2f%%)\n",
                equalityFrequency, config.getEqualityOperatorFrequencies().get("company"));

        double tolerance = 2.0;
        if (Math.abs(companyFrequency - config.getFieldFrequencies().get("company")) <= tolerance) {
            System.out.println("✓ Field frequency within acceptable tolerance");
        } else {
            System.out.println("⚠ Field frequency outside tolerance");
        }

        if (Math.abs(equalityFrequency - config.getEqualityOperatorFrequencies().get("company")) <= tolerance) {
            System.out.println("✓ Equality operator frequency within acceptable tolerance");
        } else {
            System.out.println("⚠ Equality operator frequency outside tolerance");
        }
    }

    private static void printSamples(List<Publication> publications, List<Subscription> subscriptions) {
        System.out.println("\nSample publications (first 3):");
        publications.stream().limit(3).forEach(System.out::println);

        System.out.println("\nSample subscriptions (first 3):");
        subscriptions.stream().limit(3).forEach(System.out::println);
    }
}