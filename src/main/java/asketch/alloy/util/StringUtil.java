package asketch.alloy.util;

public class StringUtil {
  public static boolean stringIsOr(String value, String... cands) {
    for (String cand : cands) {
      if (value.equals(cand)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isInt(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
