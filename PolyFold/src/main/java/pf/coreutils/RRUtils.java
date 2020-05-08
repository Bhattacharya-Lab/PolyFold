package pf.coreutils;

import java.io.*;
import java.util.HashSet;

import pf.coreutils.Scoring;

public class RRUtils {
  public static double[][] expectedDistance;
  public static String aminoSequence;
  public static String secondarySequence;
  public static int sequenceLen;

  public static int parseRR(File f) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(f));
    aminoSequence = br.readLine();
    // Do not work on files with no sequence
    if (aminoSequence == null || aminoSequence.length() == 0) return 1;
    aminoSequence = aminoSequence.trim();
    if (aminoSequence.length() > 500) return 2;
    secondarySequence = br.readLine();
    if (secondarySequence != null) secondarySequence = secondarySequence.trim();
    if (secondarySequence.length() != aminoSequence.length()) return 1;
    // Side length of matrix
    sequenceLen = aminoSequence.length();
    // We expect summation of N-1 pairs to be present (e.g. half of the matrix excluding the main
    // diagonal or (N * (N-1)) / 2)
    int expectedSize = (sequenceLen) * (sequenceLen-1) / 2;
    int actualSize = 0;
    HashSet<String> pairs = new HashSet<>();
    expectedDistance = new double[sequenceLen][sequenceLen];
    Scoring.contactTable = new double[sequenceLen][sequenceLen][2];
    String currentLine = br.readLine();
    while (currentLine != null) {
      String[] data = currentLine.split(" ");
      int residue1 = Integer.parseInt(data[0]) - 1;
      int residue2 = Integer.parseInt(data[1]) - 1;
      String pairKey = data[0] + " " + data[1];
      if (residue1 < residue2 && !pairs.contains(pairKey)) {
        actualSize++;
        double floor = Double.parseDouble(data[2]);
        double ceil = Double.parseDouble(data[3]);
        // floor distance
        Scoring.contactTable[residue1][residue2][0] = floor;
        Scoring.contactTable[residue2][residue1][0] = floor;
        // celing distance
        Scoring.contactTable[residue1][residue2][1] = ceil;
        Scoring.contactTable[residue2][residue1][1] = ceil;
        expectedDistance[residue1][residue2] = (floor + ceil) / 2.0;
        expectedDistance[residue2][residue1] = (floor + ceil) / 2.0;
      }
      currentLine = br.readLine();
    }
    if (actualSize != expectedSize) return 1;
    br.close();
    return 0;
  }
}
