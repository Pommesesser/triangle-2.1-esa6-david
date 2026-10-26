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

/**
 * Lexical analyzer: turns the character stream from a {@link SourceFile}
 * into {@link Token}s, one per call to {@link #scan()}.
 *
 * <p>Fun fact: the name "Scanner" for this component predates
 * {@code java.util.Scanner} (Java 5, 2004); this code dates from 1999.
 *
 * <p>Streaming design: tokens are produced on demand for the
 * {@link Parser} rather than collected into a list first. This is usually
 * justified as saving memory, but the parser builds the whole AST anyway,
 * so the saving is small. Mostly it lets scanning and parsing count as a
 * single pass, so Triangle can be described as a three-pass compiler
 * (syntactic analysis, contextual analysis, code generation).
 *
 * <p>Reserved words are not recognized here: they are returned as
 * {@code IDENTIFIER} and reclassified in the {@link Token} constructor.
 */
// TODO: recognize reserved words explicitly in scanToken() instead of
//  relying on the Token constructor to silently reclassify IDENTIFIER objects.
public final class Scanner {

  private SourceFile sourceFile;
  private boolean debug;

  /** One-character lookahead; always holds the next unconsumed character. */
  private char currentChar;
  /** Spelling of the token being scanned. */
  private StringBuilder currentSpelling;
  /** False while skipping separators, so {@link #takeIt()} discards them. */
  private boolean currentlyScanningToken;

  private boolean isLetter(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }

  /** Returns true iff the given character is an operator character. */
  private boolean isOperator(char c) {
    return (c == '+' || c == '-' || c == '*' || c == '/'
        || c == '=' || c == '<' || c == '>' || c == '\\'
        || c == '&' || c == '@' || c == '%' || c == '^'
        || c == '?');
  }

  /**
   * Creates a scanner and reads the first character into the lookahead.
   *
   * @param source the opened source file to read from
   */
  public Scanner(SourceFile source) {
    sourceFile = source;
    currentChar = sourceFile.getSource();
    debug = false;
  }

  public void enableDebugging() {
    debug = true;
  }

  /**
   * Appends the current character to the current token (unless skipping
   * separators) and reads the next character from the source program.
   */
  private void takeIt() {
    if (currentlyScanningToken)
      currentSpelling.append(currentChar);
    currentChar = sourceFile.getSource();
  }

  /**
   * Skips a single separator: one whitespace character, or a comment
   * starting with {@code !} and running to the end of the line.
   */
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

  /**
   * Consumes one token, appending its characters to {@link #currentSpelling}.
   *
   * <p>Operator characters are matched greedily, so {@code <=} or even
   * {@code +-} become a single {@code OPERATOR}. A character literal
   * accepts any quoted character, including a raw newline. At end of file
   * nothing is consumed, so repeated calls keep returning {@code EOT}.
   *
   * <p>MAXIMAL MUNCH</p>
   *
   * @return the kind of the token, with reserved words still as {@code IDENTIFIER}
   */
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

  /**
   * Skips separators and scans the next token.
   *
   * <p>{@code finish} is read after the lookahead has moved past the token,
   * so a token at the end of a line gets {@code finish = start + 1}.
   *
   * @return the next token, or an {@code EOT} token at end of file
   */
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
