package org.treedecor;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.ITreeBuilder;
import org.spoofax.jsglr.client.InvalidParseTableException;
import org.spoofax.jsglr.client.ParseException;
import org.spoofax.jsglr.client.ParseTable;
import org.spoofax.jsglr.client.SGLR;
import org.spoofax.jsglr.client.imploder.TreeBuilder;
import org.spoofax.jsglr.shared.BadTokenException;
import org.spoofax.jsglr.shared.SGLRException;
import org.spoofax.jsglr.shared.TokenExpectedException;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.binary.TermReader;

public class XMLParser {
	public static void main(String[] args) throws ParseError, IOException, InvalidParseTableException, TokenExpectedException, BadTokenException, ParseException, SGLRException {
		ITermFactory termFactory = new TermFactory();
		IStrategoTerm parseTableTerm = new TermReader(termFactory).parseFromFile("syntax/xml.tbl");
		ParseTable parseTable = new ParseTable(parseTableTerm, termFactory);
		ITreeBuilder treeBuilder = new TreeBuilder(false);
		SGLR parser = new SGLR(treeBuilder, parseTable);		
		// Avoids some NPE in recovery code... TODO: figure out what that actually does
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
		System.out.println(parseResult);
	}
}
