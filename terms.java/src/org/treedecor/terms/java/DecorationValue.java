package org.treedecor.terms.java;

import java.io.Serializable;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public abstract class DecorationValue implements Serializable {
  private static final long serialVersionUID = -2176080427120170638L;

  public boolean isInteger() { return false; }
  public Integer getInteger() { return null; }
  
  public boolean isString() { return false; }
  public String getString() { return null; }
  
  public boolean isTerm() { return false; }
  public IStrategoTerm getTerm() { return null; }
  
  public boolean isList() { return false; }
  public List<? extends DecorationValue> getList() { return null; }
}
