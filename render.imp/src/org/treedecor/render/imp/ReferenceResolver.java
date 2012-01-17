package org.treedecor.render.imp;

import java.util.Map;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IReferenceResolver;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.treedecor.terms.java.DecorationAttachment;
import org.treedecor.terms.java.DecorationValue;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class ReferenceResolver implements IReferenceResolver {

  public final String REFERENCE = "reference";
  public final String REFERENCE_TEXT = "reference-text";
  
  
  @Override
  public IStrategoTerm getLinkTarget(Object oNode, IParseController controller) {
    IStrategoTerm node = (IStrategoTerm) oNode;
    
    Map<String, DecorationValue> decoration = DecorationAttachment.getDecoration(node);
    if (decoration == null)
      return null;
    
    IStrategoTerm target = getReference(decoration.get(REFERENCE));
    
    return target;
  }
  
  
  
  @Override
  public String getLinkText(Object oNode) {
    IStrategoTerm node = (IStrategoTerm) oNode;
    
    Map<String, DecorationValue> decoration = DecorationAttachment.getDecoration(node);
    if (decoration == null)
      return null;
    
    String text = getReferenceText(decoration.get(REFERENCE_TEXT));
    
    return text;
  }

  private IStrategoTerm getReference(DecorationValue v) {
    if (v == null)
      return null;
    
    if (v.isTerm())
      return v.getTerm();
    
    return null;
  }

  private String getReferenceText(DecorationValue v) {
    if (v == null)
      return null;
    
    if (v.isString())
      return v.getString();
    
    return null;
  }
}
