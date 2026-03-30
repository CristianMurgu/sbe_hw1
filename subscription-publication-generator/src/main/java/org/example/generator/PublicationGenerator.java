package org.example.generator;

import org.example.config.GeneratorConfig;
import org.example.model.Publication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PublicationGenerator {
    private final GeneratorConfig config;
    private final Random random;

    public PublicationGenerator(GeneratorConfig config) {
        this.config = config;
        this.random = new Random();
    }

    public Publication generatePublication() {
        String company = config.getCompanyValues()[
                random.nextInt(config.getCompanyValues().length)
                ];

        double value = generateDouble(config.getMinValue(), config.getMaxValue());
        double drop = generateDouble(config.getMinDrop(), config.getMaxDrop());
        double variation = generateDouble(config.getMinVariation(), config.getMaxVariation());
        LocalDate date = generateDate(config.getMinDate(), config.getMaxDate());

        return new Publication(company, value, drop, variation, date);
    }

    public List<Publication> generatePublications(int count) {
        List<Publication> publications = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            publications.add(generatePublication());
        }
        return publications;
    }

    private double generateDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private LocalDate generateDate(LocalDate minDate, LocalDate maxDate) {
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }
}