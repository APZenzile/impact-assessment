package com.aphelele.numberrangesummarizer;

/**
 * CollectTest
 * -----------
 * 
 * Contains a suite of test cases that verifies the correctness and robustness
 * of the method Collect in the system.
 * 
 */

/** -------------- Asserttions --------------- */
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CollectTest {
    private Summarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new Summarizer();
    }

    private void assertCollected(String input, Integer... expected) {
        Collection<Integer> actual = summarizer.collect(input);
        assertIterableEquals(Arrays.asList(expected), actual,
                "Unexpected result for input: '" + input + "'");
    }

    /** ------------------------------------------------------------------ */
    /** -------------------- Valid inputs TestSuite ---------------------- */
    /** ------------------------------------------------------------------ */
    @Nested
    @DisplayName("Valid input")
    class ValidInput {

        @Test
        @DisplayName("parses the sample input from the interface Javadoc")
        void parsesSampleInput() {
            assertCollected("1,3,6,7,8,12,13,14,15,21,22,23,24,31",
                    1, 3, 6, 7, 8, 12, 13, 14, 15, 21, 22, 23, 24, 31);
        }

        @Test
        @DisplayName("parses a single number")
        void parsesSingleNumber() {
            assertCollected("5", 5);
        }

        @Test
        @DisplayName("ignores whitespace around tokens")
        void ignoresSurroundingWhitespace() {
            assertCollected("  1 ,\t3 , 6  ", 1, 3, 6);
        }

        @Test
        @DisplayName("accepts the format ', ' as input")
        void acceptsCommaSpaceSeparator() {
            assertCollected("1, 3, 6, 7, 8", 1, 3, 6, 7, 8);
        }

        @Test
        @DisplayName("sorts unsorted input ascending")
        void sortsUnsortedInput() {
            assertCollected("3,1,2", 1, 2, 3);
        }

        @Test
        @DisplayName("removes duplicates")
        void removesDuplicates() {
            assertCollected("1,1,2,2,2,3", 1, 2, 3);
        }

        @Test
        @DisplayName("supports negative numbers and zero")
        void supportsNegativesAndZero() {
            assertCollected("-3,-1,0,2", -3, -1, 0, 2);
        }

        @Test
        @DisplayName("supports Integer.MIN_VALUE and Integer.MAX_VALUE")
        void supportsIntegerBoundaries() {
            assertCollected(Integer.MAX_VALUE + "," + Integer.MIN_VALUE,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("accepts an explicit leading plus sign (Integer.parseInt behaviour)")
        void acceptsLeadingPlusSign() {
            assertCollected("+5,7", 5, 7);
        }

        @Test
        @DisplayName("accepts leading zeros")
        void acceptsLeadingZeros() {
            assertCollected("007,08", 7, 8);
        }
    }

    /** ------------------------------------------------------------------ */
    /** ------------------- Blank inputs Test suite ---------------------- */
    /** ------------------------------------------------------------------ */
    @Nested
    @DisplayName("Blank input")
    class BlankInput {

        @ParameterizedTest(name = "[{index}] returns empty collection for \"{0}\"")
        @ValueSource(strings = { "", " ", "     ", "\t", "\n", " \t\n " })
        void returnsEmptyCollection(String blank) {
            Collection<Integer> result = summarizer.collect(blank);

            assertTrue(result.isEmpty(), "Blank input should produce an empty collection");
        }
    }

    /** ------------------------------------------------------------------ */
    /** -------------------- Invalid input Test suite ------------------- */
    /** ------------------------------------------------------------------ */
    @Nested
    @DisplayName("Invalid input")
    class InvalidInput {

        @Test
        @DisplayName("rejects null input")
        void rejectsNull() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(null));
        }

        @ParameterizedTest(name = "[{index}] rejects empty token in \"{0}\"")
        @ValueSource(strings = {
                "1,,3", // doubled comma
                "1,2,", // trailing comma
                ",1,2", // leading comma
                ",", // only a delimiter
                ",,", // only delimiters
                "1, ,3", // whitespace-only token
                "1,2, " // trailing comma followed by whitespace
        })
        void rejectsEmptyTokens(String input) {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(input));
        }

        @ParameterizedTest(name = "[{index}] rejects non-integer token in \"{0}\"")
        @ValueSource(strings = {
                "abc",
                "1,a,3",
                "1.5", // decimal
                "1,2.0,3",
                "1 2", // missing delimiter, internal whitespace
                "1;2;3", // wrong delimiter
                "1-3", // range syntax is on input.
                "--5",
                "5-",
                "0x1F", // hex format.
                "1e3", // scientific notation format.
                "1_000", // underscore separators
                "one,two" // written number format.
        })
        void rejectsNonIntegerTokens(String input) {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(input));
        }

        @ParameterizedTest(name = "[{index}] rejects out-of-range value \"{0}\"")
        @ValueSource(strings = {
                "2147483648", // Integer.MAX_VALUE + 1 (overflow)
                "-2147483649", // Integer.MIN_VALUE - 1 (overflow)
                "99999999999999999999",
                "1,2147483648"
        })
        void rejectsOutOfRangeValues(String input) {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(input));
        }

        @Test
        @DisplayName("error message names the offending token")
        void errorMessageNamesOffendingToken() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> summarizer.collect("1,banana,3"));

            assertTrue(e.getMessage().contains("banana"),
                    "Message should contain the bad token but was: " + e.getMessage());
        }

        @Test
        @DisplayName("preserves the NumberFormatException as the cause")
        void preservesCause() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> summarizer.collect("x"));

            assertInstanceOf(NumberFormatException.class, e.getCause());
        }

        @Test
        @DisplayName("a single bad token rejects the whole input (no partial results)")
        void failsFastOnAnyBadToken() {
            assertThrows(IllegalArgumentException.class,
                    () -> summarizer.collect("1,2,3,4,5,oops"));
        }
    }

    /** ----------------------------------------------------------------- */
    /** ------------- Behavioural guarantees ---------------------------- */
    /** ----------------------------------------------------------------- */
    @Nested
    @DisplayName("Behavioural guarantees")
    class Guarantees {

        @Test
        @DisplayName("each call returns an independent collection (stateless)")
        void returnsIndependentCollections() {
            Collection<Integer> first = summarizer.collect("1,2,3");
            Collection<Integer> second = summarizer.collect("1,2,3");

            assertNotSame(first, second);

            first.add(99);
            assertEquals(3, second.size(), "Mutating one result must not affect another");
        }

        @Test
        @DisplayName("a failed call does not affect later calls")
        void failureDoesNotLeaveState() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,a"));

            assertCollected("4,5", 4, 5);
        }

        @Test
        @DisplayName("handles a large input")
        void handlesLargeInput() {
            StringBuilder sb = new StringBuilder();
            for (int i = 10_000; i >= 1; i--) {
                sb.append(i).append(i > 1 ? "," : "");
            }

            Collection<Integer> result = summarizer.collect(sb.toString());

            assertEquals(10_000, result.size());
            assertEquals(Integer.valueOf(1), result.iterator().next());
        }
    }

}
