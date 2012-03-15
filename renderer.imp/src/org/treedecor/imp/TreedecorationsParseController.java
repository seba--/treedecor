package org.treedecor.imp;

import java.io.IOException;
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
import org.spoofax.jsglr.client.InvalidParseTableException;
import org.spoofax.terms.ParseError;
import org.treedecor.Parser;

public class TreedecorationsParseController implements IParseController {

	private IPath filePath;
	private ISourceProject project;
	private IMessageHandler messageHandler;
	private Parser parser;

	@Override
	public IAnnotationTypeInfo getAnnotationTypeInfo() {
		System.out.println("getAnnotationTypeInfo");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCurrentAst() {
		System.out.println("getCurrentAst");
		// TODO Auto-generated method stub
		return null;
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
		try {
			this.parser = new Parser("../syntax.xml/xml.tbl");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object parse(String input, IProgressMonitor monitor) {
		System.out.println("parse");
		return getCurrentAst();
	}
}
