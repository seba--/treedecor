package org.treedecor.imp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.client.ClientProtocolException;
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
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.treedecor.ParserClient;

public class TreedecorationsParseController implements IParseController {

	private IPath filePath;
	private ISourceProject project;
	private IMessageHandler messageHandler;
	private IStrategoTerm currentAst;
	private ParserClient parserClient;

	@Override
	public IAnnotationTypeInfo getAnnotationTypeInfo() {
		System.out.println("getAnnotationTypeInfo");
		// TODO Auto-generated method stubs
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
		return new TreedecorationsSourcePositionLocator();
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
		try {
			this.parserClient = new ParserClient(new File("/home/stefan/Work/treedecor/syntax.xml/xml.def"), "xml", new URI("http://localhost:8080"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IStrategoTerm parse(String input, IProgressMonitor monitor) {
		System.out.println("parse");
		IStrategoTerm parseResult;
		try {
			parseResult = parserClient.parse(input);
			System.out.println("Parse result:");
			System.out.println(parseResult);

			// TODO actually decorate
			currentAst = parseResult;
			System.out.println("Decoration result ast:");
			System.out.println(currentAst);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getCurrentAst();
	}
}
