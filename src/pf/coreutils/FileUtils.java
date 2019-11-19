package pf.coreutils;

import java.io.File;

public class FileUtils {
  public static String getBaseName(File f) {
    String fileName = f.getName();
    int extensionIndex = fileName.lastIndexOf(".");
    if (extensionIndex == -1) return fileName;
    return fileName.substring(0, extensionIndex);
  }
}
