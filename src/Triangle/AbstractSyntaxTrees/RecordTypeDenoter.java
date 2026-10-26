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

package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RecordTypeDenoter extends TypeDenoter {

  public RecordTypeDenoter(FieldTypeDenoter ftAST, SourcePosition thePosition) {
    super(thePosition);
    FT = ftAST;
  }

  public <RetTy, ArgTy> RetTy visit(Visitor<RetTy, ArgTy> v, ArgTy arg) {
    return v.visitRecordTypeDenoter(this, arg);
  }

  public boolean equals(Object obj) {
    if (obj != null && obj instanceof ErrorTypeDenoter)
      return true;
    else if (obj != null && obj instanceof RecordTypeDenoter)
      return this.FT.equals(((RecordTypeDenoter) obj).FT);
    else
      return false;
  }

  public FieldTypeDenoter FT;
}
