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

package Triangle.ContextualAnalyzer;

import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.AnyTypeDenoter;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
import Triangle.AbstractSyntaxTrees.BinaryExpression;
import Triangle.AbstractSyntaxTrees.BinaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.BoolTypeDenoter;
import Triangle.AbstractSyntaxTrees.CallCommand;
import Triangle.AbstractSyntaxTrees.CallExpression;
import Triangle.AbstractSyntaxTrees.CharTypeDenoter;
import Triangle.AbstractSyntaxTrees.CharacterExpression;
import Triangle.AbstractSyntaxTrees.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.Declaration;
import Triangle.AbstractSyntaxTrees.DotVname;
import Triangle.AbstractSyntaxTrees.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.EmptyCommand;
import Triangle.AbstractSyntaxTrees.EmptyExpression;
import Triangle.AbstractSyntaxTrees.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.EntityDeclaration;
import Triangle.AbstractSyntaxTrees.ErrorTypeDenoter;
import Triangle.AbstractSyntaxTrees.FieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.FormalParameter;
import Triangle.AbstractSyntaxTrees.FormalParameterSequence;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.CallableFunction;
import Triangle.AbstractSyntaxTrees.CallableProcedure;
import Triangle.AbstractSyntaxTrees.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.Identifier;
import Triangle.AbstractSyntaxTrees.IfCommand;
import Triangle.AbstractSyntaxTrees.IfExpression;
import Triangle.AbstractSyntaxTrees.IntTypeDenoter;
import Triangle.AbstractSyntaxTrees.IntegerExpression;
import Triangle.AbstractSyntaxTrees.IntegerLiteral;
import Triangle.AbstractSyntaxTrees.LetCommand;
import Triangle.AbstractSyntaxTrees.LetExpression;
import Triangle.AbstractSyntaxTrees.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Operator;
import Triangle.AbstractSyntaxTrees.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.RecordExpression;
import Triangle.AbstractSyntaxTrees.RecordTypeDenoter;
import Triangle.AbstractSyntaxTrees.SequentialCommand;
import Triangle.AbstractSyntaxTrees.SequentialDeclaration;
import Triangle.AbstractSyntaxTrees.SimpleTypeDenoter;
import Triangle.AbstractSyntaxTrees.SimpleVname;
import Triangle.AbstractSyntaxTrees.SingleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleArrayAggregate;
import Triangle.AbstractSyntaxTrees.SingleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.SingleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleRecordAggregate;
import Triangle.AbstractSyntaxTrees.SubscriptVname;
import Triangle.AbstractSyntaxTrees.Terminal;
import Triangle.AbstractSyntaxTrees.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.TypeDenoter;
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.VisitorBase;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;
import Triangle.SyntacticAnalyzer.SourcePosition;

public final class Checker {

  // Commands

  // Always returns null. Does not use the given object.
  private class CommandChecker extends VisitorBase<Void, Void> {
    public Void visitAssignCommand(AssignCommand ast, Void __) {
      TypeDenoter vType = ast.V.visit(vnameChecker, null);
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      if (! ast.V.variable)
        reporter.reportError("LHS of assignment is not a variable", "", ast.V.position);
      if (! eType.equals(vType))
        reporter.reportError("assignment incompatibilty", "", ast.position);
      return null;
    }

    public Void visitCallCommand(CallCommand ast, Void __) {
      Declaration binding = ast.I.visit(identifierOperatorChecker, null);
      if (binding == null)
        reportUndeclared(ast.I);
      else if (binding instanceof CallableProcedure)
        ast.APS.visit(actualParameterSequenceChecker, ((CallableProcedure) binding).getFormalParameters());
      else
        reporter.reportError("\"%\" is not a procedure identifier", ast.I.spelling, ast.I.position);
      return null;
    }

    public Void visitEmptyCommand(EmptyCommand ast, Void __) {
      return null;
    }

    public Void visitIfCommand(IfCommand ast, Void __) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      if (! eType.equals(StdEnvironment.booleanType))
        reporter.reportError("Boolean expression expected here", "", ast.E.position);
      ast.C1.visit(commandChecker, null);
      ast.C2.visit(commandChecker, null);
      return null;
    }

    public Void visitLetCommand(LetCommand ast, Void __) {
      idTable.openScope();
      ast.D.visit(declarationChecker, null);
      ast.C.visit(commandChecker, null);
      idTable.closeScope();
      return null;
    }

    public Void visitSequentialCommand(SequentialCommand ast, Void __) {
      ast.C1.visit(commandChecker, null);
      ast.C2.visit(commandChecker, null);
      return null;
    }

