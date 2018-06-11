package asketch.alloy.exception;

/**
 * This class represents exceptions when unsupported holes are detected.
 */
public class HoleParsingException extends Exception {

  public HoleParsingException(String msg) {
    super(msg);
  }
}
