package asketch.opts;

import asketch.util.Logger;

public class DefaultOptions {

  public static Logger logger = new Logger(Logger.INFO);

  // The scope to solve concrete values for holes.
  public static int solvingScope = 3;
}
