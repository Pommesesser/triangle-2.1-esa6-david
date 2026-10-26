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

public interface Visitor<RetTy, ArgTy> {

  // Commands
  RetTy visitAssignCommand(AssignCommand ast, ArgTy arg);

  RetTy visitCallCommand(CallCommand ast, ArgTy arg);

  RetTy visitEmptyCommand(EmptyCommand ast, ArgTy arg);

  RetTy visitIfCommand(IfCommand ast, ArgTy arg);

  RetTy visitLetCommand(LetCommand ast, ArgTy arg);

  RetTy visitSequentialCommand(SequentialCommand ast, ArgTy arg);

  RetTy visitWhileCommand(WhileCommand ast, ArgTy arg);

  // Expressions
  RetTy visitArrayExpression(ArrayExpression ast, ArgTy arg);

  RetTy visitBinaryExpression(BinaryExpression ast, ArgTy arg);

  RetTy visitCallExpression(CallExpression ast, ArgTy arg);

  RetTy visitCharacterExpression(CharacterExpression ast, ArgTy arg);

  RetTy visitEmptyExpression(EmptyExpression ast, ArgTy arg);

  RetTy visitIfExpression(IfExpression ast, ArgTy arg);

  RetTy visitIntegerExpression(IntegerExpression ast, ArgTy arg);

  RetTy visitLetExpression(LetExpression ast, ArgTy arg);

  RetTy visitRecordExpression(RecordExpression ast, ArgTy arg);

  RetTy visitUnaryExpression(UnaryExpression ast, ArgTy arg);

  RetTy visitVnameExpression(VnameExpression ast, ArgTy arg);

  // Declarations
  RetTy visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, ArgTy arg);

  RetTy visitConstDeclaration(ConstDeclaration ast, ArgTy arg);

  RetTy visitFuncDeclaration(FuncDeclaration ast, ArgTy arg);

  RetTy visitProcDeclaration(ProcDeclaration ast, ArgTy arg);

  RetTy visitSequentialDeclaration(SequentialDeclaration ast, ArgTy arg);

  RetTy visitTypeDeclaration(TypeDeclaration ast, ArgTy arg);

  RetTy visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, ArgTy arg);

  RetTy visitVarDeclaration(VarDeclaration ast, ArgTy arg);

  // Array Aggregates
  RetTy visitMultipleArrayAggregate(MultipleArrayAggregate ast, ArgTy arg);

  RetTy visitSingleArrayAggregate(SingleArrayAggregate ast, ArgTy arg);

  // Record Aggregates
  RetTy visitMultipleRecordAggregate(MultipleRecordAggregate ast, ArgTy arg);

  RetTy visitSingleRecordAggregate(SingleRecordAggregate ast, ArgTy arg);

  // Formal Parameters
  RetTy visitConstFormalParameter(ConstFormalParameter ast, ArgTy arg);

  RetTy visitFuncFormalParameter(FuncFormalParameter ast, ArgTy arg);

  RetTy visitProcFormalParameter(ProcFormalParameter ast, ArgTy arg);

  RetTy visitVarFormalParameter(VarFormalParameter ast, ArgTy arg);

  RetTy visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, ArgTy arg);

  RetTy visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, ArgTy arg);

  RetTy visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, ArgTy arg);

  // Actual Parameters
  RetTy visitConstActualParameter(ConstActualParameter ast, ArgTy arg);

  RetTy visitFuncActualParameter(FuncActualParameter ast, ArgTy arg);

  RetTy visitProcActualParameter(ProcActualParameter ast, ArgTy arg);

  RetTy visitVarActualParameter(VarActualParameter ast, ArgTy arg);

  RetTy visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, ArgTy arg);

  RetTy visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, ArgTy arg);

  RetTy visitSingleActualParameterSequence(SingleActualParameterSequence ast, ArgTy arg);

  // Type Denoters
  RetTy visitAnyTypeDenoter(AnyTypeDenoter ast, ArgTy arg);

  RetTy visitArrayTypeDenoter(ArrayTypeDenoter ast, ArgTy arg);

  RetTy visitBoolTypeDenoter(BoolTypeDenoter ast, ArgTy arg);

  RetTy visitCharTypeDenoter(CharTypeDenoter ast, ArgTy arg);

  RetTy visitErrorTypeDenoter(ErrorTypeDenoter ast, ArgTy arg);

  RetTy visitSimpleTypeDenoter(SimpleTypeDenoter ast, ArgTy arg);

  RetTy visitIntTypeDenoter(IntTypeDenoter ast, ArgTy arg);

  RetTy visitRecordTypeDenoter(RecordTypeDenoter ast, ArgTy arg);

  RetTy visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, ArgTy arg);

  RetTy visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, ArgTy arg);

  // Literals, Identifiers and Operators
  RetTy visitCharacterLiteral(CharacterLiteral ast, ArgTy arg);

  RetTy visitIdentifier(Identifier ast, ArgTy arg);

  RetTy visitIntegerLiteral(IntegerLiteral ast, ArgTy arg);

  RetTy visitOperator(Operator ast, ArgTy arg);

  // Value-or-variable names
  RetTy visitDotVname(DotVname ast, ArgTy arg);

  RetTy visitSimpleVname(SimpleVname ast, ArgTy arg);

  RetTy visitSubscriptVname(SubscriptVname ast, ArgTy arg);

  // Programs
  RetTy visitProgram(Program ast, ArgTy arg);

}
