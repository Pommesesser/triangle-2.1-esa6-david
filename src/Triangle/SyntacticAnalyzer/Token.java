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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

enum TokenKind {
  // literals, identifiers, operators...
  INTLITERAL ( 0, "<int>"),
  CHARLITERAL( 1, "<char>"),
  IDENTIFIER ( 2, "<identifier>"),
  OPERATOR   ( 3, "<operator>"),

  // reserved words - must be in alphabetical order...
  ARRAY      ( 4, "array"),
  BEGIN      ( 5, "begin"),
  CONST      ( 6, "const"),
  DO         ( 7, "do"),
  ELSE       ( 8, "else"),
  END        ( 9, "end"),
  FUNC       (10, "func"),
  IF         (11, "if"),
  IN         (12, "in"),
  LET        (13, "let"),
  OF         (14, "of"),
  PROC       (15, "proc"),
  RECORD     (16, "record"),
  THEN       (17, "then"),
  TYPE       (18, "type"),
  VAR        (19, "var"),
  WHILE      (20, "while"),

  // punctuation...
  DOT        (21, "."),
  COLON      (22, ":"),
  SEMICOLON  (23, ";"),
  COMMA      (24, ","),
  BECOMES    (25, ":="),
  IS         (26, "~"),

  // brackets...
  LPAREN     (27, "("),
  RPAREN     (28, ")"),
  LBRACKET   (29, "["),
  RBRACKET   (30, "]"),
  LCURLY     (31, "{"),
  RCURLY     (32, "}"),

  // special tokens...
  EOT        (33, ""),
  ERROR      (34, "<error>");

  final int    id;
  final String spelling;

  TokenKind(int id, String spelling) {
    this.id       = id;
    this.spelling = spelling;
  }

  private static final Map<String, TokenKind> reservedWords;
  static {
    reservedWords = Stream.of(
        ARRAY, BEGIN, CONST, DO, ELSE, END,
        FUNC, IF, IN, LET, OF, PROC, RECORD,
        THEN, TYPE, VAR, WHILE
      ).collect(toMap(t -> t.spelling, identity()));
  }

  static TokenKind getSpecificTokenForIdentifier(String spelling) {
    return reservedWords.containsKey(spelling) ? reservedWords.get(spelling) : IDENTIFIER;
  }

}

final class Token {
  protected TokenKind      kind;
  protected String         spelling;
  protected SourcePosition position;

  public Token(TokenKind kind, String spelling, SourcePosition position) {
    this.kind     = kind == TokenKind.IDENTIFIER ? TokenKind.getSpecificTokenForIdentifier(spelling) : kind; 
    this.spelling = spelling;
    this.position = position;

  }

  public String toString() {
    return "Kind=" + kind.id + ", spelling=" + spelling + ", position=" + position;
  }

}
