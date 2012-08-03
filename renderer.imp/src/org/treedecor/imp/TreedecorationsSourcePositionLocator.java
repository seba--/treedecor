package org.treedecor.imp;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.parser.ISourcePositionLocator;

public class TreedecorationsSourcePositionLocator implements ISourcePositionLocator {
	String source;
	
	public TreedecorationsSourcePositionLocator(String currentSource) {
		this.source = currentSource;
	}
	
	private static class SourcePosition {
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

	@Override
	public Object findNode(Object astRoot, int offset) {
		System.out.println("findNode(Object, int)");
		System.out.println(astRoot);
		System.out.println(offset);
		System.out.println(sourcePosition(source, offset));
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
