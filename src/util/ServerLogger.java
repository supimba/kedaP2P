package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class ServerLogger {
	static private Logger logger  ;
	static private ServerLogger serverLogger ; 
	private FileHandler fh ;
	private String filePath = "logfile/" ;
	private Util u;
	private String fileName ; 
	
	static public Logger getLogger(){
		
		if(logger == null)
			serverLogger = new ServerLogger(); 
		
		return logger;
}

	public ServerLogger (){
		u = new Util() ; 
		String date = u.getDateMonth() ;
		fileName = date+".log" ;
		filePath += fileName ;

		logger = Logger.getLogger(fileName) ; 

		try {

			FileHandler fh = new FileHandler(filePath, true) ;
			logger.addHandler(fh);

			//use a custom formatter 
			LogfileFormatter myFormatter = new LogfileFormatter();
			fh.setFormatter(myFormatter);

		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
