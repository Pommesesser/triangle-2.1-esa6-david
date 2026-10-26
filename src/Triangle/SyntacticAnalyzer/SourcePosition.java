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
 * The range of source lines a token or AST node covers.
 *
 * <p>Both fields are 1-based line numbers. {@code (0, 0)} means
 * "no position".
 */
// TODO: stop creating a (0, 0) placeholder and filling it in later.
//  The Parser does `pos = new SourcePosition(); start(pos); ... finish(pos);`,
//  so a half-initialized position exists in between, and in loops (e.g.
//  parseCommand) the same object is mutated and shared by several AST nodes,
//  so earlier nodes see later finish values. Construct positions once with
//  both ends known, e.g. new SourcePosition(startLine, endLine).
public class SourcePosition {

  /** First and last line of the range. */
  public int start, finish;

  /** Creates the "no position" value {@code (0, 0)}. */
  public SourcePosition() {
    start = 0;
    finish = 0;
  }

  /**
   * @param s first line of the range
   * @param f last line of the range
   */
  public SourcePosition(int s, int f) {
    start = s;
    finish = f;
  }

  public String toString() {
    return "(" + start + ", " + finish + ")";
  }
}
