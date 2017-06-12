package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a custom logger for our application
 * @author Daniel
 *
 */
public class ServerLogger {
	/**
	 * The base logger
	 */
	private static Logger logger;
	
	/**
	 * The util library to get useful functions
	 */
	private static Util u;	
	
	/**
	 * The filepath where logs will be saved
	 */
	private static String filePath = "logfile/";
	
	/**
	 * The log filename
	 */
	private static String fileName;	
	
	
	/**
	 * Default constructor
	 */
	public ServerLogger (){
		initLogger();
	}
	
	/**
	 * This methods inits the custom logger
	 */
	private static void initLogger(){
		// instanciate util object
		u = new Util();
		
		// get the current month and prepare file names
		String date = u.getDateMonth() ;
		fileName = date+".log" ;
		filePath += fileName ;
		
		// creates the logger
		logger = Logger.getLogger(fileName) ; 

		// create the file
		try {
			// opens the file in append mode (i.e. creates if not exists or add content if already exists) and set it as log destination
			FileHandler fh = new FileHandler(filePath, true) ;
			logger.addHandler(fh);

			// use a custom formatter for logs
			LogfileFormatter myFormatter = new LogfileFormatter();
			fh.setFormatter(myFormatter);
			
			// log the start of logging
			logger.log(Level.INFO, "Logger started");
		} catch (SecurityException e) {
			// log of severe exception
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			// log of severe exception
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	/**
	 * Get application's custom logger
	 * @return The application's logger
	 */
	public static Logger getLogger(){
		// init the logger if not already
		if(logger == null)
			initLogger();		
		
		return logger;
	}
}