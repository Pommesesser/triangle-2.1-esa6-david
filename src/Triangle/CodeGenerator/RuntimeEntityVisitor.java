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

public interface RuntimeEntityVisitor<RetTy, ArgTy> {
  RetTy visitAddressableValue(Addressable ent, ArgTy arg);

  RetTy visitEqualityRoutine(EqualityRoutine ent, ArgTy arg);

  RetTy visitField(Field ent, ArgTy arg);

  RetTy visitKnownAddress(KnownAddress ent, ArgTy arg);

  RetTy visitKnownRoutine(KnownRoutine ent, ArgTy arg);

  RetTy visitKnownValue(KnownValue ent, ArgTy arg);

  RetTy visitPrimitiveRoutine(PrimitiveRoutine ent, ArgTy arg);

  RetTy visitTypeRepresentation(TypeRepresentation ent, ArgTy arg);

  RetTy visitUnknownAddress(UnknownAddress ent, ArgTy arg);

  RetTy visitUnknownRoutine(UnknownRoutine ent, ArgTy arg);

  RetTy visitUnknownValue(UnknownValue ent, ArgTy arg);
}
