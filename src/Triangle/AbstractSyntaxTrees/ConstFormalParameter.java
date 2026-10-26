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

public class ConstFormalParameter extends FormalParameter implements EntityDeclaration {

  public ConstFormalParameter(Identifier iAST, TypeDenoter tAST, SourcePosition thePosition) {
    super(thePosition);
    I = iAST;
    T = tAST;
  }

  public <RetTy, ArgTy> RetTy visit(Visitor<RetTy, ArgTy> v, ArgTy arg) {
    return v.visitConstFormalParameter(this, arg);
  }

  public Identifier I;
  public TypeDenoter T;

  public TypeDenoter getType() {
    return T;
  }

  public boolean isConstant() {
    return true;
  }

  public boolean equals(Object fpAST) {
    if (fpAST instanceof ConstFormalParameter) {
      ConstFormalParameter cfpAST = (ConstFormalParameter) fpAST;
      return T.equals(cfpAST.T);
    }
    else
      return false;
  }
}
