package asketch.etc;

import java.nio.file.Paths;

public class Names {

  // Suffix
  public final static String DOT_ALS = ".als";

  // Directory paths
  public final static String PROJECT_DIR_PATH = System.getProperty("user.dir");
  public final static String HIDDEN_DIR_PATH = Paths.get(PROJECT_DIR_PATH, ".hidden").toString();

  // File paths
  public final static String CHECK_FILE_PATH = Paths.get(HIDDEN_DIR_PATH, "attempt" + DOT_ALS)
      .toString();
  public final static String TEMP_TEST_PATH = Paths.get(HIDDEN_DIR_PATH, "temp" + DOT_ALS)
      .toString();
  public final static String SOLVE_FILE_PATH = Paths.get(HIDDEN_DIR_PATH, "solve" + DOT_ALS)
      .toString();

  // Special characters
  public final static String SLASH = "/";
  public final static String NEW_LINE = "\n";
  public final static String COMMA = ",";
  public final static String DOT = ".";
  public final static String SPACE = "\\s";
  public final static String UNDERSCORE = "_";
  public final static String VERTICAL_BAR = "|";
  public final static String COLON_EQUAL = ":=";

  // Command line
  public final static String MODEL_PATH = "model-path";
  public final static String FRAGMENT_PATH = "fragment-path";
  public final static String TEST_PATH = "test-path";
  public final static String SCOPE = "scope";
  public final static String SOLUTION_NUM = "sol-num";
}
