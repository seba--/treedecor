package org.treedecor.terms.java;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class IntegerDecorationValue extends DecorationValue {
  private static final long serialVersionUID = 2638491329533819314L;

  private int i;
  
  public IntegerDecorationValue(int i) {
    this.i = i;
  }
  
  @Override
  public boolean isInteger() {
    return true;
  }
  
  @Override
  public Integer getInteger() {
    return i;
  }
  
  public String toString() {
    return i + "";
  }
  
  public boolean equals(Object o) {
    return o instanceof IntegerDecorationValue && ((IntegerDecorationValue) o).i == i;
  }
  
  public int hashCode() {
    return i;
  }
}
