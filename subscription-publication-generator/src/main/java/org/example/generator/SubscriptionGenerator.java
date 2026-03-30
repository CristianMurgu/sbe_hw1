package org.example.generator;

import org.example.config.GeneratorConfig;
import org.example.model.FieldOperator;
import org.example.model.Subscription;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriptionGenerator {
    private final GeneratorConfig config;
    private final Random random;
    private final Map<String, AtomicInteger> fieldCounters;
    private final Map<String, AtomicInteger> equalityCounters;
    private int totalGenerated;

    public SubscriptionGenerator(GeneratorConfig config) {
        this.config = config;
        this.random = new Random();
        this.fieldCounters = new HashMap<>();
        this.equalityCounters = new HashMap<>();
        this.totalGenerated = 0;

        // Initialize counters for precise frequency control
        for (String field : config.getFieldFrequencies().keySet()) {
            fieldCounters.put(field, new AtomicInteger(0));
            equalityCounters.put(field, new AtomicInteger(0));
        }
    }

    public Subscription generateSubscription() {
        Subscription subscription = new Subscription();
        totalGenerated++;

        for (Map.Entry<String, Double> entry : config.getFieldFrequencies().entrySet()) {
            String field = entry.getKey();
            double targetFrequency = entry.getValue();

            int currentCount = fieldCounters.get(field).get();
            double currentFrequency = (double) currentCount / totalGenerated * 100;

            boolean shouldInclude = currentFrequency < targetFrequency;

            if (totalGenerated <= 100) {
                shouldInclude = random.nextDouble() * 100 < targetFrequency;
            }

            if (shouldInclude) {
                fieldCounters.get(field).incrementAndGet();

                FieldOperator operator = selectOperatorWithPrecision(field);
                Object value = generateValueForField(field, operator);
                subscription.addCondition(field, operator, value);
            }
        }

        return subscription;
    }

    private FieldOperator selectOperatorWithPrecision(String field) {
        Double equalityTarget = config.getEqualityOperatorFrequencies().get(field);

        if (equalityTarget != null) {
            int totalWithField = fieldCounters.get(field).get();
            int currentEquality = equalityCounters.get(field).get();

            if (totalWithField > 0) {
                double currentEqualityPercent = (double) currentEquality / totalWithField * 100;

                if (currentEqualityPercent < equalityTarget) {
                    equalityCounters.get(field).incrementAndGet();
                    return FieldOperator.EQUAL;
                }
            } else {
                if (random.nextDouble() * 100 < equalityTarget) {
                    equalityCounters.get(field).incrementAndGet();
                    return FieldOperator.EQUAL;
                }
            }
        }

        FieldOperator[] operators = {FieldOperator.GREATER_THAN,
                FieldOperator.GREATER_THAN_OR_EQUAL,
                FieldOperator.LESS_THAN,
                FieldOperator.LESS_THAN_OR_EQUAL};
        return operators[random.nextInt(operators.length)];
    }

    public List<Subscription> generateSubscriptions(int count) {
        List<Subscription> subscriptions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            subscriptions.add(generateSubscription());
        }
        return subscriptions;
    }

    private FieldOperator selectOperatorForField(String field) {
        Double equalityFrequency = config.getEqualityOperatorFrequencies().get(field);

        if (equalityFrequency != null && random.nextDouble() * 100 < equalityFrequency) {
            return FieldOperator.EQUAL;
        }

        FieldOperator[] operators = FieldOperator.values();
        return operators[random.nextInt(operators.length)];
    }

    private Object generateValueForField(String field, FieldOperator operator) {
        switch (field) {
            case "company":
                String[] companies = config.getCompanyValues();
                return companies[random.nextInt(companies.length)];

            case "value":
                return generateDoubleValue(operator, config.getMinValue(), config.getMaxValue());

            case "drop":
                return generateDoubleValue(operator, config.getMinDrop(), config.getMaxDrop());

            case "variation":
                return generateDoubleValue(operator, config.getMinVariation(), config.getMaxVariation());

            case "date":
                return generateDateValue(operator, config.getMinDate(), config.getMaxDate());

            default:
                return null;
        }
    }

    private double generateDoubleValue(FieldOperator operator, double min, double max) {
        double value = min + (max - min) * random.nextDouble();

        switch (operator) {
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
                return min + (max - min) * (0.3 + 0.7 * random.nextDouble());
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
                return min + (max - min) * (0.0 + 0.7 * random.nextDouble());
            default:
                return value;
        }
    }

    private LocalDate generateDateValue(FieldOperator operator, LocalDate minDate, LocalDate maxDate) {
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay;

        switch (operator) {
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL:
                randomDay = ThreadLocalRandom.current().nextLong(
                        minDay + (maxDay - minDay) / 3, maxDay + 1
                );
                break;
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL:
                randomDay = ThreadLocalRandom.current().nextLong(
                        minDay, minDay + (maxDay - minDay) * 2 / 3 + 1
                );
                break;
            default:
                randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay + 1);
        }

        return LocalDate.ofEpochDay(randomDay);
    }
}