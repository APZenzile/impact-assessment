package com.aphelele.numberrangesummarizer;

/**
 * SummarizeCollectionTest
 * -----------------------
 * 
 * Contains a suite of test cases that verifies the correctness and robustness
 * of the method summarizeCollection in the system.
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SummarizeCollectionTest {
    private NumberRangeSummarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new Summarizer();
    }

    private void assertCollected(String input, Integer... expected) {
        assertIterableEquals(Arrays.asList(expected), summarizer.collect(input),
                "Unexpected result for input: '" + input + "'");
    }

    private void assertSummary(String expected, Integer... input) {
        assertEquals(expected, summarizer.summarizeCollection(Arrays.asList(input)),
                "Unexpected summary for: " + Arrays.toString(input));
    }

    /** ------------------------------------------------------------------ */
    /** -------------------- Valid inputs TestSuite ---------------------- */
    /** ------------------------------------------------------------------ */
    @Nested
    @DisplayName("Valid input suite")
    class ValidInput {

        @Test
        @DisplayName("collect: parses the sample input")
        void collectParsesSample() {
            assertCollected("1,3,6,7,8,12,13,14,15,21,22,23,24,31",
                    1, 3, 6, 7, 8, 12, 13, 14, 15, 21, 22, 23, 24, 31);
        }

        @Test
        @DisplayName("collect: ignores whitespace, sorts and removes duplicates")
        void collectNormalisesInput() {
            assertCollected(" 3, 1 ,2, 2 ", 1, 2, 3);
        }

        @Test
        @DisplayName("collect: supports negatives and zero")
        void collectSupportsNegativesAndZero() {
            assertCollected("-3,0,-1", -3, -1, 0);
        }

        @Test
        @DisplayName("summarize: produces the sample output")
        void summarizeSample() {
            assertSummary("1, 3, 6-8, 12-15, 21-24, 31",
                    1, 3, 6, 7, 8, 12, 13, 14, 15, 21, 22, 23, 24, 31);
        }

        @Test
        @DisplayName("summarize: pair stays listed, triple becomes a range (threshold is 3)")
        void summarizeThreshold() {
            assertSummary("1, 2, 4-6", 1, 2, 4, 5, 6);
        }

        @Test
        @DisplayName("summarize: range at the start and at the end (final run is flushed)")
        void summarizeRangesAtBothEnds() {
            assertSummary("1-3, 7, 9-11", 1, 2, 3, 7, 9, 10, 11);
        }

        @Test
        @DisplayName("summarize: no ranges at all")
        void summarizeNoRanges() {
            assertSummary("1, 3, 5", 1, 3, 5);
        }

        @Test
        @DisplayName("summarize: negative ranges and ranges crossing zero")
        void summarizeNegatives() {
            assertSummary("-5, -3--1, 4", -5, -3, -2, -1, 4);
            assertSummary("-3-1", -3, -2, -1, 0, 1);
        }

        @Test
        @DisplayName("collect then summarize works end to end")
        void endToEnd() {
            Collection<Integer> numbers = summarizer.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");

            assertEquals("1, 3, 6-8, 12-15, 21-24, 31", summarizer.summarizeCollection(numbers));
        }
    }

    /** ------------------------------------------------------------------ */
    /** ------------------ Edge Case inputs TestSuite -------------------- */
    /** ------------------------------------------------------------------ */
    @Nested
    @DisplayName("Edge case suite")
    class EdgeCases {

        @ParameterizedTest(name = "[{index}] collect returns empty for blank \"{0}\"")
        @ValueSource(strings = { "", "   ", "\t\n" })
        void collectBlankInputGivesEmpty(String blank) {
            assertTrue(summarizer.collect(blank).isEmpty());
        }

        @Test
        @DisplayName("summarize: empty collection gives an empty string")
        void summarizeEmptyCollection() {
            assertEquals("", summarizer.summarizeCollection(Collections.<Integer>emptyList()));
        }

        @Test
        @DisplayName("single number stays a single number")
        void singleNumber() {
            assertCollected("5", 5);
            assertSummary("5", 5);
        }

        // integer range bounderies detected.
        @Test
        @DisplayName("collect: accepts Integer.MIN_VALUE and MAX_VALUE")
        void collectIntegerBoundaries() {
            assertCollected(Integer.MAX_VALUE + "," + Integer.MIN_VALUE,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("summarize: range ending at MAX_VALUE does not overflow")
        void summarizeRangeAtMaxValue() {
            assertSummary((Integer.MAX_VALUE - 2) + "-" + Integer.MAX_VALUE,
                    Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("summarize: MAX_VALUE followed by MIN_VALUE wrap-around is not a range")
        void summarizeNoWrapAround() {
            assertSummary(Integer.MIN_VALUE + ", " + Integer.MAX_VALUE,
                    Integer.MAX_VALUE, Integer.MIN_VALUE);
        }

        @Test
        @DisplayName("summarize: unsorted input with duplicates is normalised")
        void summarizeUnsortedWithDuplicates() {
            assertSummary("1-3, 8", 8, 3, 1, 2, 2, 1);
        }

        @Test
        @DisplayName("summarize: works with any Collection type (HashSet, unmodifiable list)")
        void summarizeAnyCollectionType() {
            assertEquals("1-3", summarizer.summarizeCollection(new HashSet<>(Arrays.asList(3, 1, 2))));
            assertEquals("1-3", summarizer.summarizeCollection(
                    Collections.unmodifiableList(Arrays.asList(3, 1, 2))));
        }

        @Test
        @DisplayName("summarize: does not modify the caller's collection")
        void summarizeDoesNotMutateInput() {
            List<Integer> input = new ArrayList<>(Arrays.asList(3, 1, 2, 2));

            summarizer.summarizeCollection(input);

            assertEquals(Arrays.asList(3, 1, 2, 2), input);
        }
    }

    /** ------------------------------------------------------------------ */
    /** -------------------- Invalid inputs TestSuite -------------------- */
    /** ------------------------------------------------------------------ */
    @Nested
    @DisplayName("Invalid input suite")
    class InvalidInput {

        @Test
        @DisplayName("collect: rejects null")
        void collectRejectsNull() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(null));
        }

        @ParameterizedTest(name = "[{index}] collect rejects empty token in \"{0}\"")
        @ValueSource(strings = { "1,,3", "1,2,", ",1,2", ",", "1, ,3" })
        void collectRejectsEmptyTokens(String input) {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(input));
        }

        @ParameterizedTest(name = "[{index}] collect rejects non-integer \"{0}\"")
        @ValueSource(strings = { "abc", "1,a,3", "1.5", "1 2", "1;2;3", "1-3", "0x1F", "1e3" })
        void collectRejectsNonIntegers(String input) {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(input));
        }

        @ParameterizedTest(name = "[{index}] collect rejects out-of-range \"{0}\"")
        @ValueSource(strings = { "2147483648", "-2147483649", "1,99999999999999999999" })
        void collectRejectsOutOfRange(String input) {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect(input));
        }

        @Test
        @DisplayName("collect: error names the bad token and keeps the original cause")
        void collectErrorIsDescriptive() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> summarizer.collect("1,banana,3"));

            assertTrue(e.getMessage().contains("banana"));
            assertInstanceOf(NumberFormatException.class, e.getCause());
        }

        @Test
        @DisplayName("collect: one bad token rejects the whole input")
        void collectFailsFast() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,2,3,oops"));
        }

        @Test
        @DisplayName("summarize: rejects a null collection")
        void summarizeRejectsNullCollection() {
            assertThrows(IllegalArgumentException.class, () -> summarizer.summarizeCollection(null));
        }

        @Test
        @DisplayName("summarize: rejects a null element")
        void summarizeRejectsNullElement() {
            assertThrows(IllegalArgumentException.class,
                    () -> summarizer.summarizeCollection(Arrays.asList(1, null, 3)));
        }
    }
}
