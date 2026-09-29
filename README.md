# Number Range Summarizer

This small system, implements a number range summarize, which takes as input a string with a list of numbers
and summarizes the numbers into a shortened string.

# author
- Aphelele M. Zenzile


# Set of assumptions made:

- The correct input format is a string with a comma seperated list of integers.
- An interface is stateless (does not have an identity/instances) by design and is therefore a contract, this means that the can be used more like a pipeline and not an object hence I implemented a the interface by simply implementing its method.
- A range is computed only for consecutive numbers that are more that 3. 
- An empty or blank input string results in an empty collection.
- Null input, out of range values will throw an IllegalArgumentException
- Output is sorted in ascending order with duplicates removed.
- Negative numbers are supported

# Running the system
