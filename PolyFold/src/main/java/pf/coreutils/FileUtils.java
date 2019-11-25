/* ***************************** PACKAGE ************************************ */
package pf.coreutils;
/* ************************** JAVA IMPORTS ********************************** */
import java.io.File;

/* File utility for naming PolyFold window */
public class FileUtils {
  /* Get the extensionless base name of a file */
  public static String getBaseName(File f) {
    String fileName = f.getName();
    int extensionIndex = fileName.lastIndexOf(".");
    if (extensionIndex == -1) return fileName;
    return fileName.substring(0, extensionIndex);
  }
}
