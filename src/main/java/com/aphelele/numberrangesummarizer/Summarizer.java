package com.aphelele.numberrangesummarizer;

import java.util.Collection;
import java.util.TreeSet;

public class Summarizer implements NumberRangeSummarizer {

    private final String DELIMETER = ",";

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'summarizeCollection'");
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
}