    public Void visitWhileCommand(WhileCommand ast, Void __) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      if (! eType.equals(StdEnvironment.booleanType))
        reporter.reportError("Boolean expression expected here", "", ast.E.position);
      ast.C.visit(commandChecker, null);
      return null;
    }
  }

  // Expressions

  // Returns the TypeDenoter denoting the type of the expression. Does
  // not use the given object.
  private class ExpressionChecker extends VisitorBase<TypeDenoter, Void> {
    public TypeDenoter visitArrayExpression(ArrayExpression ast, Void __) {
      TypeDenoter elemType = ast.AA.visit(arrayAggregateChecker, null);
      IntegerLiteral il = new IntegerLiteral(new Integer(ast.AA.elemCount).toString(), ast.position);
      ast.type = new ArrayTypeDenoter(il, elemType, ast.position);
      return ast.type;
    }

    public TypeDenoter visitBinaryExpression(BinaryExpression ast, Void __) {
      TypeDenoter e1Type = ast.E1.visit(expressionChecker, null);
      TypeDenoter e2Type = ast.E2.visit(expressionChecker, null);
      Declaration binding = ast.O.visit(identifierOperatorChecker, null);

      if (binding == null)
        reportUndeclared(ast.O);
      else {
        if (! (binding instanceof BinaryOperatorDeclaration))
          reporter.reportError("\"%\" is not a binary operator", ast.O.spelling, ast.O.position);
        BinaryOperatorDeclaration bbinding = (BinaryOperatorDeclaration) binding;
        if (bbinding.ARG1 == StdEnvironment.anyType) {
          // this operator must be "=" or "\="
          if (! e1Type.equals(e2Type))
            reporter.reportError("incompatible argument types for \"%\"", ast.O.spelling, ast.position);
        }
        else if (! e1Type.equals(bbinding.ARG1))
          reporter.reportError("wrong argument type for \"%\"", ast.O.spelling, ast.E1.position);
        else if (! e2Type.equals(bbinding.ARG2))
          reporter.reportError("wrong argument type for \"%\"", ast.O.spelling, ast.E2.position);
        ast.type = bbinding.RES;
      }
      return ast.type;
    }

    public TypeDenoter visitCallExpression(CallExpression ast, Void __) {
      Declaration binding = ast.I.visit(identifierOperatorChecker, null);
      if (binding == null) {
        reportUndeclared(ast.I);
        ast.type = StdEnvironment.errorType;
      }
      else if (binding instanceof CallableFunction) {
        CallableFunction target = (CallableFunction) binding;
        ast.APS.visit(actualParameterSequenceChecker, target.getFormalParameters());
        ast.type = target.getType();
      }
      else
        reporter.reportError("\"%\" is not a function identifier", ast.I.spelling, ast.I.position);
      return ast.type;
    }

    public TypeDenoter visitCharacterExpression(CharacterExpression ast, Void __) {
      ast.type = ast.CL.visit(literalChecker, null);
      return ast.type;
    }

    public TypeDenoter visitEmptyExpression(EmptyExpression ast, Void __) {
      ast.type = null;
      return ast.type;
    }

    public TypeDenoter visitIfExpression(IfExpression ast, Void __) {
      TypeDenoter e1Type = ast.E1.visit(expressionChecker, null);
      if (! e1Type.equals(StdEnvironment.booleanType))
        reporter.reportError("Boolean expression expected here", "", ast.E1.position);
      TypeDenoter e2Type = ast.E2.visit(expressionChecker, null);
      TypeDenoter e3Type = ast.E3.visit(expressionChecker, null);
      if (! e2Type.equals(e3Type))
        reporter.reportError("incompatible limbs in if-expression", "", ast.position);
      ast.type = e2Type;
      return ast.type;
    }

    public TypeDenoter visitIntegerExpression(IntegerExpression ast, Void __) {
      ast.type = ast.IL.visit(literalChecker, null);
      return ast.type;
    }

    public TypeDenoter visitLetExpression(LetExpression ast, Void __) {
      idTable.openScope();
      ast.D.visit(declarationChecker, null);
      ast.type = ast.E.visit(expressionChecker, null);
      idTable.closeScope();
      return ast.type;
    }

    public TypeDenoter visitRecordExpression(RecordExpression ast, Void __) {
      FieldTypeDenoter rType = ast.RA.visit(recordAggregateChecker, null);
      ast.type = new RecordTypeDenoter(rType, ast.position);
      return ast.type;
    }

    public TypeDenoter visitUnaryExpression(UnaryExpression ast, Void __) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      Declaration binding = ast.O.visit(identifierOperatorChecker, null);
      if (binding == null) {
        reportUndeclared(ast.O);
        ast.type = StdEnvironment.errorType;
      }
      else if (! (binding instanceof UnaryOperatorDeclaration))
        reporter.reportError("\"%\" is not a unary operator", ast.O.spelling, ast.O.position);
      else {
        UnaryOperatorDeclaration ubinding = (UnaryOperatorDeclaration) binding;
        if (! eType.equals(ubinding.ARG))
          reporter.reportError("wrong argument type for \"%\"", ast.O.spelling, ast.O.position);
        ast.type = ubinding.RES;
      }
      return ast.type;
    }

    public TypeDenoter visitVnameExpression(VnameExpression ast, Void __) {
      ast.type = ast.V.visit(vnameChecker, null);
      return ast.type;
    }
  }

  // Declarations

  // Always returns null. Does not use the given object.
  private class DeclarationChecker extends VisitorBase<Void, Void> {
    public Void visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Void __) {
      return null;
    }

    public Void visitConstDeclaration(ConstDeclaration ast, Void __) {
      ast.E.visit(expressionChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("identifier \"%\" already declared", ast.I.spelling, ast.position);
      return null;
    }

    public Void visitFuncDeclaration(FuncDeclaration ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast); // permits recursion
      if (duplicated)
        reporter.reportError("identifier \"%\" already declared", ast.I.spelling, ast.position);
      idTable.openScope();
      ast.FPS.visit(formalParameterChecker, null);
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      idTable.closeScope();
      if (! ast.T.equals(eType))
        reporter.reportError("body of function \"%\" has wrong type", ast.I.spelling, ast.E.position);
      return null;
    }

    public Void visitProcDeclaration(ProcDeclaration ast, Void __) {
      boolean duplicated = idTable.enter(ast.I.spelling, ast); // permits recursion
      if (duplicated)
        reporter.reportError("identifier \"%\" already declared", ast.I.spelling, ast.position);
      idTable.openScope();
      ast.FPS.visit(formalParameterChecker, null);
      ast.C.visit(commandChecker, null);
      idTable.closeScope();
      return null;
    }

    public Void visitSequentialDeclaration(SequentialDeclaration ast, Void __) {
      ast.D1.visit(declarationChecker, null);
      ast.D2.visit(declarationChecker, null);
      return null;
    }

    public Void visitTypeDeclaration(TypeDeclaration ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("identifier \"%\" already declared", ast.I.spelling, ast.position);
      return null;
    }

    public Void visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Void __) {
      return null;
    }

    public Void visitVarDeclaration(VarDeclaration ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("identifier \"%\" already declared", ast.I.spelling, ast.position);

      return null;
    }
  }

  // Array Aggregates

  // Returns the TypeDenoter for the Array Aggregate. Does not use the
  // given object.
  private class ArrayAggregateChecker extends VisitorBase<TypeDenoter, Void> {
    public TypeDenoter visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void __) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      TypeDenoter elemType = ast.AA.visit(arrayAggregateChecker, null);
      ast.elemCount = ast.AA.elemCount + 1;
      if (! eType.equals(elemType))
        reporter.reportError("incompatible array-aggregate element", "", ast.E.position);
      return elemType;
    }

    public TypeDenoter visitSingleArrayAggregate(SingleArrayAggregate ast, Void __) {
      TypeDenoter elemType = ast.E.visit(expressionChecker, null);
      ast.elemCount = 1;
      return elemType;
    }
  }

  // Record Aggregates

  // Returns the TypeDenoter for the Record Aggregate. Does not use the
  // given object.
  private class RecordAggregateChecker extends VisitorBase<FieldTypeDenoter, Void> {
    public FieldTypeDenoter visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void __) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      FieldTypeDenoter rType = ast.RA.visit(recordAggregateChecker, null);
      TypeDenoter fType = checkFieldIdentifier(rType, ast.I);
      if (fType != StdEnvironment.errorType)
        reporter.reportError("duplicate field \"%\" in record", ast.I.spelling, ast.I.position);
      ast.type = new MultipleFieldTypeDenoter(ast.I, eType, rType, ast.position);
      return ast.type;
    }

    public FieldTypeDenoter visitSingleRecordAggregate(SingleRecordAggregate ast, Void __) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      ast.type = new SingleFieldTypeDenoter(ast.I, eType, ast.position);
      return ast.type;
    }
  }

  // Formal Parameters

  // Always returns null. Does not use the given object.
  private class FormalParameterChecker extends VisitorBase<Void, Void> {
    public Void visitConstFormalParameter(ConstFormalParameter ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("duplicated formal parameter \"%\"", ast.I.spelling, ast.position);
      return null;
    }

    public Void visitFuncFormalParameter(FuncFormalParameter ast, Void __) {
      idTable.openScope();
      ast.FPS.visit(formalParameterChecker, null);
      idTable.closeScope();
      ast.T = ast.T.visit(typeDenoterChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("duplicated formal parameter \"%\"", ast.I.spelling, ast.position);
      return null;
    }

    public Void visitProcFormalParameter(ProcFormalParameter ast, Void __) {
      idTable.openScope();
      ast.FPS.visit(formalParameterChecker, null);
      idTable.closeScope();
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("duplicated formal parameter \"%\"", ast.I.spelling, ast.position);
      return null;
    }

    public Void visitVarFormalParameter(VarFormalParameter ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      boolean duplicated = idTable.enter(ast.I.spelling, ast);
      if (duplicated)
        reporter.reportError("duplicated formal parameter \"%\"", ast.I.spelling, ast.position);
      return null;
    }

    public Void visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Void __) {
      return null;
    }

    public Void visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Void __) {
      ast.FP.visit(formalParameterChecker, null);
      ast.FPS.visit(formalParameterChecker, null);
      return null;
    }

    public Void visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Void __) {
      ast.FP.visit(formalParameterChecker, null);
      return null;
    }
  }

  // Actual Parameters

  // Always returns null. Uses the given FormalParameter.
  private class ActualParameterChecker extends VisitorBase<Void, FormalParameter> {
    public Void visitConstActualParameter(ConstActualParameter ast, FormalParameter fp) {
      TypeDenoter eType = ast.E.visit(expressionChecker, null);

      if (! (fp instanceof ConstFormalParameter))
        reporter.reportError("const actual parameter not expected here", "", ast.position);
      else if (! eType.equals(((ConstFormalParameter) fp).T)) {
        reporter.reportError("wrong type for const actual parameter", "", ast.E.position);
      }
      return null;
    }

    public Void visitFuncActualParameter(FuncActualParameter ast, FormalParameter fp) {
      Declaration binding = ast.I.visit(identifierOperatorChecker, null);
      if (binding == null)
        reportUndeclared(ast.I);
      else if (! (binding instanceof CallableFunction))
        reporter.reportError("\"%\" is not a function identifier", ast.I.spelling, ast.I.position);
      else if (! (fp instanceof FuncFormalParameter))
        reporter.reportError("func actual parameter not expected here", "", ast.position);
      else {
        CallableFunction actual = (CallableFunction) binding;
        FuncFormalParameter formal = (FuncFormalParameter) fp;

        if (! actual.getFormalParameters().equals(formal.getFormalParameters()))
          reporter.reportError("wrong signature for function \"%\"", ast.I.spelling, ast.I.position);
        else if (! actual.getType().equals(formal.getType()))
          reporter.reportError("wrong type for function \"%\"", ast.I.spelling, ast.I.position);
      }
      return null;
    }

    public Void visitProcActualParameter(ProcActualParameter ast, FormalParameter fp) {
      Declaration binding = ast.I.visit(identifierOperatorChecker, null);
      if (binding == null)
        reportUndeclared(ast.I);
      else if (! (binding instanceof CallableProcedure))
        reporter.reportError("\"%\" is not a procedure identifier", ast.I.spelling, ast.I.position);
      else if (! (fp instanceof ProcFormalParameter))
        reporter.reportError("proc actual parameter not expected here", "", ast.position);
      else {
        CallableProcedure actual = (CallableProcedure) binding;
        ProcFormalParameter formal = (ProcFormalParameter) fp;

        if (! actual.getFormalParameters().equals(formal.getFormalParameters()))
          reporter.reportError("wrong signature for procedure \"%\"", ast.I.spelling, ast.I.position);
      }
      return null;
    }

    public Void visitVarActualParameter(VarActualParameter ast, FormalParameter fp) {
      TypeDenoter vType = ast.V.visit(vnameChecker, null);
      if (! ast.V.variable)
        reporter.reportError("actual parameter is not a variable", "", ast.V.position);
      else if (! (fp instanceof VarFormalParameter))
        reporter.reportError("var actual parameter not expected here", "", ast.V.position);
      else if (! vType.equals(((VarFormalParameter) fp).T))
        reporter.reportError("wrong type for var actual parameter", "", ast.V.position);
      return null;
    }
  }

  private class ActualParameterSequenceChecker extends VisitorBase<Void, FormalParameterSequence> {
    public Void visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, FormalParameterSequence fps) {
      if (! (fps instanceof EmptyFormalParameterSequence))
        reporter.reportError("too few actual parameters", "", ast.position);
      return null;
    }

    public Void visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, FormalParameterSequence fps) {
      if (! (fps instanceof MultipleFormalParameterSequence))
        reporter.reportError("too many actual parameters", "", ast.position);
      else {
        ast.AP.visit(actualParameterChecker, ((MultipleFormalParameterSequence) fps).FP);
        ast.APS.visit(actualParameterSequenceChecker, ((MultipleFormalParameterSequence) fps).FPS);
      }
      return null;
    }

    public Void visitSingleActualParameterSequence(SingleActualParameterSequence ast, FormalParameterSequence fps) {
      if (! (fps instanceof SingleFormalParameterSequence))
        reporter.reportError("incorrect number of actual parameters", "", ast.position);
      else {
        ast.AP.visit(actualParameterChecker, ((SingleFormalParameterSequence) fps).FP);
      }
      return null;
    }
  }

  // Type Denoters

  // Returns the expanded version of the TypeDenoter. Does not
  // use the given object.
  private class TypeDenoterChecker extends VisitorBase<TypeDenoter, Void> {
    public TypeDenoter visitAnyTypeDenoter(AnyTypeDenoter ast, Void __) {
      return StdEnvironment.anyType;
    }

    public TypeDenoter visitArrayTypeDenoter(ArrayTypeDenoter ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      if ((Integer.valueOf(ast.IL.spelling).intValue()) == 0)
        reporter.reportError("arrays must not be empty", "", ast.IL.position);
      return ast;
    }

    public TypeDenoter visitBoolTypeDenoter(BoolTypeDenoter ast, Void __) {
      return StdEnvironment.booleanType;
    }

    public TypeDenoter visitCharTypeDenoter(CharTypeDenoter ast, Void __) {
      return StdEnvironment.charType;
    }

    public TypeDenoter visitErrorTypeDenoter(ErrorTypeDenoter ast, Void __) {
      return StdEnvironment.errorType;
    }

    public TypeDenoter visitSimpleTypeDenoter(SimpleTypeDenoter ast, Void __) {
      Declaration binding = ast.I.visit(identifierOperatorChecker, null);
      if (binding == null) {
        reportUndeclared(ast.I);
        return StdEnvironment.errorType;
      }
      else if (! (binding instanceof TypeDeclaration)) {
        reporter.reportError("\"%\" is not a type identifier", ast.I.spelling, ast.I.position);
        return StdEnvironment.errorType;
      }
      return ((TypeDeclaration) binding).T;
    }

    public TypeDenoter visitIntTypeDenoter(IntTypeDenoter ast, Void __) {
      return StdEnvironment.integerType;
    }

    public TypeDenoter visitRecordTypeDenoter(RecordTypeDenoter ast, Void __) {
      ast.FT = (FieldTypeDenoter) ast.FT.visit(typeDenoterChecker, null);
      return ast;
    }

    public TypeDenoter visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      ast.FT.visit(typeDenoterChecker, null);
      return ast;
    }

    public TypeDenoter visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Void __) {
      ast.T = ast.T.visit(typeDenoterChecker, null);
      return ast;
    }
  }

  // Literals, Identifiers and Operators
  private class LiteralChecker extends VisitorBase<TypeDenoter, Void> {
    public TypeDenoter visitCharacterLiteral(CharacterLiteral CL, Void __) {
      return StdEnvironment.charType;
    }

    public TypeDenoter visitIntegerLiteral(IntegerLiteral IL, Void __) {
      return StdEnvironment.integerType;
    }
  }

  private class IdentifierOperatorChecker extends VisitorBase<Declaration, Void> {
    public Declaration visitIdentifier(Identifier I, Void __) {
      Declaration binding = idTable.retrieve(I.spelling);
      if (binding != null)
        I.decl = binding;
      return binding;
    }

    public Declaration visitOperator(Operator O, Void __) {
      Declaration binding = idTable.retrieve(O.spelling);
      if (binding != null)
        O.decl = binding;
      return binding;
    }
  }

  // Value-or-variable names

  // Determines the address of a named object (constant or variable).
  // This consists of a base object, to which 0 or more field-selection
  // or array-indexing operations may be applied (if it is a record or
  // array). As much as possible of the address computation is done at
  // compile-time. Code is generated only when necessary to evaluate
  // index expressions at run-time.
  // currentLevel is the routine level where the v-name occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the object is addressed at run-time.
  // It returns the description of the base object.
  // offset is set to the total of any field offsets (plus any offsets
  // due to index expressions that happen to be literals).
  // indexed is set to true iff there are any index expressions (other
  // than literals). In that case code is generated to compute the
  // offset due to these indexing operations at run-time.

  // Returns the TypeDenoter of the Vname. Does not use the
  // given object.
  private class VnameChecker extends VisitorBase<TypeDenoter, Void> {
    public TypeDenoter visitDotVname(DotVname ast, Void __) {
      ast.type = null;
      TypeDenoter vType = ast.V.visit(vnameChecker, null);
      ast.variable = ast.V.variable;
      if (! (vType instanceof RecordTypeDenoter))
        reporter.reportError("record expected here", "", ast.V.position);
      else {
        ast.type = checkFieldIdentifier(((RecordTypeDenoter) vType).FT, ast.I);
        if (ast.type == StdEnvironment.errorType)
          reporter.reportError("no field \"%\" in this record type", ast.I.spelling, ast.I.position);
      }
      return ast.type;
    }

    public TypeDenoter visitSimpleVname(SimpleVname ast, Void __) {
      ast.variable = false;
      ast.type = StdEnvironment.errorType;
      Declaration binding = ast.I.visit(identifierOperatorChecker, null);
      if (binding == null)
        reportUndeclared(ast.I);
      else if (binding instanceof EntityDeclaration) {
        EntityDeclaration entDecl = (EntityDeclaration) binding;
        ast.type = entDecl.getType();
        ast.variable = ! entDecl.isConstant();
      }
      else
        reporter.reportError("\"%\" is not a const or var identifier", ast.I.spelling, ast.I.position);
      return ast.type;
    }

    public TypeDenoter visitSubscriptVname(SubscriptVname ast, Void __) {
      TypeDenoter vType = ast.V.visit(vnameChecker, null);
      ast.variable = ast.V.variable;
      TypeDenoter eType = ast.E.visit(expressionChecker, null);
      if (vType != StdEnvironment.errorType) {
        if (! (vType instanceof ArrayTypeDenoter))
          reporter.reportError("array expected here", "", ast.V.position);
        else {
          if (! eType.equals(StdEnvironment.integerType))
            reporter.reportError("Integer expression expected here", "", ast.E.position);
          ast.type = ((ArrayTypeDenoter) vType).T;
        }
      }
      return ast.type;
    }
  }

  // Programs

  private class ProgramChecker extends VisitorBase<Void, Void> {
    public Void visitProgram(Program ast, Void __) {
      ast.C.visit(commandChecker, null);
      return null;
    }
  }

  // Checks whether the source program, represented by its AST, satisfies the
  // language's scope rules and type rules.
  // Also decorates the AST as follows:
  // (a) Each applied occurrence of an identifier or operator is linked to
  // the corresponding declaration of that identifier or operator.
  // (b) Each expression and value-or-variable-name is decorated by its type.
  // (c) Each type identifier is replaced by the type it denotes.
  // Types are represented by small ASTs.

  public void check(Program ast) {
    ast.visit(programChecker, null);
  }

  /////////////////////////////////////////////////////////////////////////////

  public Checker(ErrorReporter reporter) {
    this.reporter = reporter;
    this.idTable = new IdentificationTable();
    establishStdEnvironment();
  }

  private IdentificationTable idTable;
  private static SourcePosition dummyPos = new SourcePosition();
  private ErrorReporter reporter;

  private final CommandChecker commandChecker = new CommandChecker();
  private final ExpressionChecker expressionChecker = new ExpressionChecker();
  private final DeclarationChecker declarationChecker = new DeclarationChecker();
  private final ArrayAggregateChecker arrayAggregateChecker = new ArrayAggregateChecker();
  private final RecordAggregateChecker recordAggregateChecker = new RecordAggregateChecker();
  private final FormalParameterChecker formalParameterChecker = new FormalParameterChecker();
  private final ActualParameterChecker actualParameterChecker = new ActualParameterChecker();
  private final ActualParameterSequenceChecker actualParameterSequenceChecker = new ActualParameterSequenceChecker();
  private final TypeDenoterChecker typeDenoterChecker = new TypeDenoterChecker();
  private final LiteralChecker literalChecker = new LiteralChecker();
  private final IdentifierOperatorChecker identifierOperatorChecker = new IdentifierOperatorChecker();
  private final VnameChecker vnameChecker = new VnameChecker();
  private final ProgramChecker programChecker = new ProgramChecker();

  // Reports that the identifier or operator used at a leaf of the AST
  // has not been declared.

  private void reportUndeclared(Terminal leaf) {
    reporter.reportError("\"%\" is not declared", leaf.spelling, leaf.position);
  }

  private static TypeDenoter checkFieldIdentifier(FieldTypeDenoter ast, Identifier I) {
    if (ast instanceof MultipleFieldTypeDenoter) {
      MultipleFieldTypeDenoter ft = (MultipleFieldTypeDenoter) ast;
      if (ft.I.spelling.compareTo(I.spelling) == 0) {
        I.decl = ast;
        return ft.T;
      }
      else {
        return checkFieldIdentifier(ft.FT, I);
      }
    }
    else if (ast instanceof SingleFieldTypeDenoter) {
      SingleFieldTypeDenoter ft = (SingleFieldTypeDenoter) ast;
      if (ft.I.spelling.compareTo(I.spelling) == 0) {
        I.decl = ast;
        return ft.T;
      }
    }
    return StdEnvironment.errorType;
  }

  // Creates a small AST to represent the "declaration" of a standard
  // type, and enters it in the identification table.

  private TypeDeclaration declareStdType(String id, TypeDenoter typedenoter) {
    TypeDeclaration binding;

    binding = new TypeDeclaration(new Identifier(id, dummyPos), typedenoter, dummyPos);
    idTable.enter(id, binding);
    return binding;
  }

  // Creates a small AST to represent the "declaration" of a standard
  // type, and enters it in the identification table.

  private ConstDeclaration declareStdConst(String id, TypeDenoter constType) {
    IntegerExpression constExpr;
    ConstDeclaration binding;

    // constExpr used only as a placeholder for constType
    constExpr = new IntegerExpression(null, dummyPos);
    constExpr.type = constType;
    binding = new ConstDeclaration(new Identifier(id, dummyPos), constExpr, dummyPos);
    idTable.enter(id, binding);
    return binding;
  }

  // Creates a small AST to represent the "declaration" of a standard
  // type, and enters it in the identification table.

  private ProcDeclaration declareStdProc(String id, FormalParameterSequence fps) {
    ProcDeclaration binding;

    binding = new ProcDeclaration(new Identifier(id, dummyPos), fps, new EmptyCommand(dummyPos), dummyPos);
    idTable.enter(id, binding);
    return binding;
  }

  // Creates a small AST to represent the "declaration" of a standard
  // type, and enters it in the identification table.

  private FuncDeclaration declareStdFunc(String id, FormalParameterSequence fps, TypeDenoter resultType) {
    FuncDeclaration binding;

    binding = new FuncDeclaration(new Identifier(id, dummyPos), fps, resultType, new EmptyExpression(dummyPos),
        dummyPos);
    idTable.enter(id, binding);
    return binding;
  }

  // Creates a small AST to represent the "declaration" of a
  // unary operator, and enters it in the identification table.
  // This "declaration" summarises the operator's type info.

  private UnaryOperatorDeclaration declareStdUnaryOp(String op, TypeDenoter argType, TypeDenoter resultType) {
    UnaryOperatorDeclaration binding;

    binding = new UnaryOperatorDeclaration(new Operator(op, dummyPos), argType, resultType, dummyPos);
    idTable.enter(op, binding);
    return binding;
  }

  // Creates a small AST to represent the "declaration" of a
  // binary operator, and enters it in the identification table.
  // This "declaration" summarises the operator's type info.

  private BinaryOperatorDeclaration declareStdBinaryOp(String op, TypeDenoter arg1Type, TypeDenoter arg2type,
      TypeDenoter resultType) {
    BinaryOperatorDeclaration binding;

    binding = new BinaryOperatorDeclaration(new Operator(op, dummyPos), arg1Type, arg2type, resultType, dummyPos);
    idTable.enter(op, binding);
    return binding;
  }

  // Creates small ASTs to represent the standard types.
  // Creates small ASTs to represent "declarations" of standard types,
  // constants, procedures, functions, and operators.
  // Enters these "declarations" in the identification table.

  private final static Identifier dummyI = new Identifier("", dummyPos);

  private void establishStdEnvironment() {

    // idTable.startIdentification();
    StdEnvironment.booleanType = new BoolTypeDenoter(dummyPos);
    StdEnvironment.integerType = new IntTypeDenoter(dummyPos);
    StdEnvironment.charType = new CharTypeDenoter(dummyPos);
    StdEnvironment.anyType = new AnyTypeDenoter(dummyPos);
    StdEnvironment.errorType = new ErrorTypeDenoter(dummyPos);
    StdEnvironment.fileNameType = new ArrayTypeDenoter(new IntegerLiteral("20", dummyPos), StdEnvironment.charType,
        dummyPos);

    StdEnvironment.booleanDecl = declareStdType("Boolean", StdEnvironment.booleanType);
    StdEnvironment.falseDecl = declareStdConst("false", StdEnvironment.booleanType);
    StdEnvironment.trueDecl = declareStdConst("true", StdEnvironment.booleanType);
    StdEnvironment.notDecl = declareStdUnaryOp("\\", StdEnvironment.booleanType, StdEnvironment.booleanType);
    StdEnvironment.andDecl = declareStdBinaryOp("/\\", StdEnvironment.booleanType, StdEnvironment.booleanType,
        StdEnvironment.booleanType);
    StdEnvironment.orDecl = declareStdBinaryOp("\\/", StdEnvironment.booleanType, StdEnvironment.booleanType,
        StdEnvironment.booleanType);

    StdEnvironment.integerDecl = declareStdType("Integer", StdEnvironment.integerType);
    StdEnvironment.maxintDecl = declareStdConst("maxint", StdEnvironment.integerType);
    StdEnvironment.addDecl = declareStdBinaryOp("+", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.integerType);
    StdEnvironment.subtractDecl = declareStdBinaryOp("-", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.integerType);
    StdEnvironment.multiplyDecl = declareStdBinaryOp("*", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.integerType);
    StdEnvironment.divideDecl = declareStdBinaryOp("/", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.integerType);
    StdEnvironment.moduloDecl = declareStdBinaryOp("//", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.integerType);
    StdEnvironment.lessDecl = declareStdBinaryOp("<", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.booleanType);
    StdEnvironment.notgreaterDecl = declareStdBinaryOp("<=", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.booleanType);
    StdEnvironment.greaterDecl = declareStdBinaryOp(">", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.booleanType);
    StdEnvironment.notlessDecl = declareStdBinaryOp(">=", StdEnvironment.integerType, StdEnvironment.integerType,
        StdEnvironment.booleanType);

    StdEnvironment.charDecl = declareStdType("Char", StdEnvironment.charType);
    StdEnvironment.chrDecl = declareStdFunc("chr", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos), StdEnvironment.charType);
    StdEnvironment.ordDecl = declareStdFunc("ord", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos), StdEnvironment.integerType);
    StdEnvironment.eofDecl = declareStdProc("eof", new SingleFormalParameterSequence(
        new VarFormalParameter(dummyI, StdEnvironment.booleanType, dummyPos), dummyPos));
    StdEnvironment.eolDecl = declareStdProc("eol", new SingleFormalParameterSequence(
        new VarFormalParameter(dummyI, StdEnvironment.booleanType, dummyPos), dummyPos));
    StdEnvironment.getDecl = declareStdProc("get",
        new SingleFormalParameterSequence(new VarFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos));
    StdEnvironment.putDecl = declareStdProc("put", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos));
    StdEnvironment.getintDecl = declareStdProc("getint", new SingleFormalParameterSequence(
        new VarFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
    StdEnvironment.putintDecl = declareStdProc("putint", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
    StdEnvironment.geteolDecl = declareStdProc("geteol", new EmptyFormalParameterSequence(dummyPos));
    StdEnvironment.puteolDecl = declareStdProc("puteol", new EmptyFormalParameterSequence(dummyPos));
    StdEnvironment.equalDecl = declareStdBinaryOp("=", StdEnvironment.anyType, StdEnvironment.anyType,
        StdEnvironment.booleanType);
    StdEnvironment.unequalDecl = declareStdBinaryOp("\\=", StdEnvironment.anyType, StdEnvironment.anyType,
        StdEnvironment.booleanType);

    StdEnvironment.fopenDecl = declareStdProc("fopen",
        new MultipleFormalParameterSequence(new VarFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
            new MultipleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.fileNameType, dummyPos),
                new SingleFormalParameterSequence(
                    new ConstFormalParameter(dummyI, StdEnvironment.booleanType, dummyPos), dummyPos),
                dummyPos),
            dummyPos));
    StdEnvironment.fgetintDecl = declareStdProc("fgetint",
        new MultipleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
            new SingleFormalParameterSequence(new VarFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
                dummyPos),
            dummyPos));
    StdEnvironment.fputintDecl = declareStdProc("fputint",
        new MultipleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
            new SingleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
                dummyPos),
            dummyPos));
    StdEnvironment.fgetDecl = declareStdProc("fget", new MultipleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
        new SingleFormalParameterSequence(new VarFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos),
        dummyPos));
    StdEnvironment.fputDecl = declareStdProc("fput",
        new MultipleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
            new SingleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos),
                dummyPos),
            dummyPos));
    StdEnvironment.fgeteolDecl = declareStdProc("fgeteol", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
    StdEnvironment.fputeolDecl = declareStdProc("fputeol", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
    StdEnvironment.feolDecl = declareStdProc("feol",
        new MultipleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
            new SingleFormalParameterSequence(new VarFormalParameter(dummyI, StdEnvironment.booleanType, dummyPos),
                dummyPos),
            dummyPos));
    StdEnvironment.feofDecl = declareStdProc("feof",
        new MultipleFormalParameterSequence(new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos),
            new SingleFormalParameterSequence(new VarFormalParameter(dummyI, StdEnvironment.booleanType, dummyPos),
                dummyPos),
            dummyPos));
    StdEnvironment.fcloseDecl = declareStdProc("fclose", new SingleFormalParameterSequence(
        new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
  }
}
