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

public class TreedecorParseController implements IParseController {

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceProject getProject() {
		System.out.println("getProject");
		// TODO Auto-generated method stub
		return null;
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
		System.out.println("initialize");
		System.out.println(filePath);
		System.out.println(project);
		System.out.println(handler);
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object parse(String arg0, IProgressMonitor arg1) {
		System.out.println("parse");
		// TODO Auto-generated method stub
		return null;
	}
}
