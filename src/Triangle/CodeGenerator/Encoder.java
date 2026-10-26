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

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import TAM.Instruction;
import TAM.Machine;
import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.AST;
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
import Triangle.AbstractSyntaxTrees.ErrorTypeDenoter;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
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
import Triangle.AbstractSyntaxTrees.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.VisitorBase;
import Triangle.AbstractSyntaxTrees.Vname;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;

public final class Encoder {

  // Commands

  // Always returns null. Passes down the current frame.
  private class CommandEncoder extends VisitorBase<Void, Frame> {
    public Void visitAssignCommand(AssignCommand ast, Frame frame) {
      int valSize = ast.E.visit(expressionEncoder, frame);
      encodeStore(ast.V, new Frame(frame, valSize), valSize);
      return null;
    }

    public Void visitCallCommand(CallCommand ast, Frame frame) {
      int argsSize = ast.APS.visit(entityEncoder, frame);
      ast.I.visit(terminalEncoder, new Frame(frame.level, argsSize));
      return null;
    }

    public Void visitEmptyCommand(EmptyCommand ast, Frame frame) {
      return null;
    }

    public Void visitIfCommand(IfCommand ast, Frame frame) {
      int jumpifAddr, jumpAddr;

      int valSize = ast.E.visit(expressionEncoder, frame);
      jumpifAddr = nextInstrAddr;
      emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
      ast.C1.visit(commandEncoder, frame);
      jumpAddr = nextInstrAddr;
      emit(Machine.JUMPop, 0, Machine.CBr, 0);
      patch(jumpifAddr, nextInstrAddr);
      ast.C2.visit(commandEncoder, frame);
      patch(jumpAddr, nextInstrAddr);
      return null;
    }

    public Void visitLetCommand(LetCommand ast, Frame frame) {
      int extraSize = ast.D.visit(entityEncoder, frame);
      ast.C.visit(commandEncoder, new Frame(frame, extraSize));
      if (extraSize > 0)
        emit(Machine.POPop, 0, 0, extraSize);
      return null;
    }

    public Void visitSequentialCommand(SequentialCommand ast, Frame frame) {
      ast.C1.visit(commandEncoder, frame);
      ast.C2.visit(commandEncoder, frame);
      return null;
    }

    public Void visitWhileCommand(WhileCommand ast, Frame frame) {
      int jumpAddr, loopAddr;

      jumpAddr = nextInstrAddr;
      emit(Machine.JUMPop, 0, Machine.CBr, 0);
      loopAddr = nextInstrAddr;
      ast.C.visit(commandEncoder, frame);
      patch(jumpAddr, nextInstrAddr);
      ast.E.visit(expressionEncoder, frame);
      emit(Machine.JUMPIFop, Machine.trueRep, Machine.CBr, loopAddr);
      return null;
    }
  }

  // Expressions

  // returns the number of words the expression's result uses, passes down the
  // current frame
  private class ExpressionEncoder extends VisitorBase<Integer, Frame> {
    public Integer visitArrayExpression(ArrayExpression ast, Frame frame) {
      ast.type.visit(typeEncoder, null);
      return ast.AA.visit(entityEncoder, frame);
    }

    public Integer visitBinaryExpression(BinaryExpression ast, Frame frame) {
      int valSize = ast.type.visit(typeEncoder, null);
      int valSize1 = ast.E1.visit(expressionEncoder, frame);
      Frame frame1 = new Frame(frame, valSize1);
      int valSize2 = ast.E2.visit(expressionEncoder, frame1);
      Frame frame2 = new Frame(frame.level, valSize1 + valSize2);
      ast.O.visit(terminalEncoder, frame2);
      return valSize;
    }

    public Integer visitCallExpression(CallExpression ast, Frame frame) {
      int valSize = ast.type.visit(typeEncoder, null);
      int argsSize = ast.APS.visit(entityEncoder, frame);
      ast.I.visit(terminalEncoder, new Frame(frame.level, argsSize));
      return valSize;
    }

    public Integer visitCharacterExpression(CharacterExpression ast, Frame frame) {
      int valSize = ast.type.visit(typeEncoder, null);
      emit(Machine.LOADLop, 0, 0, ast.CL.spelling.charAt(1));
      return valSize;
    }

