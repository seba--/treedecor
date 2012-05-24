package org.treedecor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ParseHTTPHandler extends AbstractHandler {
	private final Parser parser;
	
	public ParseHTTPHandler(Parser parser) {
		super();
		this.parser = parser;
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		String parseResult = "Error while parsing:\n";
		try {
			parseResult = parser.parse(request.getInputStream()).toString();
		} catch (Exception e) {
			parseResult += e.getMessage();
		}
		response.setContentType("text/plain");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().write(parseResult);
	}
	
	public static void main(String[] args) throws Exception {
		Server server = null;
		try {
			Options options = new Options();
			options.addOption("t", true, "Path to grammar's .tbl file (required)");
			options.addOption("p", true, "Port for HTTP server");
			options.addOption("h", "help", false, "Print help");
			CommandLine line = (new PosixParser()).parse(options, args);

			if (!(line.hasOption("t") && line.hasOption("p"))) {
				(new HelpFormatter()).printHelp("Treedecorations SDF parser", options );
				System.exit(1);
			}

			int port = Integer.parseInt(line.getOptionValue("p"), 10);
			server = new Server(port);
			server.setHandler(new ParseHTTPHandler(new Parser(line.getOptionValue("t"))));
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			server.stop();
		}
	}
}
