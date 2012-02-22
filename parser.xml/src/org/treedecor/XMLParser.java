package org.treedecor;

import java.io.IOException;
import java.util.concurrent.Callable;

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
import org.spoofax.terms.TermVisitor;
import org.spoofax.terms.io.binary.TermReader;

public class XMLParser {
	static ITermFactory termFactory = new TermFactory();
	
	public static IStrategoTerm annotateTree(IStrategoTerm term, IFn<IStrategoList, IStrategoTerm> f) {
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
	
	public static void main(String[] args) throws ParseError, IOException, InvalidParseTableException, TokenExpectedException, BadTokenException, ParseException, SGLRException {
		IStrategoTerm parseTableTerm = new TermReader(termFactory).parseFromFile("syntax/xml.tbl");
		ParseTable parseTable = new ParseTable(parseTableTerm, termFactory);
		ITreeBuilder treeBuilder = new TreeBuilder(false);
		SGLR parser = new SGLR(treeBuilder, parseTable);		
		// Absolutely need to set this (not necessarily to true, crashes otherwise)
		parser.setUseStructureRecovery(true);
		Object parseResult = parser.parse("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
"<verzeichnis>\n" +
"     <titel>Wikipedia Städteverzeichnis</titel>\n" +
"     <eintrag>\n" +
"          <stichwort>Genf</stichwort>\n" +
          "<eintragstext>Genf ist der Sitz von ...</eintragstext>\n" +
     "</eintrag>\n" +
     "<eintrag>\n" +
          "<stichwort>Köln</stichwort>\n" +
          "<eintragstext>Köln ist eine Stadt, die ...</eintragstext>\n" +
     "</eintrag>\n" +
"</verzeichnis>\n");
		
		IStrategoTerm annotated = annotateTree((IStrategoTerm) parseResult, new IFn<IStrategoList, IStrategoTerm>() {
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
		System.out.println(parseResult);
		System.out.println(annotated);
	}
}