    public Integer visitEmptyExpression(EmptyExpression ast, Frame frame) {
      return 0;
    }

    public Integer visitIfExpression(IfExpression ast, Frame frame) {
      int valSize;
      int jumpifAddr, jumpAddr;

      ast.type.visit(typeEncoder, null);
      ast.E1.visit(expressionEncoder, frame);
      jumpifAddr = nextInstrAddr;
      emit(Machine.JUMPIFop, Machine.falseRep, Machine.CBr, 0);
      valSize = ast.E2.visit(expressionEncoder, frame);
      jumpAddr = nextInstrAddr;
      emit(Machine.JUMPop, 0, Machine.CBr, 0);
      patch(jumpifAddr, nextInstrAddr);
      valSize = ast.E3.visit(expressionEncoder, frame);
      patch(jumpAddr, nextInstrAddr);
      return valSize;
    }

    public Integer visitIntegerExpression(IntegerExpression ast, Frame frame) {
      int valSize = ast.type.visit(typeEncoder, null);
      emit(Machine.LOADLop, 0, 0, Integer.parseInt(ast.IL.spelling));
      return valSize;
    }

    public Integer visitLetExpression(LetExpression ast, Frame frame) {
      ast.type.visit(typeEncoder, null);
      int extraSize = ast.D.visit(entityEncoder, frame);
      Frame frame1 = new Frame(frame, extraSize);
      int valSize = ast.E.visit(expressionEncoder, frame1);
      if (extraSize > 0)
        emit(Machine.POPop, valSize, 0, extraSize);
      return valSize;
    }

    public Integer visitRecordExpression(RecordExpression ast, Frame frame) {
      ast.type.visit(typeEncoder, null);
      return ast.RA.visit(entityEncoder, frame);
    }

    public Integer visitUnaryExpression(UnaryExpression ast, Frame frame) {
      int valSize = ast.type.visit(typeEncoder, null);
      ast.E.visit(expressionEncoder, frame);
      ast.O.visit(terminalEncoder, new Frame(frame.level, valSize));
      return valSize;
    }

    public Integer visitVnameExpression(VnameExpression ast, Frame frame) {
      int valSize = ast.type.visit(typeEncoder, null);
      encodeFetch(ast.V, frame, valSize);
      return valSize;
    }
  }

  // returns the number of words the declaration allocates, passes down the
  // current frame
  private class EntityEncoder extends VisitorBase<Integer, Frame> {

    // Declarations
    public Integer visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Frame frame) {
      return 0;
    }

    public Integer visitConstDeclaration(ConstDeclaration ast, Frame frame) {
      int extraSize = 0;

      if (ast.E instanceof CharacterExpression) {
        CharacterLiteral CL = ((CharacterExpression) ast.E).CL;
        ast.entity = new KnownValue(Machine.characterSize, characterValuation(CL.spelling));
      }
      else if (ast.E instanceof IntegerExpression) {
        IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
        ast.entity = new KnownValue(Machine.integerSize, Integer.parseInt(IL.spelling));
      }
      else {
        int valSize = ast.E.visit(expressionEncoder, frame);
        ast.entity = new UnknownValue(valSize, frame.level, frame.size);
        extraSize = valSize;
      }
      writeTableDetails(ast);
      return extraSize;
    }

    public Integer visitFuncDeclaration(FuncDeclaration ast, Frame frame) {
      int jumpAddr = nextInstrAddr;
      int argsSize = 0, valSize = 0;

      emit(Machine.JUMPop, 0, Machine.CBr, 0);
      ast.entity = new KnownRoutine(Machine.closureSize, frame.level, nextInstrAddr);
      writeTableDetails(ast);
      if (frame.level == Machine.maxRoutineLevel)
        reporter.reportRestriction("can't nest routines more than 7 deep");
      else {
        Frame frame1 = new Frame(frame.level + 1, 0);
        argsSize = ast.FPS.visit(entityEncoder, frame1);
        Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
        valSize = ast.E.visit(expressionEncoder, frame2);
      }
      emit(Machine.RETURNop, valSize, 0, argsSize);
      patch(jumpAddr, nextInstrAddr);
      return 0;
    }

