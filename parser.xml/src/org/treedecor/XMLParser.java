package org.treedecor;

import java.io.IOException;

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
	
	public static IStrategoTuple makeStringIntPair(String s, int i) {
		return termFactory.makeTuple(termFactory.makeString(s), termFactory.makeInt(i));
	}
	
	public static IStrategoTerm annotateSourceLocation(IStrategoTerm term) {
		TermVisitor v = new TermVisitor() {
			@Override
			public void preVisit(IStrategoTerm term) {
				ImploderAttachment imploderAttachment = term.getAttachment(ImploderAttachment.TYPE);
				int startColumn = imploderAttachment.getLeftToken().getColumn();
				int startLine = imploderAttachment.getLeftToken().getLine();
				int endColumn = imploderAttachment.getRightToken().getEndColumn();
				int endLine = imploderAttachment.getLeftToken().getEndLine();
				
				termFactory.annotateTerm(term, termFactory.makeList(
						makeStringIntPair("startColumn", startColumn),
						makeStringIntPair("startLine", startLine),
						makeStringIntPair("endColumn", endColumn),
						makeStringIntPair("endLine", endLine)));
			}
		};
		v.visit(term);
		return term;
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
		
		Object annotated = annotateSourceLocation((IStrategoTerm) parseResult);
		
		System.out.println(parseResult);
		System.out.println(annotated);
	}
}
