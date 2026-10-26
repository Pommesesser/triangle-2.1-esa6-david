/*
 * Copyright (C) 2016 J. Oppermann and A. Koch
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

public class RuntimeEntityVisitorBase<RetTy, ArgTy> implements RuntimeEntityVisitor<RetTy, ArgTy> {

  protected RetTy defaultOperation(ArgTy arg) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RetTy visitAddressableValue(Addressable ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitEqualityRoutine(EqualityRoutine ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitField(Field ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitKnownAddress(KnownAddress ent, ArgTy arg) {
    return visitAddressableValue(ent, arg);
  }

  @Override
  public RetTy visitKnownRoutine(KnownRoutine ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitKnownValue(KnownValue ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitPrimitiveRoutine(PrimitiveRoutine ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitTypeRepresentation(TypeRepresentation ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitUnknownAddress(UnknownAddress ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitUnknownRoutine(UnknownRoutine ent, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitUnknownValue(UnknownValue ent, ArgTy arg) {
    return visitAddressableValue(ent, arg);
  }

}
