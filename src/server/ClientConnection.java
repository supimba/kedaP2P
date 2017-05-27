package server;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.ServerLogger;


public class ClientConnection implements Runnable {

	private static Logger logger ;

	private Socket clientSocket  ; 
	private ArrayList<String> clientFiles; 
	private PrintWriter out ; 
	private BufferedReader in ;  
	private ArrayList<Object> listAllClients ; 
	private ObjectInputStream objectInput ; 
	private ObjectOutputStream objectOutput ;
	private Object object ;
	private String actionChoice ; 

	//TODO :  Envoie l'adresse ip puis envoie la liste des fichiers après confirmation
	//TODO : Demande confirmation de rengistrement

	public ClientConnection(Socket clientSocket, ArrayList<Object> listAllClients) {
		this.clientSocket = clientSocket ;
		this.listAllClients = listAllClients;
		//		this.objectInput = objectInput ; 
		//		this.ipAdress = clientSocket.getRemoteSocketAddress().toString() ;


	}

	/*
	 * Wait for the actions by the client:
	 * - registration
	 * - get files shared by other clients
	 */
	@Override
	public void run() {
		logger = ServerLogger.getLogger() ; 

		try {
			// open a object input stream 
			objectInput = new ObjectInputStream(clientSocket.getInputStream())  ;

			//in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) ;
			//out = new PrintWriter(clientSocket.getOutputStream(), true) ;
			while(true){

				// get action from client in the ObjectInputStream
				actionChoice = (String) objectInput.readObject() ;

				switch(actionChoice){
				case "registration":
					logger.log(Level.INFO, "Action 'registration' chosen") ;

					// get the object containing the client information
					object = objectInput.readObject() ;

					// register the client in the list of all clients
					registerClient(object);
					objectInput.close();
					logger.log(Level.INFO, "ObjectInputStream closed");
					break;

				case"getfiles":
					System.out.println("ready to get files");
					ArrayList<Object> clientsArrayList = getClientsFilesList() ;

					objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
					objectOutput.writeObject(clientsArrayList);
					objectOutput.flush() ;
					logger.log(Level.INFO, "Clients information and files list sent");

					break;

				}

			}

		} catch ( ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);


		}catch(SocketException e ){
			logger.log(Level.SEVERE, e.getMessage(), e);

		}catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);

		}

	}

	private void registerClient(Object object){

		clientFiles = (ArrayList<String>) object ; 

		//TODO : A supprimer
		// récupérer directemnet un objet ArrayList  : https://docs.oracle.com/javase/tutorial/essential/io/objectstreams.html
		//		clientFiles = new ArrayList<String>() ;
		//		clientFiles.add(ipAdress) ;

		// TODO: A supprimer
		printArray(clientFiles);
		listAllClients.add(clientFiles) ;
		logger.info("Registration from "+clientSocket.getRemoteSocketAddress()+" complete.");

		//		try {
		//			out = new PrintWriter(clientSocket.getOutputStream(), true);
		//			out.println("File list was registred.");
		//			out.flush();
		//			out.close();
		//		} catch (IOException e) {
		//			logger.log(Level.SEVERE, e.getMessage(), e);
		//			e.printStackTrace();
		//		}

		printObjectList();

	}
	private ArrayList<Object> getClientsFilesList() {

		return listAllClients ; 

	}

	public void printArray(ArrayList<String> array){

		for(String files : array)
			logger.log(Level.INFO, "Filename : \"" + files+ "\"");

	}

	public void printObjectList(){

		for(int i =0 ; i<  listAllClients.size(); i++ ){
			printArray( (ArrayList<String>)listAllClients.get(i) ) ; 
		}

	}

}

