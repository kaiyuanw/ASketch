package asketch.alloy.exception;

/**
 * This class represents the exception when some user given test valuation is unsatisfiable for the
 * model.
 */
public class TestValuationUnsatisfiableException extends Exception {

  public TestValuationUnsatisfiableException(String msg) {
    super(msg);
  }
}
