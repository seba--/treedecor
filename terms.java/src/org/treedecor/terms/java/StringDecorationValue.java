package org.treedecor.terms.java;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class StringDecorationValue extends DecorationValue {
  private static final long serialVersionUID = 2638491329533819314L;

  private String s;
  
  public StringDecorationValue(String s) {
    this.s = s;
  }
  
  @Override
  public boolean isString() {
    return true;
  }
  
  public String getString() {
    return s;
  }
  
  public String toString() {
    return s;
  }
  
  public boolean equals(Object o) {
    return o instanceof StringDecorationValue && ((StringDecorationValue) o).s.equals(s);
  }
  
  public int hashCode() {
    return s.hashCode();
  }
}
