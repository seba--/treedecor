package org.treedecor.imp;

import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.StrategoString;

public class TreedecorationsSourcePositionLocator implements ISourcePositionLocator {
	private static Integer integerAnnotation(IStrategoTerm term, String key) {
		for (IStrategoList l = term.getAnnotations(); !l.isEmpty(); l=l.tail()) {
			if (l.head().getTermType() == IStrategoTerm.TUPLE) {
				IStrategoTuple tuple = (IStrategoTuple) l.head();
				if (tuple.size() == 2 && tuple.get(0).getTermType() == IStrategoTerm.STRING && tuple.get(0).equals(new StrategoString(key, null, IStrategoTerm.IMMUTABLE))) {
					return ((IStrategoInt) tuple.get(1)).intValue();
				}
			}
		}
		return null;
	}

	@Override
	public Object findNode(Object astRoot, int offset) {
		System.out.println("findNode(Object, int)");
		System.out.println(astRoot);
		System.out.println(offset);

		// NOTE that's what strategoxt does
		return findNode(astRoot, offset, offset - 1);
	}

	@Override
	public IStrategoTerm findNode(Object astRoot, int startOffset, int endOffset) {
		System.out.println("findNode(Object, int, int)");
		System.out.println(astRoot);
		System.out.println(startOffset);
		System.out.println(endOffset);
		
		IStrategoTerm ret = findNode2(astRoot, startOffset, endOffset);
		
		System.out.println("-->");
		System.out.println(ret);
		return ret;
	}
	
	public IStrategoTerm findNode2(Object astRoot, int startOffset, int endOffset) {
		IStrategoTerm ast = (IStrategoTerm) astRoot;
        
        if (getStartOffset(ast) <= startOffset
                        && endOffset <= getEndOffset(ast)
                        /* // no idea what this list suffix stuff is about, also uses info we do not expose (Tokenizer.findRightMostLayoutToken)
                        || isPartOfListSuffixAt(ast, endOffset)) */
                                        ) {
                for (int i = 0, max = ast.getSubtermCount(); i < max; i++) {
                        IStrategoTerm child = ast.getSubterm(i);
                        IStrategoTerm candidate = findNode2(child, startOffset, endOffset);
                if (candidate != null) {
                        assert integerAnnotation(candidate, "startOffset") != null;
                    return candidate;
                }
            }
                assert integerAnnotation(ast, "startOffset") != null;
            return ast;
        } else {
            return null;
        }
	}

	@Override
	public int getStartOffset(Object entity) {
//		System.out.println("getStartOffset(Object)");
//		System.out.println(entity);
		// NOTE no idea what the contract is for this method, for now just return the source info from the parser 
		return integerAnnotation((IStrategoTerm) entity, "startOffset");
	}

	@Override
	public int getEndOffset(Object entity) {
//		System.out.println("getEndOffset(Object)");
//		System.out.println(entity);
		// NOTE no idea what the contract is for this method, for now just return the source info from the parser 
		return integerAnnotation((IStrategoTerm) entity, "endOffset");	
	}

	@Override
	public int getLength(Object entity) {
		System.out.println("getLength(Object)");
		System.out.println(entity);
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IPath getPath(Object node) {
		System.out.println("getLength(Object)");
		System.out.println(node);
		// TODO Auto-generated method stub
		return null;
	}
}
