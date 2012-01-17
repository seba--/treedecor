package org.treedecor.terms.java;

import java.util.Map;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.AbstractTermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.spoofax.terms.attachments.VolatileTermAttachmentType;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class DecorationAttachment extends AbstractTermAttachment {

  private static final long serialVersionUID = -2695767146327188666L;

  public static TermAttachmentType<DecorationAttachment> TYPE = 
	new VolatileTermAttachmentType<DecorationAttachment>(
        DecorationAttachment.class);

  
  private Map<String, DecorationValue> decoration;

  public DecorationAttachment(Map<String, DecorationValue> decoration) {
    this.decoration = decoration;
  }

  public TermAttachmentType<?> getAttachmentType() {
    return TYPE;
  }

  public Map<String, DecorationValue> getDecoration() {
    return decoration;
  }

  /**
   * Gets the *original* parent of this term at the time of creation, if available.
   */
  public static Map<String, DecorationValue> getDecoration(ISimpleTerm term) {
    if (term == null) return null;
    DecorationAttachment attachment = term.getAttachment(TYPE);
    return attachment == null ? null : attachment.getDecoration();
  }

  /**
   * @param parent         The parent of this term
   * @param elementParent  The direct 'Cons' node parent of a list element 
   */
  public static void putDecoration(ISimpleTerm term, Map<String, DecorationValue> decoration) {
    term.putAttachment(new DecorationAttachment(decoration));
  }
  
}
