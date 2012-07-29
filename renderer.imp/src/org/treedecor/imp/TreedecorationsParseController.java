package org.treedecor.imp;

import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IAnnotationTypeInfo;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.jface.text.IRegion;

public class TreedecorationsParseController implements IParseController {

	private IPath filePath;
	private ISourceProject project;
	private IMessageHandler messageHandler;

	@Override
	public IAnnotationTypeInfo getAnnotationTypeInfo() {
		System.out.println("getAnnotationTypeInfo");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStrategoTerm getCurrentAst() {
		System.out.println("getCurrentAst");
		return currentAst;
	}

	@Override
	public Language getLanguage() {
		System.out.println("getLanguage");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getPath() {
		System.out.println("getPath");
		return filePath;
	}

	@Override
	public ISourceProject getProject() {
		System.out.println("getProject");
		return project;
	}

	@Override
	public ISourcePositionLocator getSourcePositionLocator() {
		System.out.println("getSourcePositionLocator");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILanguageSyntaxProperties getSyntaxProperties() {
		System.out.println("getSyntaxProperties");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getTokenIterator(IRegion arg0) {
		System.out.println("getTokenIterator");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
		this.filePath = filePath;
		this.project = project;
		this.messageHandler = handler;
	}

	@Override
	public IStrategoTerm parse(String input, IProgressMonitor monitor) {
		System.out.println("parse");
		try {
			String parseResult = CommandExecution.callSync(getParserExe(), input);
			System.out.println("Parse result:");
			System.out.println(parseResult);
			String decorationResult = CommandExecution.callSync(getDecoratorExe(), parseResult);
			System.out.println("Decoration result string:");
			System.out.println(decorationResult);
			currentAst = parseDecorated(decorationResult);
			System.out.println("Decoration result ast:");
			System.out.println(currentAst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getCurrentAst();
	}

	// TODO: move and make this user configurable
	private String[] getParserExe() {
		String[] parserExe = {"/home/stefan/Work/treedecor/parser/parser.sh", "-t", "/home/stefan/Work/treedecor/syntax.xml/xml.tbl"};
		return parserExe;
	}
	
	// TODO: move and make this user configurable
	private String[] getDecoratorExe() {
		String[] decoratorExe = {"/home/stefan/Work/treedecor/decorator.xml/decorator.sh"};
		return decoratorExe;
	}
	
	private IStrategoTerm parseDecorated(String s) {
		StringTermReader str = new StringTermReader(new TermFactory());
		return str.parseFromString(s);
	}
}
