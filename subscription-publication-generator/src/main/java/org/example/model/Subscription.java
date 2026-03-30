package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class Subscription {
    private Map<String, FieldCondition> conditions;

    public Subscription() {
        this.conditions = new HashMap<>();
    }

    public void addCondition(String field, FieldOperator operator, Object value) {
        conditions.put(field, new FieldCondition(operator, value));
    }

    public Map<String, FieldCondition> getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Subscription{");
        conditions.forEach((field, condition) ->
                sb.append(String.format("(%s,%s,%s);", field,
                        condition.operator.getSymbol(),
                        condition.value))
        );
        sb.append("}");
        return sb.toString();
    }

    public static class FieldCondition {
        private FieldOperator operator;
        private Object value;

        public FieldCondition(FieldOperator operator, Object value) {
            this.operator = operator;
            this.value = value;
        }

        public FieldOperator getOperator() { return operator; }
        public Object getValue() { return value; }
    }
}