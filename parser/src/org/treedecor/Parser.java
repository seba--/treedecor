package org.treedecor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
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
import org.strategoxt.lang.Context;
import org.strategoxt.stratego_aterm.pp_aterm_box_0_1;
import org.strategoxt.stratego_aterm.stratego_aterm;
import org.strategoxt.stratego_gpp.box2text_string_0_1;

public class Parser {
	protected final static ITermFactory termFactory = new TermFactory();
	protected final static Context prettyPrintingContext = stratego_aterm.init();
	protected final ParseTable parseTable;
	protected final SGLR parser;
	
	private Parser(IStrategoTerm parseTableTerm) throws InvalidParseTableException {
		parseTable = new ParseTable(parseTableTerm, termFactory);
		ITreeBuilder treeBuilder = new TreeBuilder(false);
		parser = new SGLR(treeBuilder, parseTable);		
		// Absolutely need to set this (not necessarily to true, crashes otherwise)
		parser.setUseStructureRecovery(true);
	}
	
	public Parser(byte[] table) throws ParseError, InvalidParseTableException, IOException {
		this(new TermReader(termFactory).parseFromStream(new ByteArrayInputStream(table)));
	}
	
	public Parser(String pathToTable) throws ParseError, IOException, InvalidParseTableException {
		this(new TermReader(termFactory).parseFromFile(pathToTable));
	}

	public IStrategoTerm parse(File f) throws TokenExpectedException, BadTokenException, ParseException, SGLRException, IOException, InterruptedException {
		return parse(fileContentAsString(f), f.getName());
	}
	
	public IStrategoTerm parse(String s) throws TokenExpectedException, BadTokenException, ParseException, SGLRException, InterruptedException {
		return parse(s, "No file");
	}
	
	public IStrategoTerm parse(InputStream is) throws TokenExpectedException, BadTokenException, ParseException, SGLRException, IOException, InterruptedException {
		return parse(inputStreamAsString(is), "System.in");
	}
	
	public IStrategoTerm parse(String s, String fileName) throws TokenExpectedException, BadTokenException, ParseException, SGLRException, InterruptedException {
		IStrategoTerm parseResult = (IStrategoTerm) parser.parse(s, fileName);
		return parseResult;
	}
	
	public static IStrategoTerm annotateSourceLocationInformation(IStrategoTerm ast) {
		IStrategoTerm parseResultWithSourceLocation = annotateTree(ast, new IFn<IStrategoList, IStrategoTerm>() {
			private IStrategoTuple makeStringIntPair(String s, int i) {
				return termFactory.makeTuple(termFactory.makeString(s), termFactory.makeInt(i));
			}
			
			@Override
			public IStrategoList invoke(IStrategoTerm term) {
				ImploderAttachment imploderAttachment = term.getAttachment(ImploderAttachment.TYPE);
				int startOffset = imploderAttachment.getLeftToken().getStartOffset();
				int startColumn = imploderAttachment.getLeftToken().getColumn();
				int startLine = imploderAttachment.getLeftToken().getLine();
				int endColumn = imploderAttachment.getRightToken().getEndColumn();
				int endLine = imploderAttachment.getRightToken().getEndLine();
				int endOffset = imploderAttachment.getRightToken().getEndOffset();

				return termFactory.makeList(
						makeStringIntPair("startOffset", startOffset),
						makeStringIntPair("startColumn", startColumn),
						makeStringIntPair("startLine", startLine),
						makeStringIntPair("endColumn", endColumn),
						makeStringIntPair("endLine", endLine),
						makeStringIntPair("endOffset", endOffset));
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
	private static IStrategoTerm annotateTree(IStrategoTerm term, IFn<IStrategoList, IStrategoTerm> f) {
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

	public static String prettyPrint(IStrategoTerm term) {
	    IStrategoTerm aboxTerm = pp_aterm_box_0_1.instance.invoke(prettyPrintingContext, term, termFactory.makeInt(8));
	    if (aboxTerm == null)
	    	return null;
	    
	    IStrategoTerm textTerm = box2text_string_0_1.instance.invoke(prettyPrintingContext, aboxTerm, termFactory.makeInt(80));
	    if (textTerm.getTermType() == IStrategoTerm.STRING)
	        return ((IStrategoString) textTerm).stringValue();
	    
	    return null;
	}

	public static void main(String[] args) throws org.apache.commons.cli.ParseException, ParseError, IOException, InvalidParseTableException, TokenExpectedException, BadTokenException, ParseException, SGLRException, InterruptedException {		
		Options options = new Options();
		options.addOption("t", true, "Path to grammar's .tbl file (required).");
		options.addOption("i", true, "Input file to be parsed, omit to read from std in.");
		options.addOption("o", true, "Write output to file, omit to write to std out.");
		options.addOption(null, "disable-source-location-information", false, "Do not include source location information. Defaults to false.");
		options.addOption(null, "disable-pretty-printing", false, "Disable AST pretty printing. Defaults to false.");
		options.addOption("h", "help", false, "Print this help.");
		CommandLine line = (new PosixParser()).parse(options, args);
		
		if (!line.hasOption("t") || line.hasOption("h")) {
			(new HelpFormatter()).printHelp("Treedecorations SDF parser", options );
			System.exit(1);
		}
		Parser parser = new Parser(line.getOptionValue("t"));
		
		IStrategoTerm parseResult;
		if (line.hasOption("i")) {
			parseResult = parser.parse(new File(line.getOptionValue("i")));
		} else {
			parseResult = parser.parse(System.in);
		}
		
		if (!line.hasOption("disable-source-location-information"))
			parseResult = annotateSourceLocationInformation(parseResult);
		
		String output = !line.hasOption("disable-pretty-printing") ? prettyPrint(parseResult) : parseResult.toString();
		
		if (line.hasOption("o")) {
			new FileWriter(line.getOptionValue("o")).write(output);
		} else {
			System.out.append(output);
		}
	}
}