    public Integer visitProcDeclaration(ProcDeclaration ast, Frame frame) {
      int jumpAddr = nextInstrAddr;
      int argsSize = 0;

      emit(Machine.JUMPop, 0, Machine.CBr, 0);
      ast.entity = new KnownRoutine(Machine.closureSize, frame.level, nextInstrAddr);
      writeTableDetails(ast);
      if (frame.level == Machine.maxRoutineLevel)
        reporter.reportRestriction("can't nest routines so deeply");
      else {
        Frame frame1 = new Frame(frame.level + 1, 0);
        argsSize = ast.FPS.visit(entityEncoder, frame1);
        Frame frame2 = new Frame(frame.level + 1, Machine.linkDataSize);
        ast.C.visit(commandEncoder, frame2);
      }
      emit(Machine.RETURNop, 0, 0, argsSize);
      patch(jumpAddr, nextInstrAddr);
      return 0;
    }

    public Integer visitSequentialDeclaration(SequentialDeclaration ast, Frame frame) {
      int extraSize1, extraSize2;

      extraSize1 = ast.D1.visit(entityEncoder, frame);
      Frame frame1 = new Frame(frame, extraSize1);
      extraSize2 = ast.D2.visit(entityEncoder, frame1);
      return extraSize1 + extraSize2;
    }

    public Integer visitTypeDeclaration(TypeDeclaration ast, Frame frame) {
      // just to ensure the type's representation is decided
      ast.T.visit(typeEncoder, null);
      return 0;
    }

