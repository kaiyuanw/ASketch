package asketch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextFileReader {

  public static String readText(String fpath) {
    File file = new File(fpath);
    BufferedReader reader = null;
    StringBuilder sb = new StringBuilder();
    String tmp;
    try {
      reader = new BufferedReader(new FileReader(file));
      while ((tmp = reader.readLine()) != null) {
        sb.append(tmp);
        sb.append("\n");
      }
    } catch (IOException ex) {
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
      }
    }
    return new String(sb);
  }
}
