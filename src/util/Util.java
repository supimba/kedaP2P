package util;


import java.text.SimpleDateFormat;
import java.util.Date;


public class Util {

	public String getTimeStamp(){
		String timestampString = null; 

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		timestampString  = dateFormat.format(new Date());
		

		return  timestampString;
	}
	
	public String getDateMonth(){
		String date = null ; 
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MMMM") ;
		date  = dateFormat.format(new Date());

		return date ; 
	}

}
