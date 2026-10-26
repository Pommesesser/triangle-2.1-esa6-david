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

package Triangle.CodeGenerator;

public class UnknownRoutine extends RuntimeEntity {

  public UnknownRoutine() {
    super();
    address = null;
  }

  public UnknownRoutine(int size, int level, int displacement) {
    super(size);
    address = new ObjectAddress(level, displacement);
  }

  public <RetTy, ArgTy> RetTy visit(RuntimeEntityVisitor<RetTy, ArgTy> visitor, ArgTy arg) {
    return visitor.visitUnknownRoutine(this, arg);
  }

  public ObjectAddress address;

}
