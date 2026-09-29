# Number Range Summarizer

This is a small system that implements a number range summarizer interface which takes as input, a string with a list of numbers
and summarizes the numbers into a shortened string, with sequential numbers collapsed into hyphen seperated range.

# Author
- Aphelele M. Zenzile


# Set of assumptions made:

- The correct input format is a string with a comma seperated list of integers.
- An interface is stateless (does not have an identity/instances) by design and is therefore a contract, this means that the can be used more like a pipeline and not an object hence I implemented a the interface by simply implementing its method.
- A range is computed only for consecutive numbers that are more that 3. 
- An empty or blank input string results in an empty collection.
- Null input, out of integer range values will throw an IllegalArgumentException
- Output is sorted in ascending order with duplicates removed.
- Negative numbers are supported

# Running the system
- Clone repository: ```git clone <http/ssh url>```
- Compile with ```mvn clean compile```
- Run unit tests: ```mvn clean test``` or ```mvn clean install```

# Requirements

- Java 8 (atleast)
- Maven build system (https://maven.apache.org/install.html)