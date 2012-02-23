package org.treedecor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.ITreeBuilder;
import org.spoofax.jsglr.client.InvalidParseTableException;
import org.spoofax.jsglr.client.ParseException;
import org.spoofax.jsglr.client.ParseTable;
import org.spoofax.jsglr.client.SGLR;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.jsglr.client.imploder.TreeBuilder;
import org.spoofax.jsglr.shared.BadTokenException;
import org.spoofax.jsglr.shared.SGLRException;
import org.spoofax.jsglr.shared.TokenExpectedException;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.binary.TermReader;

public class Parser {
	protected final ITermFactory termFactory = new TermFactory();
	protected final ParseTable parseTable;
	protected final SGLR parser;
	
	public Parser(String pathToTable) throws ParseError, IOException, InvalidParseTableException {
		IStrategoTerm parseTableTerm = new TermReader(termFactory).parseFromFile(pathToTable);
		parseTable = new ParseTable(parseTableTerm, termFactory);
		ITreeBuilder treeBuilder = new TreeBuilder(false);
		parser = new SGLR(treeBuilder, parseTable);		
		// Absolutely need to set this (not necessarily to true, crashes otherwise)
		parser.setUseStructureRecovery(true);
	}

	public IStrategoTerm parse(File f) throws TokenExpectedException, BadTokenException, ParseException, SGLRException, IOException {
		return parse(fileContentAsString(f), f.getName());
	}
	
	public IStrategoTerm parse(String s) throws TokenExpectedException, BadTokenException, ParseException, SGLRException {
		return parse(s, "No file");
	}
	
	public IStrategoTerm parse(InputStream is) throws TokenExpectedException, BadTokenException, ParseException, SGLRException, IOException {
		return parse(inputStreamAsString(is), "System.in");
	}
	
	public IStrategoTerm parse(String s, String fileName) throws TokenExpectedException, BadTokenException, ParseException, SGLRException {
		IStrategoTerm parseResult = (IStrategoTerm) parser.parse(s, fileName);
		IStrategoTerm parseResultWithSourceLocation = annotateTree(parseResult, new IFn<IStrategoList, IStrategoTerm>() {
			private IStrategoTuple makeStringIntPair(String s, int i) {
				return termFactory.makeTuple(termFactory.makeString(s), termFactory.makeInt(i));
			}
			
			@Override
			public IStrategoList invoke(IStrategoTerm term) {
				ImploderAttachment imploderAttachment = term.getAttachment(ImploderAttachment.TYPE);
				int startColumn = imploderAttachment.getLeftToken().getColumn();
				int startLine = imploderAttachment.getLeftToken().getLine();
				int endColumn = imploderAttachment.getRightToken().getEndColumn();
				int endLine = imploderAttachment.getLeftToken().getEndLine();
				
				return termFactory.makeList(
						makeStringIntPair("startColumn", startColumn),
						makeStringIntPair("startLine", startLine),
						makeStringIntPair("endColumn", endColumn),
						makeStringIntPair("endLine", endLine));
			}
		});
		return parseResultWithSourceLocation;
	}
	
	/** 
	 * Bottom up tree annotator.
	 * 
	 * Takes a IStratgeoTerm term,
	 * and a function f, that given an IStrategoTerm returns an IStrategoList suitable for annotations,
	 * and returns a new tree where each node is annotated with the result of calling f on it.
	 */
	protected IStrategoTerm annotateTree(IStrategoTerm term, IFn<IStrategoList, IStrategoTerm> f) {
		// in leaf
		if (term.getAllSubterms().length == 0) {
			return termFactory.annotateTerm(term, f.invoke(term));
		}
		
		// descend into children
		IStrategoTerm[] children = term.getAllSubterms().clone();
		for (int i = 0; i < children.length; i++) {
			children[i] = annotateTree(term.getSubterm(i), f);
		}
		// rebuild current term with newly annotated children
		IStrategoList annotations = term.getAnnotations();
		switch (term.getTermType()) {
		case IStrategoTerm.APPL:
			return termFactory.makeAppl(((IStrategoAppl) term).getConstructor(), children, annotations);
		case IStrategoTerm.LIST:
			return termFactory.makeList(children, annotations);
		case IStrategoTerm.INT:
			return termFactory.makeInt(((IStrategoInt) term).intValue());
		case IStrategoTerm.REAL:
			return termFactory.makeReal(((IStrategoReal) term).realValue());
		case IStrategoTerm.STRING:
			return termFactory.makeString(((IStrategoString) term).stringValue());
		case IStrategoTerm.CTOR:
			return termFactory.makeConstructor(((IStrategoConstructor) term).getName(), ((IStrategoConstructor) term).getArity()); 
		case IStrategoTerm.TUPLE:
			return termFactory.makeTuple(children, annotations);
		case IStrategoTerm.REF:
		case IStrategoTerm.BLOB:
		case IStrategoTerm.PLACEHOLDER:
			throw new RuntimeException("dunno how");
		default:
			throw new RuntimeException("this should be unreachable ^^ Check IStrategoTerm's list of possible term types");				
		}
	}
	
	protected static String fileContentAsString(String fileName) throws IOException {
		return fileContentAsString(new File(fileName));
	}
	
	protected static String fileContentAsString(File file) throws IOException {
		if (file.length() > Integer.MAX_VALUE)
			throw new IOException("Input file " + file.getCanonicalPath() + " is too large");
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file)); 
		return inputStreamAsString(reader);
	}
	
	protected static String inputStreamAsString(InputStream in) throws IOException {
		StringWriter sw = new StringWriter();
		int c;
		while ((c = in.read()) != -1) {
			sw.write(c);
		}
		return sw.toString();
	}
	
	public static void main(String[] args) {
		// TODO CLI
	}
}
