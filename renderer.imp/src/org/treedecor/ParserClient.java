package org.treedecor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTermBuilder;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.binary.TermReader;
import org.treedecor.UnregisteredGrammarException;
import org.w3c.dom.Document;

public class ParserClient {
	final File grammar;
	final String module;
	final URI parsingServerBaseURL;
	String grammarKey;
	
	public ParserClient(File grammar, String module, URI parsingServerBaseURL) {
		this.grammar = grammar;
		this.module = module;
		this.parsingServerBaseURL = parsingServerBaseURL;
	}

	public IStrategoTerm parse(String input) throws ClientProtocolException, URISyntaxException, IOException {
		String parseResult;
		try {
			parseResult = parseOnServer(input);
		} catch (UnregisteredGrammarException e) {
			System.out.println("Exceptions for control flow :|");
			registerGrammar();
			parseResult = parseOnServer(input);
		}
		return unserialize(parseResult);
	}
	
	IStrategoTerm unserialize(String parseResult) {
		return new TermReader(new TermFactory()).parseFromString(parseResult);
	}
	
	String parseOnServer(String input) throws URISyntaxException, IOException {
		URIBuilder uriBuilder = new URIBuilder(parsingServerBaseURL);
		uriBuilder.setPath("/parse/"+grammarKey);
		
		String parseResult = Request.Post(uriBuilder.build()).bodyString(input, ContentType.DEFAULT_BINARY).execute().handleResponse(new ResponseHandler<String>() {
			@Override
		    public String handleResponse(final HttpResponse response) throws IOException {
		        StatusLine statusLine = response.getStatusLine();
		        if (statusLine.getStatusCode() == 410) { // server replies "(re-)register grammar"
		            throw new UnregisteredGrammarException();
		        }
				return IOUtils.toString(response.getEntity().getContent());
		    }});

		System.out.println(parseResult);
		return parseResult;
	}

	void registerGrammar() throws URISyntaxException, ClientProtocolException, IOException {
		URIBuilder uriBuilder = new URIBuilder(parsingServerBaseURL);
		uriBuilder.setPath("/grammar");
		if (module != null)
			uriBuilder.addParameter("module", module);
		System.out.println("Register grammar at:");
		System.out.println(uriBuilder);
		
		String response = Request.Post(uriBuilder.build()).bodyFile(grammar, ContentType.DEFAULT_BINARY).execute().returnContent().asString();
		
		System.out.println("Grammar registration response:");
		System.out.println(response);
		grammarKey = response;
	}
	
	public static void main(String[] args) throws ClientProtocolException, URISyntaxException, IOException {
		ParserClient test = new ParserClient(new File("/home/stefan/Work/treedecor/syntax.xml/xml.def"), "xml", new URI("http://localhost:8080"));
		System.out.println(test.parse("<html></html>"));
	}
}
