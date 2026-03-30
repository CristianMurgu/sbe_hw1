package org.example.generator;

import org.example.config.GeneratorConfig;
import org.example.model.Publication;
import org.example.model.Subscription;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DataGenerator {
    private final GeneratorConfig config;
    private final int parallelism;
    private String outputDir = "generated_data";

    public DataGenerator(GeneratorConfig config, int parallelism) {
        this.config = config;
        this.parallelism = parallelism;
    }

    public DataGenerator(GeneratorConfig config, int parallelism, String outputDir) {
        this.config = config;
        this.parallelism = parallelism;
        this.outputDir = outputDir;
    }

    public GenerationResult generate() throws InterruptedException, ExecutionException, IOException {
        long startTime = System.currentTimeMillis();

        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        ExecutorService executor = Executors.newFixedThreadPool(parallelism);

        List<Publication> publications = generatePublicationsInParallel(executor);

        List<Subscription> subscriptions = generateSubscriptionsInParallel(executor);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        String publicationsFile = outputDir + "/publications_" + timestamp + ".txt";
        String subscriptionsFile = outputDir + "/subscriptions_" + timestamp + ".txt";

        savePublicationsToFile(publications, publicationsFile);
        saveSubscriptionsToFile(subscriptions, subscriptionsFile);

        long endTime = System.currentTimeMillis();

        return new GenerationResult(publications, subscriptions, endTime - startTime,
                publicationsFile, subscriptionsFile);
    }

    private List<Publication> generatePublicationsInParallel(ExecutorService executor)
            throws InterruptedException, ExecutionException {
        int totalCount = config.getTotalPublications();
        int itemsPerThread = totalCount / parallelism;
        int remainder = totalCount % parallelism;

        List<Future<List<Publication>>> futures = new ArrayList<>();

        for (int i = 0; i < parallelism; i++) {
            final int count = itemsPerThread + (i < remainder ? 1 : 0);

            Future<List<Publication>> future = executor.submit(() -> {
                PublicationGenerator pubGen = new PublicationGenerator(config);
                return pubGen.generatePublications(count);
            });
            futures.add(future);
        }

        List<Publication> allPublications = new ArrayList<>();
        for (Future<List<Publication>> future : futures) {
            allPublications.addAll(future.get());
        }

        return allPublications;
    }

    private List<Subscription> generateSubscriptionsInParallel(ExecutorService executor)
            throws InterruptedException, ExecutionException {
        int totalCount = config.getTotalSubscriptions();
        int itemsPerThread = totalCount / parallelism;
        int remainder = totalCount % parallelism;

        List<Future<List<Subscription>>> futures = new ArrayList<>();

        for (int i = 0; i < parallelism; i++) {
            final int count = itemsPerThread + (i < remainder ? 1 : 0);

            Future<List<Subscription>> future = executor.submit(() -> {
                SubscriptionGenerator subGen = new SubscriptionGenerator(config);
                return subGen.generateSubscriptions(count);
            });
            futures.add(future);
        }

        List<Subscription> allSubscriptions = new ArrayList<>();
        for (Future<List<Subscription>> future : futures) {
            allSubscriptions.addAll(future.get());
        }

        return allSubscriptions;
    }

    private void savePublicationsToFile(List<Publication> publications, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            writer.println("# Publications Generated");
            writer.println("# Format: {company,value,drop,variation,date}");
            writer.println("# Total: " + publications.size());
            writer.println("# Generated at: " + LocalDateTime.now());
            writer.println();

            for (Publication pub : publications) {
                writer.println(pub.toString());
            }
        }
        System.out.println("Publications saved to: " + filename);
    }

    private void saveSubscriptionsToFile(List<Subscription> subscriptions, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            writer.println("# Subscriptions Generated");
            writer.println("# Format: {(field,operator,value);...}");
            writer.println("# Total: " + subscriptions.size());
            writer.println("# Generated at: " + LocalDateTime.now());
            writer.println();

            for (Subscription sub : subscriptions) {
                writer.println(sub.toString());
            }
        }
        System.out.println("Subscriptions saved to: " + filename);
    }

    public static class GenerationResult {
        private final List<Publication> publications;
        private final List<Subscription> subscriptions;
        private final long generationTimeMs;
        private final String publicationsFile;
        private final String subscriptionsFile;

        public GenerationResult(List<Publication> publications,
                                List<Subscription> subscriptions,
                                long generationTimeMs,
                                String publicationsFile,
                                String subscriptionsFile) {
            this.publications = publications;
            this.subscriptions = subscriptions;
            this.generationTimeMs = generationTimeMs;
            this.publicationsFile = publicationsFile;
            this.subscriptionsFile = subscriptionsFile;
        }

        public List<Publication> getPublications() { return publications; }
        public List<Subscription> getSubscriptions() { return subscriptions; }
        public long getGenerationTimeMs() { return generationTimeMs; }
        public String getPublicationsFile() { return publicationsFile; }
        public String getSubscriptionsFile() { return subscriptionsFile; }
    }
}