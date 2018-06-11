package asketch.util;

import static asketch.opts.DefaultOptions.logger;

/**
 * This class provides some utility methods.
 */
public class Util {

  public static void printASketchUsage() {
    logger.info(
        "ASketchSolve requires: model path, fragment file path, test suite path and number of solutions."
    );
  }
}
