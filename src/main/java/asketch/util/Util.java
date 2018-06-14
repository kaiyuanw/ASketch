package asketch.util;

import asketch.ASketch;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * This class provides some utility methods.
 */
public class Util {

  public static int CLI_USAGE_DESCRIPTION_WIDTH = 1000;

  public static void printASketchUsage(HelpFormatter formatter, Options options) {
    formatter.setOptionComparator(null);
    formatter.printHelp(
        CLI_USAGE_DESCRIPTION_WIDTH,
        ASketch.class.getSimpleName(),
        "Sketch an Alloy model with holes.  "
            + "ASketch encodes holes into a single Alloy meta model and invoke the solver to search for solutions.  "
            + "The solution fragments of holes are reported in the declaring order of the holes.",
        options,
        null,
        true);
  }
}
