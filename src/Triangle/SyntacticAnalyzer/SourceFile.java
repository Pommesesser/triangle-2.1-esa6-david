/*
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * 
 * Changes: Copyright (C) 2016 J. Oppermann and A. Koch
 * Embedded Systems and Applications Group
 * Department of Computer Science, Technische Universitaet Darmstadt,
 * Hochschulstr. 10, 64289 Darmstadt, Germany.
 * 
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package Triangle.SyntacticAnalyzer;

/**
 * Reads a Triangle source file one character at a time and tracks the
 * current line number.
 *
 * <p>If the file cannot be opened, the constructor does not throw;
 * instead {@link #getCurrentLine()} returns {@code 0}. Callers must check
 * for this before calling {@link #getSource()}.
 */
public class SourceFile {

  public static final char EOL = '\n';
  public static final char EOT = '\u0000';

  java.io.File sourceFile;
  java.io.FileInputStream source;
  int currentLine;

  /**
   * Opens the given file for reading.
   *
   * <p>
   *     I DESPISE THIS STYLE OF ERROR HANDLING
   * </p>
   *
   * @param filename path of the source file
   */
  public SourceFile(String filename) {
    try {
      sourceFile = new java.io.File(filename);
      source = new java.io.FileInputStream(sourceFile);
      currentLine = 1;
    }
    catch (java.io.IOException s) {
      sourceFile = null;
      source = null;
      currentLine = 0;
    }
  }

  /**
   * Reads the next character and advances the line counter on {@link #EOL}.
   *
   * @return the next character, or {@link #EOT} at end of file or on an I/O error
   * @throws NullPointerException if the file failed to open
   */
  char getSource() {
    try {
      int c = source.read();

      if (c == - 1) {
        c = EOT;
      }
      else if (c == EOL) {
        currentLine++;
      }
      return (char) c;
    }
    catch (java.io.IOException s) {
      return EOT;
    }
  }

  /**
   * @return the 1-based number of the line being read, or {@code 0} if the
   *         file failed to open
   */
  public int getCurrentLine() {
    return currentLine;
  }
}
