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
	
//	private static class SourceRegion {
//		public final SourcePosition start, end;
//
//		public SourceRegion(SourcePosition start, SourcePosition end) {
//			this.start = start;
//			this.end = end;
//		}
//		
////		public boolean inside(SourceRegion o) {
////			return this.start.compareTo(o.start) <= 0 && this.end.compareTo(o.end) >= 0;
////		}
//	}
	
	private static class SourcePosition implements Comparable<SourcePosition> {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + col;
			result = prime * result + line;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SourcePosition other = (SourcePosition) obj;
			if (col != other.col)
				return false;
			if (line != other.line)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SourcePosition [line=" + line + ", col=" + col + "]";
		}

		public final int line;
		public final int col;
		
		public SourcePosition(int line, int col) {
			this.line = line;
			this.col = col;
		}

		@Override
		public int compareTo(SourcePosition o) {
			int ld = this.line - o.line;
			return ld == 0 ? this.col - o.col : ld;
		}
		
		public boolean inside(SourcePosition a, SourcePosition b) {
			if (a == null || b == null) return false;
			return a.compareTo(this) < 0 && this.compareTo(b) < 0;
		}
	}
	
	// FIXME: off by one (lines start at 1 in the AST)
	private static SourcePosition sourcePosition(String source, int offset) {
		int line = 0, col = 0;
		for (int i=0; i <= offset; i++) {
			if (source.charAt(i) == '\n') {
				line++;
				col = 0;
			} else {
				col++;
			}
		}
		return new SourcePosition(line, col);
	}
	
	// FIXME: off by one (probably)
	private static int offset(String source, SourcePosition sp) {
		String pattern = "";
		for (int l = 0; l < sp.line; l++) {
			pattern += "\\n";
		}
		for (int c = 0; c < sp.col; c++) {
			pattern += ".";
		}
		return Pattern.compile(pattern).matcher(source).end();
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
	
	private static SourcePosition startPosition(IStrategoTerm term) {
		try {
			return new SourcePosition(integerAnnotation(term, "startLine"), integerAnnotation(term, "startColumn"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static SourcePosition endPosition(IStrategoTerm term) {
		try {
			return new SourcePosition(integerAnnotation(term, "endLine"), integerAnnotation(term, "endColumn"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static IStrategoTerm innermostChildAt(IStrategoTerm root, SourcePosition sp) {
		if (root.getSubtermCount() == 0) {
			if (sp.inside(startPosition(root), endPosition(root))) {
				return root;
			} else {
				return null;
			}
		}
		for (IStrategoTerm subterm : root.getAllSubterms()) {
			IStrategoTerm child = innermostChildAt(subterm, sp); 
			if (child != null) {
				return child;
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
		System.out.println(sourcePosition(source, offset));
		System.out.println(innermostChildAt(root, sourcePosition(source, offset)));
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findNode(Object astRoot, int startOffset, int endOffset) {
		System.out.println("findNode(Object, int, int)");
		System.out.println(astRoot);
		System.out.println(startOffset);
		System.out.println(sourcePosition(source, startOffset));
		System.out.println(endOffset);
		System.out.println(sourcePosition(source, endOffset));
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
