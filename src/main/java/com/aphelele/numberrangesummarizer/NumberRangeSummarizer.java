package com.aphelele.numberrangesummarizer;

import java.util.Collection;

/**
 * @author Werner
 *
 *         Implement this Interface to produce a comma delimited list of
 *         numbers,
 *         grouping the numbers into a range when they are sequential.
 *
 *
 *         Sample Input: "1,3,6,7,8,12,13,14,15,21,22,23,24,31
 *         Result: "1, 3, 6-8, 12-15, 21-24, 31"
 *
 *         The code will be evaluated on
 *         - functionality
 *         - style
 *         - robustness
 *         - best practices
 *         - unit tests
 */
public interface NumberRangeSummarizer {

    /**
     * @brief Takes an input string of comma seperated integers (hopefully)
     *        and parses it into a java collection.
     * 
     * @param input the string of integers.
     * 
     * @return java collection of integers.
     */
    Collection<Integer> collect(String input);

    /**
     * @brief Produces a summerised version of the string of integers and returns
     *        it.
     * 
     * @param input a collection of the numbers.
     * 
     * @return returns a summerised string.
     */
    String summarizeCollection(Collection<Integer> input);

}
