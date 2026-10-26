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

package Triangle.TreeDrawer;

import java.awt.FontMetrics;

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
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;

public class LayoutVisitor implements Visitor<DrawingTree, Void> {

  private final int BORDER = 5;
  private final int PARENT_SEP = 30;

  private FontMetrics fontMetrics;

  public LayoutVisitor(FontMetrics fontMetrics) {
    this.fontMetrics = fontMetrics;
  }

  // Commands
  public DrawingTree visitAssignCommand(AssignCommand ast, Void __) {
    return layoutBinary("AssignCom.", ast.V, ast.E);
  }

  public DrawingTree visitCallCommand(CallCommand ast, Void __) {
    return layoutBinary("CallCom.", ast.I, ast.APS);
  }

  public DrawingTree visitEmptyCommand(EmptyCommand ast, Void __) {
    return layoutNullary("EmptyCom.");
  }

  public DrawingTree visitIfCommand(IfCommand ast, Void __) {
    return layoutTernary("IfCom.", ast.E, ast.C1, ast.C2);
  }

  public DrawingTree visitLetCommand(LetCommand ast, Void __) {
    return layoutBinary("LetCom.", ast.D, ast.C);
  }

  public DrawingTree visitSequentialCommand(SequentialCommand ast, Void __) {
    return layoutBinary("Seq.Com.", ast.C1, ast.C2);
  }

  public DrawingTree visitWhileCommand(WhileCommand ast, Void __) {
    return layoutBinary("WhileCom.", ast.E, ast.C);
  }

  // Expressions
  public DrawingTree visitArrayExpression(ArrayExpression ast, Void __) {
    return layoutUnary("ArrayExpr.", ast.AA);
  }

  public DrawingTree visitBinaryExpression(BinaryExpression ast, Void __) {
    return layoutTernary("Bin.Expr.", ast.E1, ast.O, ast.E2);
  }

  public DrawingTree visitCallExpression(CallExpression ast, Void __) {
    return layoutBinary("CallExpr.", ast.I, ast.APS);
  }

  public DrawingTree visitCharacterExpression(CharacterExpression ast, Void __) {
    return layoutUnary("Char.Expr.", ast.CL);
  }

  public DrawingTree visitEmptyExpression(EmptyExpression ast, Void __) {
    return layoutNullary("EmptyExpr.");
  }

  public DrawingTree visitIfExpression(IfExpression ast, Void __) {
    return layoutTernary("IfExpr.", ast.E1, ast.E2, ast.E3);
  }

  public DrawingTree visitIntegerExpression(IntegerExpression ast, Void __) {
    return layoutUnary("Int.Expr.", ast.IL);
  }

  public DrawingTree visitLetExpression(LetExpression ast, Void __) {
    return layoutBinary("LetExpr.", ast.D, ast.E);
  }

  public DrawingTree visitRecordExpression(RecordExpression ast, Void __) {
    return layoutUnary("Rec.Expr.", ast.RA);
  }

  public DrawingTree visitUnaryExpression(UnaryExpression ast, Void __) {
    return layoutBinary("UnaryExpr.", ast.O, ast.E);
  }

  public DrawingTree visitVnameExpression(VnameExpression ast, Void __) {
    return layoutUnary("VnameExpr.", ast.V);
  }

