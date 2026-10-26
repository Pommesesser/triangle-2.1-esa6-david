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
 * Represents a basic tuple of start and finish position.
 * It uses int to represent start and finish.
 * If I had access to a better type system I might consider using an unsigned integer type instead of int.
 * That depends on whether there is arithmetic on the source position tho... (overflow or underflow)
 */
public class SourcePosition {

  public int start, finish;

  /**
   * Initializes both to 0.
   * Questionable sentinel value because 0 kinda carries meaning.
   * Maybe a better approach is avoiding separate declaration and init???
   */
  public SourcePosition() {
    start = 0;
    finish = 0;
  }

  public SourcePosition(int s, int f) {
    start = s;
    finish = f;
  }

  public String toString() {
    return "(" + start + ", " + finish + ")";
  }
}
