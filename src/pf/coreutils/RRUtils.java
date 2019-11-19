package pf.coreutils;

import java.io.*;

import pf.coreutils.Scoring;

public class RRUtils {
  public static double[][] expectedDistance;
  public static String aminoSequence;
  public static String secondarySequence;
  public static int sequenceLen;

  public static boolean parseRR(File f) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(f));
    aminoSequence = br.readLine();
    // Do not work on files with no sequence
    if (aminoSequence == null || aminoSequence.length() == 0) return false;
    secondarySequence = br.readLine();
    // Trim sequences
    aminoSequence = aminoSequence.trim();
    if (secondarySequence != null) secondarySequence = secondarySequence.trim();
    // Side length of matrix
    sequenceLen = aminoSequence.length();
    expectedDistance = new double[sequenceLen][sequenceLen];
    Scoring.contactTable = new double[sequenceLen][sequenceLen][3];
    String currentLine = br.readLine();
    while (currentLine != null) {
      String[] data = currentLine.split(" ");
      int residue1 = Integer.parseInt(data[0]) - 1;
      int residue2 = Integer.parseInt(data[1]) - 1;
      // floor distance
      Scoring.contactTable[residue1][residue2][0] = Double.parseDouble(data[2]);
      // celing distance
      Scoring.contactTable[residue1][residue2][1] = Double.parseDouble(data[3]);
      // difference between floor and ceiling
      Scoring.contactTable[residue1][residue2][2] = Double.parseDouble(data[4]);
      expectedDistance[residue1][residue2] = (
          (Double.parseDouble(data[2]) + Double.parseDouble(data[3])) / 2.0);
      currentLine = br.readLine();
    }
    br.close();
    return true;
  }
}