    public Integer visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Frame frame) {
      return 0;
    }

    public Integer visitVarDeclaration(VarDeclaration ast, Frame frame) {
      int extraSize;

      extraSize = ast.T.visit(typeEncoder, null);
      emit(Machine.PUSHop, 0, 0, extraSize);
      ast.entity = new KnownAddress(Machine.addressSize, frame.level, frame.size);
      writeTableDetails(ast);
      return extraSize;
    }

    // Array Aggregates
    public Integer visitMultipleArrayAggregate(MultipleArrayAggregate ast, Frame frame) {
      int elemSize = ast.E.visit(expressionEncoder, frame);
      Frame frame1 = new Frame(frame, elemSize);
      int arraySize = ast.AA.visit(entityEncoder, frame1);
      return elemSize + arraySize;
    }

    public Integer visitSingleArrayAggregate(SingleArrayAggregate ast, Frame frame) {
      return ast.E.visit(expressionEncoder, frame);
    }

    // Record Aggregates
    public Integer visitMultipleRecordAggregate(MultipleRecordAggregate ast, Frame frame) {
      int fieldSize = ast.E.visit(expressionEncoder, frame);
      Frame frame1 = new Frame(frame, fieldSize);
      int recordSize = ast.RA.visit(entityEncoder, frame1);
      return fieldSize + recordSize;
    }

    public Integer visitSingleRecordAggregate(SingleRecordAggregate ast, Frame frame) {
      return ast.E.visit(expressionEncoder, frame);
    }

    // Formal Parameters
    public Integer visitConstFormalParameter(ConstFormalParameter ast, Frame frame) {
      int valSize = ast.T.visit(typeEncoder, null);
      ast.entity = new UnknownValue(valSize, frame.level, - frame.size - valSize);
      writeTableDetails(ast);
      return valSize;
    }

    public Integer visitFuncFormalParameter(FuncFormalParameter ast, Frame frame) {
      int argsSize = Machine.closureSize;
      ast.entity = new UnknownRoutine(Machine.closureSize, frame.level, - frame.size - argsSize);
      writeTableDetails(ast);
      return argsSize;
    }

    public Integer visitProcFormalParameter(ProcFormalParameter ast, Frame frame) {
      int argsSize = Machine.closureSize;
      ast.entity = new UnknownRoutine(Machine.closureSize, frame.level, - frame.size - argsSize);
      writeTableDetails(ast);
      return argsSize;
    }

    public Integer visitVarFormalParameter(VarFormalParameter ast, Frame frame) {
      ast.T.visit(typeEncoder, null);
      ast.entity = new UnknownAddress(Machine.addressSize, frame.level, - frame.size - Machine.addressSize);
      writeTableDetails(ast);
      return Machine.addressSize;
    }

    public Integer visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Frame frame) {
      return 0;
    }

    public Integer visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Frame frame) {
      int argsSize1 = ast.FPS.visit(entityEncoder, frame);
      Frame frame1 = new Frame(frame, argsSize1);
      int argsSize2 = ast.FP.visit(entityEncoder, frame1);
      return argsSize1 + argsSize2;
    }

    public Integer visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Frame frame) {
      return ast.FP.visit(entityEncoder, frame);
    }

    // Actual Parameters
    public Integer visitConstActualParameter(ConstActualParameter ast, Frame frame) {
      return ast.E.visit(expressionEncoder, frame);
    }

    private int encodeClosure(RuntimeEntity ent, Frame frame) {
      ent.visit(new RuntimeEntityVisitorBase<Void, Void>() {
        public Void visitKnownRoutine(KnownRoutine ent, Void __) {
          // static link, code address
          ObjectAddress address = ent.address;
          emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), 0);
          emit(Machine.LOADAop, 0, Machine.CBr, address.displacement);
          return null;
        }

        public Void visitUnknownRoutine(UnknownRoutine ent, Void __) {
          ObjectAddress address = ent.address;
          emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level, address.level), address.displacement);
          return null;
        }

        public Void visitPrimitiveRoutine(PrimitiveRoutine ent, Void __) {
          int displacement = ent.displacement;
          // static link, code address
          emit(Machine.LOADAop, 0, Machine.SBr, 0);
          emit(Machine.LOADAop, 0, Machine.PBr, displacement);
          return null;
        }
      }, null);

      return Machine.closureSize;
    }

    public Integer visitFuncActualParameter(FuncActualParameter ast, Frame frame) {
      return encodeClosure(ast.I.decl.entity, frame);
    }

    public Integer visitProcActualParameter(ProcActualParameter ast, Frame frame) {
      return encodeClosure(ast.I.decl.entity, frame);
    }

    public Integer visitVarActualParameter(VarActualParameter ast, Frame frame) {
      encodeFetchAddress(ast.V, frame);
      return Machine.addressSize;
    }

    public Integer visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Frame frame) {
      return 0;
    }

    public Integer visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Frame frame) {
      int argsSize1 = ast.AP.visit(entityEncoder, frame);
      Frame frame1 = new Frame(frame, argsSize1);
      int argsSize2 = ast.APS.visit(entityEncoder, frame1);
      return argsSize1 + argsSize2;
    }

    public Integer visitSingleActualParameterSequence(SingleActualParameterSequence ast, Frame frame) {
      return ast.AP.visit(entityEncoder, frame);
    }
  }

  // Type Denoters

  // return type size, passes down the current element offset for composite
  // types
  private class TypeEncoder extends VisitorBase<Integer, Integer> {
    public Integer visitAnyTypeDenoter(AnyTypeDenoter ast, Integer offset) {
      return 0;
    }

    public Integer visitArrayTypeDenoter(ArrayTypeDenoter ast, Integer offset) {
      int typeSize;
      if (ast.entity == null) {
        int elemSize = ast.T.visit(typeEncoder, null);
        typeSize = Integer.parseInt(ast.IL.spelling) * elemSize;
        ast.entity = new TypeRepresentation(typeSize);
        writeTableDetails(ast);
      }
      else
        typeSize = ast.entity.size;
      return typeSize;
    }

    public Integer visitBoolTypeDenoter(BoolTypeDenoter ast, Integer offset) {
      if (ast.entity == null) {
        ast.entity = new TypeRepresentation(Machine.booleanSize);
        writeTableDetails(ast);
      }
      return Machine.booleanSize;
    }

    public Integer visitCharTypeDenoter(CharTypeDenoter ast, Integer offset) {
      if (ast.entity == null) {
        ast.entity = new TypeRepresentation(Machine.characterSize);
        writeTableDetails(ast);
      }
      return Machine.characterSize;
    }

    public Integer visitErrorTypeDenoter(ErrorTypeDenoter ast, Integer offset) {
      return 0;
    }

    public Integer visitSimpleTypeDenoter(SimpleTypeDenoter ast, Integer offset) {
      return 0;
    }

    public Integer visitIntTypeDenoter(IntTypeDenoter ast, Integer offset) {
      if (ast.entity == null) {
        ast.entity = new TypeRepresentation(Machine.integerSize);
        writeTableDetails(ast);
      }
      return Machine.integerSize;
    }

    public Integer visitRecordTypeDenoter(RecordTypeDenoter ast, Integer offset) {
      int typeSize;
      if (ast.entity == null) {
        typeSize = ast.FT.visit(typeEncoder, 0);
        ast.entity = new TypeRepresentation(typeSize);
        writeTableDetails(ast);
      }
      else
        typeSize = ast.entity.size;
      return typeSize;
    }

    public Integer visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Integer offset) {
      int fieldSize;

      if (ast.entity == null) {
        fieldSize = ast.T.visit(typeEncoder, null);
        ast.entity = new Field(fieldSize, offset);
        writeTableDetails(ast);
      }
      else
        fieldSize = ast.entity.size;

      int offset1 = offset + fieldSize;
      int recSize = ast.FT.visit(typeEncoder, offset1);
      return fieldSize + recSize;
    }

    public Integer visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Integer offset) {
      int fieldSize;

      if (ast.entity == null) {
        fieldSize = ast.T.visit(typeEncoder, null);
        ast.entity = new Field(fieldSize, offset);
        writeTableDetails(ast);
      }
      else
        fieldSize = ast.entity.size;

      return fieldSize;
    }
  }

  // Literals, Identifiers and Operators

  // always returns null, passes down the current frame
  private class TerminalEncoder extends VisitorBase<Void, Frame> {
    private Void encodeRoutine(RuntimeEntity ent, Frame frame) {
      ent.visit(new RuntimeEntityVisitorBase<Void, Void>() {
        public Void visitKnownRoutine(KnownRoutine ent, Void __) {
          ObjectAddress address = ent.address;
          emit(Machine.CALLop, displayRegister(frame.level, address.level), Machine.CBr, address.displacement);
          return null;
        }

        public Void visitUnknownRoutine(UnknownRoutine ent, Void __) {
          ObjectAddress address = ent.address;
          emit(Machine.LOADop, Machine.closureSize, displayRegister(frame.level, address.level), address.displacement);
          emit(Machine.CALLIop, 0, 0, 0);
          return null;
        }

        public Void visitPrimitiveRoutine(PrimitiveRoutine ent, Void __) {
          int displacement = ent.displacement;
          if (displacement != Machine.idDisplacement)
            emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
          return null;
        }

        public Void visitEqualityRoutine(EqualityRoutine ent, Void __) {
          // "=" or "\="
          int displacement = ent.displacement;
          emit(Machine.LOADLop, 0, 0, frame.size / 2);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, displacement);
          return null;
        }
      }, null);

      return null;
    }

    public Void visitIdentifier(Identifier ast, Frame frame) {
      return encodeRoutine(ast.decl.entity, frame);
    }

    public Void visitOperator(Operator ast, Frame frame) {
      return encodeRoutine(ast.decl.entity, frame);
    }

    public Void visitCharacterLiteral(CharacterLiteral ast, Frame frame) {
      return null;
    }

    public Void visitIntegerLiteral(IntegerLiteral ast, Frame frame) {
      return null;
    }
  }

  // Value-or-variable names

  // return referenced entity, passes down current frame
  private class VnameEncoder extends VisitorBase<RuntimeEntity, Frame> {
    public RuntimeEntity visitDotVname(DotVname ast, Frame frame) {
      RuntimeEntity baseObject = ast.V.visit(vnameEncoder, frame);
      ast.offset = ast.V.offset + ((Field) ast.I.decl.entity).fieldOffset;
      // I.decl points to the appropriate record field
      ast.indexed = ast.V.indexed;
      return baseObject;
    }

    public RuntimeEntity visitSimpleVname(SimpleVname ast, Frame frame) {
      ast.offset = 0;
      ast.indexed = false;
      return ast.I.decl.entity;
    }

    public RuntimeEntity visitSubscriptVname(SubscriptVname ast, Frame frame) {
      RuntimeEntity baseObject;
      int elemSize, indexSize;

      baseObject = ast.V.visit(vnameEncoder, frame);
      ast.offset = ast.V.offset;
      ast.indexed = ast.V.indexed;
      elemSize = ast.type.visit(typeEncoder, null);
      if (ast.E instanceof IntegerExpression) {
        IntegerLiteral IL = ((IntegerExpression) ast.E).IL;
        ast.offset = ast.offset + Integer.parseInt(IL.spelling) * elemSize;
      }
      else {
        // v-name is indexed by a proper expression, not a literal
        if (ast.indexed)
          frame.size = frame.size + Machine.integerSize;
        indexSize = ast.E.visit(expressionEncoder, frame);
        if (elemSize != 1) {
          emit(Machine.LOADLop, 0, 0, elemSize);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.multDisplacement);
        }
        if (ast.indexed)
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        else
          ast.indexed = true;
      }
      return baseObject;
    }
  }

  // Programs

  private class ProgramEncoder extends VisitorBase<Void, Frame> {
    public Void visitProgram(Program ast, Frame frame) {
      return ast.C.visit(commandEncoder, frame);
    }
  }

  public Encoder(ErrorReporter reporter) {
    this.reporter = reporter;
    nextInstrAddr = Machine.CB;
    elaborateStdEnvironment();
  }

  private ErrorReporter reporter;

  private CommandEncoder commandEncoder = new CommandEncoder();
  private ExpressionEncoder expressionEncoder = new ExpressionEncoder();
  private EntityEncoder entityEncoder = new EntityEncoder();
  private TypeEncoder typeEncoder = new TypeEncoder();
  private TerminalEncoder terminalEncoder = new TerminalEncoder();
  private VnameEncoder vnameEncoder = new VnameEncoder();
  private ProgramEncoder programEncoder = new ProgramEncoder();

  // Generates code to run a program.
  // showingTable is true iff entity description details
  // are to be displayed.
  public final void encodeRun(Program theAST, boolean showingTable) {
    tableDetailsReqd = showingTable;
    // startCodeGeneration();
    theAST.visit(programEncoder, new Frame(0, 0));
    emit(Machine.HALTop, 0, 0, 0);
  }

  // Decides run-time representation of a standard constant.
  private final void elaborateStdConst(Declaration constDeclaration, int value) {

    if (constDeclaration instanceof ConstDeclaration) {
      ConstDeclaration decl = (ConstDeclaration) constDeclaration;
      int typeSize = decl.E.type.visit(typeEncoder, null);
      decl.entity = new KnownValue(typeSize, value);
      writeTableDetails(constDeclaration);
    }
  }

  // Decides run-time representation of a standard routine.
  private final void elaborateStdPrimRoutine(Declaration routineDeclaration, int routineOffset) {
    routineDeclaration.entity = new PrimitiveRoutine(Machine.closureSize, routineOffset);
    writeTableDetails(routineDeclaration);
  }

  private final void elaborateStdEqRoutine(Declaration routineDeclaration, int routineOffset) {
    routineDeclaration.entity = new EqualityRoutine(Machine.closureSize, routineOffset);
    writeTableDetails(routineDeclaration);
  }

  private final void elaborateStdRoutine(Declaration routineDeclaration, int routineOffset) {
    routineDeclaration.entity = new KnownRoutine(Machine.closureSize, 0, routineOffset);
    writeTableDetails(routineDeclaration);
  }

  private final void elaborateStdEnvironment() {
    tableDetailsReqd = false;
    elaborateStdConst(StdEnvironment.falseDecl, Machine.falseRep);
    elaborateStdConst(StdEnvironment.trueDecl, Machine.trueRep);
    elaborateStdPrimRoutine(StdEnvironment.notDecl, Machine.notDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.andDecl, Machine.andDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.orDecl, Machine.orDisplacement);
    elaborateStdConst(StdEnvironment.maxintDecl, Machine.maxintRep);
    elaborateStdPrimRoutine(StdEnvironment.addDecl, Machine.addDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.subtractDecl, Machine.subDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.multiplyDecl, Machine.multDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.divideDecl, Machine.divDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.moduloDecl, Machine.modDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.lessDecl, Machine.ltDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.notgreaterDecl, Machine.leDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.greaterDecl, Machine.gtDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.notlessDecl, Machine.geDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.chrDecl, Machine.idDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.ordDecl, Machine.idDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.eolDecl, Machine.eolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.eofDecl, Machine.eofDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.getDecl, Machine.getDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.putDecl, Machine.putDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.getintDecl, Machine.getintDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.putintDecl, Machine.putintDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.geteolDecl, Machine.geteolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.puteolDecl, Machine.puteolDisplacement);
    elaborateStdEqRoutine(StdEnvironment.equalDecl, Machine.eqDisplacement);
    elaborateStdEqRoutine(StdEnvironment.unequalDecl, Machine.neDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fopenDecl, Machine.fopenDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fgetintDecl, Machine.fgetintDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fputintDecl, Machine.fputintDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fgetDecl, Machine.fgetDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fputDecl, Machine.fputDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fgeteolDecl, Machine.fgeteolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fputeolDecl, Machine.fputeolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.feolDecl, Machine.feolDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.feofDecl, Machine.feofDisplacement);
    elaborateStdPrimRoutine(StdEnvironment.fcloseDecl, Machine.fcloseDisplacement);
  }

  // Saves the object program in the named file.

  public void saveObjectProgram(String objectName) {
    FileOutputStream objectFile = null;
    DataOutputStream objectStream = null;

    int addr;

    try {
      objectFile = new FileOutputStream(objectName);
      objectStream = new DataOutputStream(objectFile);

      addr = Machine.CB;
      for (addr = Machine.CB; addr < nextInstrAddr; addr++)
        Machine.code[addr].write(objectStream);
      objectFile.close();
    }
    catch (FileNotFoundException s) {
      System.err.println("Error opening object file: " + s);
    }
    catch (IOException s) {
      System.err.println("Error writing object file: " + s);
    }
  }

  boolean tableDetailsReqd;

  public static void writeTableDetails(AST ast) {
  }

  // OBJECT CODE

  // Implementation notes:
  // Object code is generated directly into the TAM Code Store, starting at CB.
  // The address of the next instruction is held in nextInstrAddr.

  private int nextInstrAddr;

  // Appends an instruction, with the given fields, to the object code.
  private void emit(int op, int n, int r, int d) {
    Instruction nextInstr = new Instruction();
    if (n > 255) {
      reporter.reportRestriction("length of operand can't exceed 255 words");
      n = 255; // to allow code generation to continue
    }
    nextInstr.op = op;
    nextInstr.n = n;
    nextInstr.r = r;
    nextInstr.d = d;
    if (nextInstrAddr == Machine.PB)
      reporter.reportRestriction("too many instructions for code segment");
    else {
      Machine.code[nextInstrAddr] = nextInstr;
      nextInstrAddr = nextInstrAddr + 1;
    }
  }

  // Patches the d-field of the instruction at address addr.
  private void patch(int addr, int d) {
    Machine.code[addr].d = d;
  }

  // DATA REPRESENTATION

  public int characterValuation(String spelling) {
    // Returns the machine representation of the given character literal.
    return spelling.charAt(1);
    // since the character literal is of the form 'x'}
  }

  // REGISTERS

  // Returns the register number appropriate for object code at currentLevel
  // to address a data object at objectLevel.
  private int displayRegister(int currentLevel, int objectLevel) {
    if (objectLevel == 0)
      return Machine.SBr;
    else if (currentLevel - objectLevel <= 6)
      return Machine.LBr + currentLevel - objectLevel; // LBr|L1r|...|L6r
    else {
      reporter.reportRestriction("can't access data more than 6 levels out");
      return Machine.L6r; // to allow code generation to continue
    }
  }

  // Generates code to store the value of a named constant or variable
  // and pop it from the stack.
  // currentLevel is the routine level where the vname occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the constant or variable is fetched at run-time.
  // valSize is the size of the constant or variable's value.

  private void encodeStore(Vname V, Frame frame, int valSize) {
    RuntimeEntity baseObject = V.visit(vnameEncoder, frame);
    // If indexed = true, code will have been generated to load an index value.
    if (valSize > 255) {
      reporter.reportRestriction("can't store values larger than 255 words");
      valSize = 255; // to allow code generation to continue
    }

    final int vs = valSize;
    baseObject.visit(new RuntimeEntityVisitorBase<Void, Void>() {
      public Void visitKnownAddress(KnownAddress ent, Void __) {
        ObjectAddress address = ent.address;
        if (V.indexed) {
          emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), address.displacement + V.offset);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
          emit(Machine.STOREIop, vs, 0, 0);
        }
        else {
          emit(Machine.STOREop, vs, displayRegister(frame.level, address.level), address.displacement + V.offset);
        }
        return null;
      }

      public Void visitUnknownAddress(UnknownAddress ent, Void __) {
        ObjectAddress address = ent.address;
        emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level, address.level), address.displacement);
        if (V.indexed)
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        if (V.offset != 0) {
          emit(Machine.LOADLop, 0, 0, V.offset);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        }
        emit(Machine.STOREIop, vs, 0, 0);
        return null;
      }

    }, null);
  }

  // Generates code to fetch the value of a named constant or variable
  // and push it on to the stack.
  // currentLevel is the routine level where the vname occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the constant or variable is fetched at run-time.
  // valSize is the size of the constant or variable's value.

  private void encodeFetch(Vname V, Frame frame, int valSize) {
    RuntimeEntity baseObject = V.visit(vnameEncoder, frame);
    // If indexed = true, code will have been generated to load an index value.
    if (valSize > 255) {
      reporter.reportRestriction("can't load values larger than 255 words");
      valSize = 255; // to allow code generation to continue
    }

    final int vs = valSize;
    baseObject.visit(new RuntimeEntityVisitorBase<Void, Void>() {
      public Void visitKnownValue(KnownValue ent, Void __) {
        // presumably offset = 0 and indexed = false
        int value = ent.value;
        emit(Machine.LOADLop, 0, 0, value);
        return null;
      }

      private Void encodeVariableFetch(ObjectAddress address) {
        if (V.indexed) {
          emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), address.displacement + V.offset);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
          emit(Machine.LOADIop, vs, 0, 0);
        }
        else
          emit(Machine.LOADop, vs, displayRegister(frame.level, address.level), address.displacement + V.offset);
        return null;
      }

      public Void visitAddressableValue(Addressable ent, Void __) {
        ObjectAddress address = ent.address;
        return encodeVariableFetch(address);
      }

      public Void visitUnknownAddress(UnknownAddress ent, Void __) {
        ObjectAddress address = ent.address;
        emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level, address.level), address.displacement);
        if (V.indexed)
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        if (V.offset != 0) {
          emit(Machine.LOADLop, 0, 0, V.offset);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        }
        emit(Machine.LOADIop, vs, 0, 0);
        return null;
      }
    }, null);
  }

  // Generates code to compute and push the address of a named variable.
  // vname is the program phrase that names this variable.
  // currentLevel is the routine level where the vname occurs.
  // frameSize is the anticipated size of the local stack frame when
  // the variable is addressed at run-time.

  private void encodeFetchAddress(Vname V, Frame frame) {
    RuntimeEntity baseObject = V.visit(vnameEncoder, frame);

    // If indexed = true, code will have been generated to load an index value.
    baseObject.visit(new RuntimeEntityVisitorBase<Void, Void>() {
      public Void visitKnownAddress(KnownAddress ent, Void __) {
        ObjectAddress address = ent.address;
        emit(Machine.LOADAop, 0, displayRegister(frame.level, address.level), address.displacement + V.offset);
        if (V.indexed)
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        return null;
      }

      public Void visitUnknownAddress(UnknownAddress ent, Void __) {
        ObjectAddress address = ent.address;
        emit(Machine.LOADop, Machine.addressSize, displayRegister(frame.level, address.level), address.displacement);
        if (V.indexed)
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        if (V.offset != 0) {
          emit(Machine.LOADLop, 0, 0, V.offset);
          emit(Machine.CALLop, Machine.SBr, Machine.PBr, Machine.addDisplacement);
        }
        return null;
      }

    }, null);
  }
}
