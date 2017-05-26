import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.imageio.spi.RegisterableService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import util.ServerLogger;


public class CopyOfClientConnection implements Runnable {

	private Socket clientSocket  ; 
	private ArrayList<String> clientFiles; 
	private PrintWriter out ; 
	private BufferedReader in ;  
	private ArrayList<Object> listAllClients ; 
	private String ipAdress ;
	private static Logger logger = ServerLogger.getLogger() ;
	private ObjectInputStream objectInput ; 


	//TODO :  Envoie l'adresse ip puis envoie la liste des fichiers après confirmation
	//TODO : Demande confirmation de rengistrement



	public CopyOfClientConnection(Socket clientSocket, ArrayList<Object> listAllClients, ObjectInputStream objectInput) {
		this.clientSocket = clientSocket ;
		this.listAllClients = listAllClients;
		this.objectInput = objectInput ; 
		//		this.ipAdress = clientSocket.getRemoteSocketAddress().toString() ;


	}

	/*
	 * The class run() may ask clients if they want to 
	 * register (1)
	 * or get the files list (2)from the others clients
	 */
	@Override
	public void run() {
		String actionChoice ;
		String ipAdress ;
		Object object ;


		try {

			//			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) ;
			//			out = new PrintWriter(clientSocket.getOutputStream(), true) ; 

			//			objectInput = new ObjectInputStream(clientSocket.getInputStream())  ; 
			
//			object = (Object) objectInput.readObject() ;
			HashMap hm = (HashMap) objectInput.readObject() ;
			System.out.println("IP : " + hm.get("IP"));
			ArrayList<String> array = (ArrayList<String>) hm.get("ClientFiles") ; 
			
//			System.out.println(array.get(0));
			for(String files : array)
				System.out.println(files);
					
					
// Cette partie fonctionne pour récupérer uniquement une ArrayList
			//			while( (object = (Object) objectInput.readObject()) != null )
			//				// METTRE Une seule ArrayList c'est plus simple
			//				if ( object instanceof String)
			//					System.out.println("IP " + objectInput.readObject());
			//			
			//				else{
			//					ArrayList<String> fileList ;
			//					fileList = (ArrayList<String>) object ; 
			//					
			//					for(String files : fileList)
			//						System.out.println("Files " + files );
			//					
			//				}



			//			out.println("Chose option :");
			//			out.flush();
			//			out.println("1 to register");
			//			out.flush();
			//			out.println("2 to get file list");
			//			out.flush();
			//			out.println("3 to to quit ");


			while(true){
				actionChoice = in.readLine() ; 

				System.out.println("Client choice : ");
				System.out.println(actionChoice);

				switch(actionChoice){
				case "1": 
					registerClient();
					break;

				case"2":
					System.out.println("ready to get files");					
					break;

				case "3":
					clientSocket.close();
					break;
				default:
					out.println("Chose option :");
					out.flush(); 
					out.println("1 to register :");
					out.println("2 to get file list :");
					out.println("3 to to quit :");
					break;
				}


			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void registerClient(){

		// récupérer directemnet un objet ArrayList  : https://docs.oracle.com/javase/tutorial/essential/io/objectstreams.html
		clientFiles = new ArrayList<String>() ;
		clientFiles.add(ipAdress) ; 
		printArray();

		try {

			while(in.readLine()!=null){

				clientFiles.add(in.readLine());

			}

			listAllClients.add(clientFiles) ;
			logger.info("Registration from "+clientSocket.getRemoteSocketAddress()+" complete.");
			printArray();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ask client if he accept to share the list of files
	}

	public void printArray(){

		for(String files : clientFiles)
			System.out.println(files);

	}

}

