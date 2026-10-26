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

package Triangle.AbstractSyntaxTrees;

public abstract class VisitorBase<RetTy, ArgTy> implements Visitor<RetTy, ArgTy> {

  protected RetTy defaultOperation(ArgTy arg) {
    throw new UnsupportedOperationException();
  }

  // --- Commands ---

  @Override
  public RetTy visitAssignCommand(AssignCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitCallCommand(CallCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitEmptyCommand(EmptyCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitIfCommand(IfCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitLetCommand(LetCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSequentialCommand(SequentialCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitWhileCommand(WhileCommand ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Expressions ---

  @Override
  public RetTy visitArrayExpression(ArrayExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitBinaryExpression(BinaryExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitCallExpression(CallExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitCharacterExpression(CharacterExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitEmptyExpression(EmptyExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitIfExpression(IfExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitIntegerExpression(IntegerExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitLetExpression(LetExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitRecordExpression(RecordExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitUnaryExpression(UnaryExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitVnameExpression(VnameExpression ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Declarations ---

  @Override
  public RetTy visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitConstDeclaration(ConstDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitFuncDeclaration(FuncDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitProcDeclaration(ProcDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSequentialDeclaration(SequentialDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitTypeDeclaration(TypeDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitVarDeclaration(VarDeclaration ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Array Aggregates ---

  @Override
  public RetTy visitMultipleArrayAggregate(MultipleArrayAggregate ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSingleArrayAggregate(SingleArrayAggregate ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Record Aggregates ---

  @Override
  public RetTy visitMultipleRecordAggregate(MultipleRecordAggregate ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSingleRecordAggregate(SingleRecordAggregate ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Formal Parameters ---

  @Override
  public RetTy visitConstFormalParameter(ConstFormalParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitFuncFormalParameter(FuncFormalParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitProcFormalParameter(ProcFormalParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitVarFormalParameter(VarFormalParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Actual Parameters ---

  @Override
  public RetTy visitConstActualParameter(ConstActualParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitFuncActualParameter(FuncActualParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitProcActualParameter(ProcActualParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitVarActualParameter(VarActualParameter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSingleActualParameterSequence(SingleActualParameterSequence ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Tyoe Denoters ---

  @Override
  public RetTy visitAnyTypeDenoter(AnyTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitArrayTypeDenoter(ArrayTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitBoolTypeDenoter(BoolTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitCharTypeDenoter(CharTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitErrorTypeDenoter(ErrorTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSimpleTypeDenoter(SimpleTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitIntTypeDenoter(IntTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitRecordTypeDenoter(RecordTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Literals, Identifiers and Operators ---

  @Override
  public RetTy visitCharacterLiteral(CharacterLiteral ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitIdentifier(Identifier ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitIntegerLiteral(IntegerLiteral ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitOperator(Operator ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Value-or-variable names

  @Override
  public RetTy visitDotVname(DotVname ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSimpleVname(SimpleVname ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  @Override
  public RetTy visitSubscriptVname(SubscriptVname ast, ArgTy arg) {
    return defaultOperation(arg);
  }

  // --- Programs ---

  @Override
  public RetTy visitProgram(Program ast, ArgTy arg) {
    return defaultOperation(arg);
  }

}
