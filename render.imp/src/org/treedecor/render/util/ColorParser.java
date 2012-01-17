package org.treedecor.render.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class ColorParser {
  public static Color parseColor(String color) {
    if ("red".equals(color))
      return fromAWTColor(java.awt.Color.RED);
    if ("green".equals(color))
      return fromAWTColor(java.awt.Color.GREEN);
    if ("blue".equals(color))
      return fromAWTColor(java.awt.Color.BLUE);
    if ("yellow".equals(color))
      return fromAWTColor(java.awt.Color.YELLOW);
    return null;
  }
  
  private static Color fromAWTColor(java.awt.Color color) {
    return new Color(Display.getCurrent(), color.getRed(), color.getGreen(), color.getBlue());
  }
}
