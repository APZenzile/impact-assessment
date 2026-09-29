package com.aphelele.numberrangesummarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class Summarizer implements NumberRangeSummarizer {

    private final String DELIMETER = ",";
    private static final String ITEM_SEPARATOR = ", ";
    private static final String RANGE_SEPARATOR = "-";
    private static final int MIN_RANGE_LENGTH = 3;

    @Override
    public Collection<Integer> collect(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }

        // an empty input string gets an empty collection as an output.
        if (input.trim().isEmpty()) {
            return new TreeSet<>();
        }

        // converts the string into an array of tokens (seperated by a comma)
        String[] tokens = input.split(DELIMETER, -1);
        // the collection of numbers to be returned.
        Collection<Integer> numbers = new TreeSet<>();

        for (String token : tokens) {
            // gets whitespaces off the single token.
            String trimmedToken = token.trim();
            int number = parseSingleToken(trimmedToken);
            numbers.add(number);
        }

        return numbers;
    }

    @Override
    public String summarizeCollection(Collection<Integer> input) {
        validate(input);

        /** converts the collection into a list of integers. */
        List<Integer> numbers = new ArrayList<>(new TreeSet<>(input));

        /** empty collection gets an empty string as an output. */
        if (numbers.isEmpty()) {
            return "";
        }

        List<String> parts = new ArrayList<>();
        /** start of the considered range. */
        int rangeStart = numbers.get(0);
        /** end of the considered range. */
        int rangeEnd = rangeStart;

        /** loops through the numbers to translate them into a summary. */
        for (int i = 1; i < numbers.size(); i++) {
            int current = numbers.get(i);

            if ((long) current == (long) rangeEnd + 1) {
                rangeEnd = current;
            } else {
                addRange(parts, rangeStart, rangeEnd);
                rangeStart = current;
                rangeEnd = current;
            }
        }

        addRange(parts, rangeStart, rangeEnd);

        return String.join(ITEM_SEPARATOR, parts);
    }

    /** ---------------------------------------------------- */
    /** -------------------- HELPERS ----------------------- */
    /** ---------------------------------------------------- */

    /**
     * @brief parses a single value from the input string, checks its validity and
     *        parses it into an integer.
     * 
     * @param token the single input token from the string.
     * 
     * @return the token as an integers
     */
    private int parseSingleToken(String token) {
        // raises an exception for empty tokens, e.g ,,
        if (token.isEmpty()) {
            throw new IllegalArgumentException(
                    "Input contains an empty value");
        }

        // parses the token to an integer.
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a valid integer: '" + token + "'", e);
        }
    }

    private void validate(Collection<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input collection must not be null");
        }
        for (Integer number : input) {
            if (number == null) {
                throw new IllegalArgumentException("Input collection must not contain null elements");
            }
        }
    }

    /**
     * @brief Formats the range and adds it into a list of strings which acting as
     *        the summary string.
     * 
     * @param parts the string that acts as a summary of the list.
     * 
     * @param start the start of the range of numbers considered
     * @param end   the end of the range of numbers considered
     */
    private void addRange(List<String> parts, int start, int end) {
        // computes the length of the range.
        long length = ((long) end - start) + 1;

        if (length >= MIN_RANGE_LENGTH) {
            // add the numbers as a range seperated by a hyphen.
            parts.add(start + RANGE_SEPARATOR + end);
        } else {
            // add the numbers as a list of numbers.
            parts.add(String.valueOf(start));
            if (end != start) {
                parts.add(String.valueOf(end));
            }
        }
    }
}
