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
}
