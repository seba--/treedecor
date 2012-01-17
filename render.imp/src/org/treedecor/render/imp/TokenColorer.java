package org.treedecor.render.imp;

import static org.spoofax.jsglr.client.imploder.ImploderAttachment.getRightToken;

import java.util.Map;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.ITokenColorer;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.jsglr.client.imploder.IToken;
import org.treedecor.render.util.ColorParser;
import org.treedecor.terms.java.DecorationAttachment;
import org.treedecor.terms.java.DecorationValue;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class TokenColorer implements ITokenColorer {

  public final String TEXT_COLOR = "text-color";
  
  public IRegion calculateDamageExtent(IRegion seed, IParseController parseController) {
    if (parseController.getCurrentAst() == null)
      return seed;
    
    // Always damage the complete source
    // TODO: Is always damaging the complete source still necessary??
    // Right now, TokenColorerHelper.isParserBasedPresentation() depends on this property
    ISimpleTerm ast = (ISimpleTerm) parseController.getCurrentAst();
    return new Region(0, getRightToken(ast).getTokenizer().getInput().length() - 1);
  }

  
  public TextAttribute getColoring(IParseController controller, Object oToken) {
    IToken token = (IToken) oToken;
    ISimpleTerm node = token.getAstNode();
    
    Map<String, DecorationValue> decoration = DecorationAttachment.getDecoration(node);
    if (decoration == null)
      return null;
    
    TextAttribute textColor = getTextColor(decoration.get(TEXT_COLOR));
    
    
    return textColor;
  }
  
  
  private TextAttribute getTextColor(DecorationValue v) {
    if (v == null)
      return null;
    
    if (v.isString()) {
      Color color = ColorParser.parseColor(v.getString());
      return color == null ? null : new TextAttribute(color);
    }
    
    return null;
  }

}
