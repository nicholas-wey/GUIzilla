package guizilla.sol.client.parser
import sparkzilla.src.Token

/**
 * An exception thrown when there is an unexpected error in the tokenizer
 */
class ParseException(expected: String)
  extends Exception("Error in parsing, expected: " + expected + ".")