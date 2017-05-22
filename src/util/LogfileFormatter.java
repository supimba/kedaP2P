package util;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogfileFormatter extends Formatter {

	public LogfileFormatter() {
		super() ; 
	}
	
	
	@Override
	public String format(LogRecord record) {
		// Create a StringBuffer to contain the formatted record
					StringBuffer sb = new StringBuffer();
					String timestamp ;
					
					// Get timestamp
					Util u = new Util();
					timestamp = u.getTimeStamp() ; 
					
					// Format log with timestamp, class name, level and message
					sb.append(timestamp);
					sb.append(" ("+ record.getSourceClassName()+"."+record.getSourceMethodName()+") ");
					sb.append(" ["+record.getLevel().getName()+"] ");
					sb.append(formatMessage(record));
					sb.append("\r\n");

					return sb.toString();
	
	}

}
