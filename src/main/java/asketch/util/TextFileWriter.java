package asketch.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileWriter {

  public static void writeText(String content, String fname, boolean append) {
    File file = new File(fname);
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(file, append));
      bw.write(content);
    } catch (IOException ex) {
    } finally {
      if (bw != null) {
        try {
          bw.close();
        } catch (IOException ex) {
        }
      }
    }
  }
}
