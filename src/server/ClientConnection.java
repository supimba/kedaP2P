package server;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
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
	private SocketAddress connectedClientIP ; 


	public ClientConnection(Socket clientSocket, ArrayList<Object> listAllClients) {
		this.clientSocket = clientSocket ;
		this.listAllClients = listAllClients;
		this.connectedClientIP = clientSocket.getRemoteSocketAddress() ; 
	}

	/*
	 * Wait for the actions by the client:
	 * - registration
	 * - get files shared by other clients
	 */
	@Override
	public void run() {
//		logger = ServerLogger.getLogger() ; 

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
					break;

				case"getfiles":
					logger.log(Level.INFO, "Start to retrieve clients information and files list");
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

		}catch(EOFException e){
			logger.log(Level.INFO, "ObjectInput closed") ;

			
		}catch (IOException e) {
			
			// verify if the client socket was lost or closed
			if(clientSocket.isClosed())
				logger.log(Level.INFO, "Connection with the client from "+ connectedClientIP +" closed") ;
			else
				logger.log(Level.SEVERE, e.getMessage(), e);

		}

	}

	private void registerClient(Object object){

		clientFiles = (ArrayList<String>) object ; 
		
		//log the file list send by the client
		printArray(clientFiles);
		listAllClients.add(clientFiles) ;
		logger.info("Registration from "+clientSocket.getRemoteSocketAddress()+" complete");

		System.out.println("COMPLETE ");

	}
	private ArrayList<Object> getClientsFilesList() {

		return listAllClients ; 

	}

	public void printArray(ArrayList<String> array){
		
		logger.log(Level.INFO, "Shared files:");
		for(String files : array)
			logger.log(Level.INFO, files);

	}

}

