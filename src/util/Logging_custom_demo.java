package util ; 


import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging_custom_demo {
	/**
	 * @param args
	 */
	//extend the current Formatter
	public static class SocketFormatter extends Formatter {

		public SocketFormatter() {
			super();
		}

		public String format(LogRecord record) {

			// Create a StringBuffer to contain the formatted record
			StringBuffer sb = new StringBuffer();

			// Get the date from the LogRecord and add it to the buffer
			Date date = new Date(record.getMillis());
			sb.append(date.toString());
			sb.append(";");

			sb.append(record.getSourceClassName());
			sb.append(";");

			// Get the level name and add it to the buffer
			sb.append(record.getLevel().getName());
			sb.append(";");

			sb.append(formatMessage(record));
			sb.append("Hello world");
			sb.append(";");
			sb.append("\r\n");

			return sb.toString();
		}
	}

	// Define the level of log
	// 1 = info
	// 2 = severe    
	public static void main(String[] args){

		//Get the logger
		Logger myLogger = Logger.getLogger("TestLog");

		try{
			// define a new file handler and its log
			FileHandler fh = new FileHandler("logfile/my.log",true);

			//add the handle to the log            
			myLogger.addHandler(fh);

			//use a custom formatter 
			SocketFormatter myFormatter = new SocketFormatter();
			fh.setFormatter(myFormatter);
			myLogger.setLevel(Level.INFO);
			//do some logs
			myLogger.info("this is the info level");
			myLogger.setLevel(Level.WARNING);
			//do some logs
			myLogger.warning("this is the warning level");
			myLogger.setLevel(Level.SEVERE);
			//do some logs
			myLogger.severe("this is the severe level");

		} catch (RuntimeException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
