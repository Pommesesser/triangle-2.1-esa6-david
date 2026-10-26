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

package TAM;

public final class Machine {

  public final static int fileNameLength = 20; // all filenames are array fileNameLength of Char, chr(0) to indicate end-of-string

  public final static int maxRoutineLevel = 7;

  // WORDS AND ADDRESSES

  // Java has no type synonyms, so the following representations are
  // assumed:
  //
  // type
  //   Word        = -32767..+32767; {16 bits signed}
  //   DoubleWord  = -2147483648..+2147483647; {32 bits signed}
  //   CodeAddress = 0..+32767; {15 bits unsigned}
  //   DataAddress = 0..+32767; {15 bits unsigned}

  // INSTRUCTIONS

  // Operation codes
  public final static int
      LOADop   =  0,
      LOADAop  =  1,
      LOADIop  =  2,
      LOADLop  =  3,
      STOREop  =  4,
      STOREIop =  5,
      CALLop   =  6,
      CALLIop  =  7,
      RETURNop =  8,
      PUSHop   = 10,
      POPop    = 11,
      JUMPop   = 12,
      JUMPIop  = 13,
      JUMPIFop = 14,
      HALTop   = 15,
      LASTop   = HALTop;

  public final static String[] mnemonics = { "LOAD", "LOADA", "LOADI", "LOADL", "STORE", "STOREI", "CALL", "CALLI",
      "RETURN", "ILL", "PUSH", "POP", "JUMP", "JUMPI", "JUMPIF", "HALT" };

  // CODE STORE
  public final static int maxPrimitives = 38; // number of user defined primitives

  public final static int maxCodeSize = 32767 - maxPrimitives; // max size of user code store in words

  public static Instruction[] code = new Instruction[maxCodeSize];

  // CODE STORE REGISTERS

  public final static int 
      CB = 0,
      PB = maxCodeSize, // = upper bound of code array + 1
      PT = PB + maxPrimitives;

  // REGISTER NUMBERS

  public final static int
      CBr = 0,
      CTr = 1,
      PBr = 2,
      PTr = 3,
      SBr = 4,
      STr = 5,
      HBr = 6,
      HTr = 7,
      LBr = 8,
      L1r = LBr + 1,
      L2r = LBr + 2,
      L3r = LBr + 3,
      L4r = LBr + 4,
      L5r = LBr + 5,
      L6r = LBr + 6,
      CPr = 15;

  // DATA REPRESENTATION

  public final static int
      booleanSize   = 1,
      characterSize = 1,
      integerSize   = 1,
      addressSize   = 1,
      closureSize   = 2 * addressSize,
      linkDataSize  = 3 * addressSize,
      falseRep      = 0,
      trueRep       = 1,
      maxintRep     = 32767;

  // ADDRESSES OF PRIMITIVE ROUTINES

  public final static int
      idDisplacement      =  1,
      notDisplacement     =  2,
      andDisplacement     =  3,
      orDisplacement      =  4,
      succDisplacement    =  5,
      predDisplacement    =  6,
      negDisplacement     =  7,
      addDisplacement     =  8,
      subDisplacement     =  9,
      multDisplacement    = 10, 
      divDisplacement      = 11,
      modDisplacement     = 12,
      ltDisplacement      = 13,
      leDisplacement      = 14,
      geDisplacement      = 15,
      gtDisplacement      = 16,
      eqDisplacement      = 17,
      neDisplacement      = 18,
      eolDisplacement     = 19,
      eofDisplacement     = 20,
      getDisplacement     = 21,
      putDisplacement     = 22,
      geteolDisplacement  = 23,
      puteolDisplacement  = 24,
      getintDisplacement  = 25,
      putintDisplacement  = 26,
      newDisplacement     = 27,
      disposeDisplacement = 28,
      fopenDisplacement   = 29,
      fgetintDisplacement = 30,
      fputintDisplacement = 31,
      fgetDisplacement    = 32,
      fputDisplacement    = 33,
      fgeteolDisplacement = 34,
      fputeolDisplacement = 35,
      feolDisplacement    = 36,
      feofDisplacement    = 37,
      fcloseDisplacement = maxPrimitives; // 38

  public static final String[] primnames = { "err", "id", "not", "and", "or", "succ", "pred", "neg", "add", "sub",
      "mult", "div", "mod", "lt", "le", "ge", "gt", "eq", "ne", "eol", "eof", "get", "put", "geteol", "puteol",
      "getint", "putint", "new", "dispose", "fopen", "fgetint", "fputint", "fget", "fput", "fgeteol", "fputeol", "feol",
      "feof", "fclose" };

  // for dynamic profiling
  public final static int[] execProfile = new int[LASTop + 1];
  public final static int[] primProfile = new int[maxPrimitives + 2];

}
