package pf.coreutils;

import java.util.HashMap;

public class Maps {
  private static final char NULL_BYTE = 0;
  public static HashMap<Character, String> aaShort;
  public static HashMap<Character, String> ssShort;
  public static HashMap<Character, String> aaThreeCharacter;

  static {
    aaShort = new HashMap<Character, String>();
    aaThreeCharacter = new HashMap<Character, String>();
    ssShort = new HashMap<Character, String>();

    aaShort.put(NULL_BYTE, "Unknown");
    aaShort.put('A', "Alanine");
    aaShort.put('R', "Arginine");
    aaShort.put('N', "Asparagine");
    aaShort.put('D', "Aspartic Acid");
    aaShort.put('B', "Asparagine or Aspartic Acid");
    aaShort.put('C', "Cysteine");
    aaShort.put('E', "Glutamic Acid");
    aaShort.put('Q', "Glutamine");
    aaShort.put('Z', "Glutamine or Glutamic Acid");
    aaShort.put('G', "Glycine");
    aaShort.put('H', "Histidine");
    aaShort.put('I', "Isoleucine");
    aaShort.put('L', "Leucine");
    aaShort.put('K', "Lysine");
    aaShort.put('M', "Methionine");
    aaShort.put('F', "Phenylalanine");
    aaShort.put('P', "Proline");
    aaShort.put('S', "Serine");
    aaShort.put('T', "Threonine");
    aaShort.put('W', "Tryptophan");
    aaShort.put('Y', "Tyrosine");
    aaShort.put('V', "Valine");

    aaThreeCharacter.put('A', "ALA");
    aaThreeCharacter.put('R', "ARG");
    aaThreeCharacter.put('N', "ASN");
    aaThreeCharacter.put('D', "ASP");
    aaThreeCharacter.put('B', "ASX");
    aaThreeCharacter.put('C', "CYS");
    aaThreeCharacter.put('E', "GLU");
    aaThreeCharacter.put('Q', "GLN");
    aaThreeCharacter.put('Z', "GLX");
    aaThreeCharacter.put('G', "GLY");
    aaThreeCharacter.put('H', "HIS");
    aaThreeCharacter.put('I', "ILE");
    aaThreeCharacter.put('L', "LEU");
    aaThreeCharacter.put('K', "LYS");
    aaThreeCharacter.put('M', "MET");
    aaThreeCharacter.put('F', "PHE");
    aaThreeCharacter.put('P', "PRO");
    aaThreeCharacter.put('S', "SER");
    aaThreeCharacter.put('T', "THR");
    aaThreeCharacter.put('W', "TRP");
    aaThreeCharacter.put('Y', "TYR");
    aaThreeCharacter.put('V', "VAL");

    ssShort.put(NULL_BYTE, "Unknown");
    ssShort.put('H', "Helix");
    ssShort.put('E', "Strand");
    ssShort.put('C', "Coil");
  }
}
