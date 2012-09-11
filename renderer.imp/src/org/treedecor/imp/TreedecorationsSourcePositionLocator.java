package org.treedecor.imp;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.StrategoString;

public class TreedecorationsSourcePositionLocator implements ISourcePositionLocator {
	String source;
	
	public TreedecorationsSourcePositionLocator(String currentSource) {
		this.source = currentSource;
	}

	private static Integer integerAnnotation(IStrategoTerm term, String key) throws Exception {
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
		IStrategoTerm root = (IStrategoTerm) astRoot;
		System.out.println("findNode(Object, int)");
		System.out.println(astRoot);
		System.out.println(offset);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findNode(Object astRoot, int startOffset, int endOffset) {
		System.out.println("findNode(Object, int, int)");
		System.out.println(astRoot);
		System.out.println(startOffset);
		System.out.println(endOffset);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartOffset(Object entity) {
		System.out.println("getStartOffset(Object)");
		System.out.println(entity);
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEndOffset(Object entity) {
		System.out.println("getEndOffset(Object)");
		System.out.println(entity);
		// TODO Auto-generated method stub
		return 0;
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
