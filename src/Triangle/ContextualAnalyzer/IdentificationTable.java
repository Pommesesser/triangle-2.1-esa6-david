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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Triangle.AbstractSyntaxTrees.Declaration;

public final class IdentificationTable {

  private Deque<Map<String, Declaration>> scopes;

  public IdentificationTable() {
    scopes = new ArrayDeque<>();
    openScope(); // For global symbols
  }

  // Opens a new level in the identification table, 1 higher than the
  // current topmost level.
  public void openScope() {
    scopes.add(new HashMap<>());
  }

  // Closes the topmost level in the identification table, discarding
  // all entries belonging to that level.
  public void closeScope() {
    scopes.removeLast();
  }

  // Makes a new entry in the identification table for the given identifier
  // and attribute. The new entry belongs to the current level.
  // returns true if there is already an entry for the
  // same identifier at the current level.
  public boolean enter(String id, Declaration attr) {
    Map<String, Declaration> currentScope = scopes.peekLast();
    boolean duplicated = currentScope.containsKey(id);
    currentScope.put(id, attr);
    return duplicated;
  }

  // Finds an entry for the given identifier in the identification table,
  // if any. If there are several entries for that identifier, finds the
  // entry at the highest level, in accordance with the scope rules.
  // Returns null if no entry is found.
  // otherwise returns the attribute field of the entry found.
  public Declaration retrieve(String id) {
    for (Iterator<Map<String, Declaration>> iter = scopes.descendingIterator(); iter.hasNext();) {
      Map<String, Declaration> currentScope = iter.next();
      if (currentScope.containsKey(id))
        return currentScope.get(id);
    }
    return null;
  }

}
