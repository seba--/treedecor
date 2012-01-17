package org.treedecor.terms.java;

import java.util.Iterator;
import java.util.List;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class ListDecorationValue extends DecorationValue {
  private static final long serialVersionUID = 2638491329533819314L;

  private List<? extends DecorationValue> list;
  
  public ListDecorationValue(List<? extends DecorationValue> list) {
    this.list = list;
  }
  
  @Override
  public boolean isList() {
    return true;
  }
  
  @Override
  public List<? extends DecorationValue> getList() {
    return list;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("[");
    for (Iterator<? extends DecorationValue> it = list.iterator(); it.hasNext();) {
      b.append(it.next());
      if (it.hasNext())
        b.append(",");
    }
    b.append("]");
    return b.toString();
  }
  
  public boolean equals(Object o) {
    return o instanceof ListDecorationValue && ((ListDecorationValue) o).list.equals(list);
  }
  
  public int hashCode() {
    return list.hashCode();
  }
}
