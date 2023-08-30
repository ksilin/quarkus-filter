package com.example.filter.utils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MessageFilterHelper {

    private static final String ARRAY_EXP = ".[%s][\'%s\']";
    private static final String CONTAINS_EXP = "[?(%s contains '%s')]";
    private static final String NON_STRING_CONTAINS_EXP = "[?(%s contains %s)]";
    private static final String EQUALS_EXP = "[?(%s == %s)]";
    private static final String WILDCART = "*";
    private static final String FIELD = "[\'%s\']";
    private static final String ROOT_EXP = "$.";

    /**
     * Validate that the message / message argument is valid based on the filterString parameters, return boolean [true]
     * if message is validate successfully otherwise boolean [false] is returned.
     *
     * @param message     - contain json object to validate
     * @param filterString - contains comma-seperated values to filter by i.e. {"transportOrder.receiver.code":["MU807740","MU807732"]}"
     * @return ${{@link Boolean}}
     */
    public static boolean isMessageValid(final String message, final String filterString) {
        if (Objects.isNull(message) || Objects.isNull(filterString))
            return false;

        //todo: regex to validate filter conforms or not i.e. {"transportOrder.receiver.code":["MU807740","MU807732"], "code": ["12345"]}

        final Map<String, Object> filterMap = JsonSerializer.mapper(Map.class, filterString);

        return filterMap.entrySet().stream().allMatch(f -> {
            final String[] keyPath = f.getKey().split("\\.");
            final DocumentContext documentContext = JsonPath.parse(message);
            String filterExpression = ROOT_EXP;
            Object result = null;

            if (keyPath.length > 0) {
                for (int i = 0; i < keyPath.length; i++) {
                    if (filterExpression.equals(ROOT_EXP)) {
                        filterExpression = getFieldExpression(filterExpression, keyPath[i]);
                        result = documentContext.read(filterExpression);
                        continue;
                    }

                    if (i == 1 && result instanceof JSONArray) {
                        filterExpression += String.format(ARRAY_EXP, WILDCART, keyPath[i]);
                    } else {
                        filterExpression = getFieldExpression(filterExpression, keyPath[i]);
                    }
                }

                final List<Object> values = (List) f.getValue();
                final String filterCriteria = filterExpression;
                return values.stream().anyMatch(v -> {
                    final Object resultV = documentContext.read(getJsonPath(filterCriteria, v));
                    return ((JSONArray) resultV).size() > 0;
                });
            }
            return false;
        });
    }

    private static String getJsonPath(String filterCriteria, Object v) {
        final String jsonPath;
        if (!(v instanceof String)) {
            if (filterCriteria.contains(WILDCART)) {
                jsonPath = String.format(NON_STRING_CONTAINS_EXP, filterCriteria, v);
            } else {
                jsonPath = String.format(EQUALS_EXP, filterCriteria, v);
            }
        } else {
            jsonPath = String.format(CONTAINS_EXP, filterCriteria, v);
        }
        return jsonPath;
    }

    private static String getFieldExpression(String filter, String valuePath) {
        filter += String.format(FIELD, valuePath);
        return filter;
    }
}

