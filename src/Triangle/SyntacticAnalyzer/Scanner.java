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

import static Triangle.SyntacticAnalyzer.TokenKind.*;

public final class Scanner {

  private SourceFile sourceFile;
  private boolean debug;

  private char currentChar;
  private StringBuilder currentSpelling;
  private boolean currentlyScanningToken;

  private boolean isLetter(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }

  // isOperator returns true iff the given character is an operator character.

  private boolean isOperator(char c) {
    return (c == '+' || c == '-' || c == '*' || c == '/'
        || c == '=' || c == '<' || c == '>' || c == '\\'
        || c == '&' || c == '@' || c == '%' || c == '^'
        || c == '?');
  }

  ///////////////////////////////////////////////////////////////////////////////

  public Scanner(SourceFile source) {
    sourceFile = source;
    currentChar = sourceFile.getSource();
    debug = false;
  }

  public void enableDebugging() {
    debug = true;
  }

  // takeIt appends the current character to the current token, and gets
  // the next character from the source program.

  private void takeIt() {
    if (currentlyScanningToken)
      currentSpelling.append(currentChar);
    currentChar = sourceFile.getSource();
  }

  // scanSeparator skips a single separator.

  private void scanSeparator() {
    switch (currentChar) {
    case '!': {
      takeIt();
      while ((currentChar != SourceFile.EOL) && (currentChar != SourceFile.EOT))
        takeIt();
      if (currentChar == SourceFile.EOL)
        takeIt();
    }
      break;

    case ' ': case '\n': case '\r': case '\t':
      takeIt();
      break;
    }
  }

  private TokenKind scanToken() {

    switch (currentChar) {

    case 'a':  case 'b':  case 'c':  case 'd':  case 'e':
    case 'f':  case 'g':  case 'h':  case 'i':  case 'j':
    case 'k':  case 'l':  case 'm':  case 'n':  case 'o':
    case 'p':  case 'q':  case 'r':  case 's':  case 't':
    case 'u':  case 'v':  case 'w':  case 'x':  case 'y':
    case 'z':
    case 'A':  case 'B':  case 'C':  case 'D':  case 'E':
    case 'F':  case 'G':  case 'H':  case 'I':  case 'J':
    case 'K':  case 'L':  case 'M':  case 'N':  case 'O':
    case 'P':  case 'Q':  case 'R':  case 'S':  case 'T':
    case 'U':  case 'V':  case 'W':  case 'X':  case 'Y':
    case 'Z':
      takeIt();
      while (isLetter(currentChar) || isDigit(currentChar))
        takeIt();
      return IDENTIFIER;

    case '0':  case '1':  case '2':  case '3':  case '4':
    case '5':  case '6':  case '7':  case '8':  case '9':
      takeIt();
      while (isDigit(currentChar))
        takeIt();
      return INTLITERAL;

    case '+':  case '-':  case '*': case '/':  case '=':
    case '<':  case '>':  case '\\':  case '&':  case '@':
    case '%':  case '^':  case '?':
      takeIt();
      while (isOperator(currentChar))
        takeIt();
      return OPERATOR;

    case '\'':
      takeIt();
      takeIt(); // the quoted character
      if (currentChar == '\'') {
        takeIt();
        return CHARLITERAL;
      }
      else
        return ERROR;

    case '.':
      takeIt();
      return DOT;

    case ':':
      takeIt();
      if (currentChar == '=') {
        takeIt();
        return BECOMES;
      }
      else
        return COLON;

    case ';':
      takeIt();
      return SEMICOLON;

    case ',':
      takeIt();
      return COMMA;

    case '~':
      takeIt();
      return IS;

    case '(':
      takeIt();
      return LPAREN;

    case ')':
      takeIt();
      return RPAREN;

    case '[':
      takeIt();
      return LBRACKET;

    case ']':
      takeIt();
      return RBRACKET;

    case '{':
      takeIt();
      return LCURLY;

    case '}':
      takeIt();
      return RCURLY;

    case SourceFile.EOT:
      return EOT;

    default:
      takeIt();
      return ERROR;
    }
  }

  public Token scan() {
    Token tok;
    SourcePosition pos;
    TokenKind kind;

    currentlyScanningToken = false;
    while (currentChar == '!'
        || currentChar == ' '
        || currentChar == '\n'
        || currentChar == '\r'
        || currentChar == '\t')
      scanSeparator();

    currentlyScanningToken = true;
    currentSpelling = new StringBuilder();
    pos = new SourcePosition();
    pos.start = sourceFile.getCurrentLine();

    kind = scanToken();

    pos.finish = sourceFile.getCurrentLine();
    tok = new Token(kind, currentSpelling.toString(), pos);
    if (debug)
      System.out.println(tok);
    return tok;
  }

}