  // Declarations
  public DrawingTree visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Void __) {
    return layoutQuaternary("Bin.Op.Decl.", ast.O, ast.ARG1, ast.ARG2, ast.RES);
  }

  public DrawingTree visitConstDeclaration(ConstDeclaration ast, Void __) {
    return layoutBinary("ConstDecl.", ast.I, ast.E);
  }

  public DrawingTree visitFuncDeclaration(FuncDeclaration ast, Void __) {
    return layoutQuaternary("FuncDecl.", ast.I, ast.FPS, ast.T, ast.E);
  }

  public DrawingTree visitProcDeclaration(ProcDeclaration ast, Void __) {
    return layoutTernary("ProcDecl.", ast.I, ast.FPS, ast.C);
  }

  public DrawingTree visitSequentialDeclaration(SequentialDeclaration ast, Void __) {
    return layoutBinary("Seq.Decl.", ast.D1, ast.D2);
  }

  public DrawingTree visitTypeDeclaration(TypeDeclaration ast, Void __) {
    return layoutBinary("TypeDecl.", ast.I, ast.T);
  }

  public DrawingTree visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Void __) {
    return layoutTernary("UnaryOp.Decl.", ast.O, ast.ARG, ast.RES);
  }

  public DrawingTree visitVarDeclaration(VarDeclaration ast, Void __) {
    return layoutBinary("VarDecl.", ast.I, ast.T);
  }

  // Array Aggregates
  public DrawingTree visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void __) {
    return layoutBinary("Mult.ArrayAgg.", ast.E, ast.AA);
  }

  public DrawingTree visitSingleArrayAggregate(SingleArrayAggregate ast, Void __) {
    return layoutUnary("Sing.ArrayAgg.", ast.E);
  }

  // Record Aggregates
  public DrawingTree visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void __) {
    return layoutTernary("Mult.Rec.Agg.", ast.I, ast.E, ast.RA);
  }

  public DrawingTree visitSingleRecordAggregate(SingleRecordAggregate ast, Void __) {
    return layoutBinary("Sing.Rec.Agg.", ast.I, ast.E);
  }

  // Formal Parameters
  public DrawingTree visitConstFormalParameter(ConstFormalParameter ast, Void __) {
    return layoutBinary("ConstF.P.", ast.I, ast.T);
  }

  public DrawingTree visitFuncFormalParameter(FuncFormalParameter ast, Void __) {
    return layoutTernary("FuncF.P.", ast.I, ast.FPS, ast.T);
  }

  public DrawingTree visitProcFormalParameter(ProcFormalParameter ast, Void __) {
    return layoutBinary("ProcF.P.", ast.I, ast.FPS);
  }

  public DrawingTree visitVarFormalParameter(VarFormalParameter ast, Void __) {
    return layoutBinary("VarF.P.", ast.I, ast.T);
  }

  public DrawingTree visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Void __) {
    return layoutNullary("EmptyF.P.S.");
  }

  public DrawingTree visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Void __) {
    return layoutBinary("Mult.F.P.S.", ast.FP, ast.FPS);
  }

  public DrawingTree visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Void __) {
    return layoutUnary("Sing.F.P.S.", ast.FP);
  }

  // Actual Parameters
  public DrawingTree visitConstActualParameter(ConstActualParameter ast, Void __) {
    return layoutUnary("ConstA.P.", ast.E);
  }

  public DrawingTree visitFuncActualParameter(FuncActualParameter ast, Void __) {
    return layoutUnary("FuncA.P.", ast.I);
  }

  public DrawingTree visitProcActualParameter(ProcActualParameter ast, Void __) {
    return layoutUnary("ProcA.P.", ast.I);
  }

  public DrawingTree visitVarActualParameter(VarActualParameter ast, Void __) {
    return layoutUnary("VarA.P.", ast.V);
  }

  public DrawingTree visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Void __) {
    return layoutNullary("EmptyA.P.S.");
  }

  public DrawingTree visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Void __) {
    return layoutBinary("Mult.A.P.S.", ast.AP, ast.APS);
  }

  public DrawingTree visitSingleActualParameterSequence(SingleActualParameterSequence ast, Void __) {
    return layoutUnary("Sing.A.P.S.", ast.AP);
  }

  // Type Denoters
  public DrawingTree visitAnyTypeDenoter(AnyTypeDenoter ast, Void __) {
    return layoutNullary("any");
  }

  public DrawingTree visitArrayTypeDenoter(ArrayTypeDenoter ast, Void __) {
    return layoutBinary("ArrayTypeD.", ast.IL, ast.T);
  }

  public DrawingTree visitBoolTypeDenoter(BoolTypeDenoter ast, Void __) {
    return layoutNullary("bool");
  }

  public DrawingTree visitCharTypeDenoter(CharTypeDenoter ast, Void __) {
    return layoutNullary("char");
  }

  public DrawingTree visitErrorTypeDenoter(ErrorTypeDenoter ast, Void __) {
    return layoutNullary("error");
  }

  public DrawingTree visitSimpleTypeDenoter(SimpleTypeDenoter ast, Void __) {
    return layoutUnary("Sim.TypeD.", ast.I);
  }

  public DrawingTree visitIntTypeDenoter(IntTypeDenoter ast, Void __) {
    return layoutNullary("int");
  }

  public DrawingTree visitRecordTypeDenoter(RecordTypeDenoter ast, Void __) {
    return layoutUnary("Rec.TypeD.", ast.FT);
  }

  public DrawingTree visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Void __) {
    return layoutTernary("Mult.F.TypeD.", ast.I, ast.T, ast.FT);
  }

  public DrawingTree visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Void __) {
    return layoutBinary("Sing.F.TypeD.", ast.I, ast.T);
  }

  // Literals, Identifiers and Operators
  public DrawingTree visitCharacterLiteral(CharacterLiteral ast, Void __) {
    return layoutNullary(ast.spelling);
  }

  public DrawingTree visitIdentifier(Identifier ast, Void __) {
    return layoutNullary(ast.spelling);
  }

  public DrawingTree visitIntegerLiteral(IntegerLiteral ast, Void __) {
    return layoutNullary(ast.spelling);
  }

  public DrawingTree visitOperator(Operator ast, Void __) {
    return layoutNullary(ast.spelling);
  }

  // Value-or-variable names
  public DrawingTree visitDotVname(DotVname ast, Void __) {
    return layoutBinary("DotVname", ast.I, ast.V);
  }

  public DrawingTree visitSimpleVname(SimpleVname ast, Void __) {
    return layoutUnary("Sim.Vname", ast.I);
  }

  public DrawingTree visitSubscriptVname(SubscriptVname ast, Void __) {
    return layoutBinary("Sub.Vname", ast.V, ast.E);
  }

  // Programs
  public DrawingTree visitProgram(Program ast, Void __) {
    return layoutUnary("Program", ast.C);
  }

  private DrawingTree layoutCaption(String name) {
    int w = fontMetrics.stringWidth(name) + 4;
    int h = fontMetrics.getHeight() + 4;
    return new DrawingTree(name, w, h);
  }

  private DrawingTree layoutNullary(String name) {
    DrawingTree dt = layoutCaption(name);
    dt.contour.upper_tail = new Polyline(0, dt.height + 2 * BORDER, null);
    dt.contour.upper_head = dt.contour.upper_tail;
    dt.contour.lower_tail = new Polyline(- dt.width - 2 * BORDER, 0, null);
    dt.contour.lower_head = new Polyline(0, dt.height + 2 * BORDER, dt.contour.lower_tail);
    return dt;
  }

  private DrawingTree layoutUnary(String name, AST child1) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = child1.visit(this, null);
    dt.setChildren(new DrawingTree[] { d1 });
    attachParent(dt, join(dt));
    return dt;
  }

  private DrawingTree layoutBinary(String name, AST child1, AST child2) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = child1.visit(this, null);
    DrawingTree d2 = child2.visit(this, null);
    dt.setChildren(new DrawingTree[] { d1, d2 });
    attachParent(dt, join(dt));
    return dt;
  }

  private DrawingTree layoutTernary(String name, AST child1, AST child2, AST child3) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = child1.visit(this, null);
    DrawingTree d2 = child2.visit(this, null);
    DrawingTree d3 = child3.visit(this, null);
    dt.setChildren(new DrawingTree[] { d1, d2, d3 });
    attachParent(dt, join(dt));
    return dt;
  }

  private DrawingTree layoutQuaternary(String name, AST child1, AST child2, AST child3, AST child4) {
    DrawingTree dt = layoutCaption(name);
    DrawingTree d1 = child1.visit(this, null);
    DrawingTree d2 = child2.visit(this, null);
    DrawingTree d3 = child3.visit(this, null);
    DrawingTree d4 = child4.visit(this, null);
    dt.setChildren(new DrawingTree[] { d1, d2, d3, d4 });
    attachParent(dt, join(dt));
    return dt;
  }

  private void attachParent(DrawingTree dt, int w) {
    int y = PARENT_SEP;
    int x2 = (w - dt.width) / 2 - BORDER;
    int x1 = x2 + dt.width + 2 * BORDER - w;

    dt.children[0].offset.y = y + dt.height;
    dt.children[0].offset.x = x1;
    dt.contour.upper_head = new Polyline(0, dt.height, new Polyline(x1, y, dt.contour.upper_head));
    dt.contour.lower_head = new Polyline(0, dt.height, new Polyline(x2, y, dt.contour.lower_head));
  }

  private int join(DrawingTree dt) {
    int w, sum;

    dt.contour = dt.children[0].contour;
    sum = w = dt.children[0].width + 2 * BORDER;

    for (int i = 1; i < dt.children.length; i++) {
      int d = merge(dt.contour, dt.children[i].contour);
      dt.children[i].offset.x = d + w;
      dt.children[i].offset.y = 0;
      w = dt.children[i].width + 2 * BORDER;
      sum += d + w;
    }
    return sum;
  }

  private int merge(Polygon c1, Polygon c2) {
    int x, y, total, d;
    Polyline lower, upper, b;

    x = y = total = 0;
    upper = c1.lower_head;
    lower = c2.upper_head;

    while (lower != null && upper != null) {
      d = offset(x, y, lower.dx, lower.dy, upper.dx, upper.dy);
      x += d;
      total += d;

      if (y + lower.dy <= upper.dy) {
        x += lower.dx;
        y += lower.dy;
        lower = lower.link;
      }
      else {
        x -= upper.dx;
        y -= upper.dy;
        upper = upper.link;
      }
    }

    if (lower != null) {
      b = bridge(c1.upper_tail, 0, 0, lower, x, y);
      c1.upper_tail = (b.link != null) ? c2.upper_tail : b;
      c1.lower_tail = c2.lower_tail;
    }
    else {
      b = bridge(c2.lower_tail, x, y, upper, 0, 0);
      if (b.link == null) {
        c1.lower_tail = b;
      }
    }

    c1.lower_head = c2.lower_head;

    return total;
  }

  private int offset(int p1, int p2, int a1, int a2, int b1, int b2) {
    int d, s, t;

    if (b2 <= p2 || p2 + a2 <= 0) {
      return 0;
    }

    t = b2 * a1 - a2 * b1;
    if (t > 0) {
      if (p2 < 0) {
        s = p2 * a1;
        d = s / a2 - p1;
      }
      else if (p2 > 0) {
        s = p2 * b1;
        d = s / b2 - p1;
      }
      else {
        d = - p1;
      }
    }
    else if (b2 < p2 + a2) {
      s = (b2 - p2) * a1;
      d = b1 - (p1 + s / a2);
    }
    else if (b2 > p2 + a2) {
      s = (a2 + p2) * b1;
      d = s / b2 - (p1 + a1);
    }
    else {
      d = b1 - (p1 + a1);
    }

    if (d > 0) {
      return d;
    }
    else {
      return 0;
    }
  }

  private Polyline bridge(Polyline line1, int x1, int y1, Polyline line2, int x2, int y2) {
    int dy, dx, s;
    Polyline r;

    dy = y2 + line2.dy - y1;
    if (line2.dy == 0) {
      dx = line2.dx;
    }
    else {
      s = dy * line2.dx;
      dx = s / line2.dy;
    }

    r = new Polyline(dx, dy, line2.link);
    line1.link = new Polyline(x2 + line2.dx - dx - x1, 0, r);

    return r;
  }

}