package org.treedecor.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Provides methods for calling external commands.
 * 
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class CommandExecution {
	private static class Logger extends Thread {
		private final InputStream in;
		private final StringBuffer sb = new StringBuffer();

		public Logger(InputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		public String toString() {
			return sb.toString();
		}
	}
	
	public static String callSync(String cmd, String[] args, String in) throws IOException, InterruptedException {
		String[] cmds = new String[args.length+1];
		System.arraycopy(args, 0, cmds, 1, args.length);
		cmds[0] = cmd;
		
		Process process = Runtime.getRuntime().exec(cmds);
		
		Logger logger = new Logger(process.getInputStream());
		logger.start();
		
		OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
		osw.write(in);
		osw.flush();
		
		process.waitFor();
		return logger.toString();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(CommandExecution.callSync("dir", new String[0], ""));
	}
}
