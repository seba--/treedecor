package org.treedecor.terms.java;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class TermDecorationValue extends DecorationValue {
  private static final long serialVersionUID = 2638491329533819314L;

  private IStrategoTerm term;
  
  public TermDecorationValue(IStrategoTerm term) {
    this.term = term;
  }
  
  @Override
  public boolean isTerm() {
    return true;
  }
  
  public IStrategoTerm getTerm() {
    return term;
  }
  
  public String toString() {
    return term.toString();
  }
  
  public boolean equals(Object o) {
    return o instanceof TermDecorationValue && ((TermDecorationValue) o).term.equals(term);
  }
  
  public int hashCode() {
    return term.hashCode();
  }
}
