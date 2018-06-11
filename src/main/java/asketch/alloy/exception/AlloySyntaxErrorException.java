package asketch.alloy.exception;

/**
 * This class represents exceptions when ASketch cannot make Alloy model (with holes) compile.
 */
public class AlloySyntaxErrorException extends Exception {

  public AlloySyntaxErrorException(String msg) {
    super(msg);
  }
}
