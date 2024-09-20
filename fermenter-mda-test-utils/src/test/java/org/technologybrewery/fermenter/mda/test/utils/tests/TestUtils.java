package org.technologybrewery.fermenter.mda.test.utils.tests;

import io.cucumber.java.ParameterType;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common cucumber parameter types to ease use of in Cucumber 7+.
 */
public class TestUtils {

    /**
     * Creates a Boolean from a String, following standard Boolean.valueOf(String) rules.
     * <p>
     * MUST be used within quotes in your Cucumber annotation: \"{booleanValue}\"
     *
     * @param value string value
     * @return Boolean value
     */
    @ParameterType(value = "[^\"]*")
    public Boolean booleanValue(String value) {
        return Boolean.valueOf(value);
    }

    /**
     * Transforms a data table cell with comma separated values into a `List<String>.
     * <p>
     * MUST be used within quotes in your Cucumber annotation: \"{listOfStrings}\"
     *
     * @param stringOfCommaSeparatedValues values as one field
     * @return list of Strings
     */
    @ParameterType(value = "[^\"]*")
    public List<String> listOfStrings(String stringOfCommaSeparatedValues) {
        return StringUtils.isNotBlank(stringOfCommaSeparatedValues)
            ? Stream.of(stringOfCommaSeparatedValues.split(",", 0)).map(String::trim).collect(Collectors.toList())
            : new ArrayList<>();
    }

    /**
     * Transforms a data table cell with comma separated values into a `List<Integer>`.
     * <p>
     * MUST be used within quotes in your Cucumber annotation: \"{listOfIntegers}\"
     *
     * @param stringOfCommaSeparatedValues values as one field
     * @return list of Integers
     */
    @ParameterType(value = "[^\"]*")
    public List<Integer> listOfIntegers(String stringOfCommaSeparatedValues) {
        return StringUtils.isNotBlank(stringOfCommaSeparatedValues)
            ? Stream.of(stringOfCommaSeparatedValues.split(",", 0)).map(s -> Integer.valueOf(s.trim())).collect(Collectors.toList())
            : new ArrayList<>();
    }

}
