package application;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ServerLogger;

/**
 * This class allows to open a connection with a client
 * 
 * The run method always wait for messages from the client. 
 * The messages can either be
 * <ul>
 * 		<li><b>registration</b> Indicates that a client wants to connect to the server. The server will then wait for a client object in the objectinputstream.</li>
 * 		<li><b>getfiles</b> Indicates that a client wants to get all clients connected. The server will send a LinkedHashMap with all clients.</li>
 * 		<li><b>getclientbyuuid</b> Indicates that a client wants a specific client's details. The server will first read the uuid passed by the client and then send the desired client.</li>
 * 		<li><b>quit</b> Indicates that a client wants to disconnect from the server. The server will simply first read the uuid passed by the client and then remove the client from the LinkedHashMap.</li>
 * </ul>
 * @author Daniel
 *
 */
public class ClientConnection implements Runnable {
	/**
	 * The object that will allow to log all the necessary informations
	 */
	private static Logger logger;
	
	/**
	 * The socket for client connection
	 */
	private Socket clientSocket;
	
	/**
	 * This contains the client's ip address	
	 */
	private SocketAddress connectedClientIP;
	
	/**
	 * A list containing all registered clients
	 */
	private LinkedHashMap<String, Client> clients;
	
	/**
	 * This will be used to get informations from client
	 */
	private ObjectInputStream objectInput; 
	
	/**
	 * This will be used to send informations to the client
	 */
	private ObjectOutputStream objectOutput;
	
	/**
	 * This allows to store the client's choice
	 */
	private String actionChoice;

	/**
	 * Default constructor
	 * @param clientSocket A socket object for client connection
	 * @param clients A LinkedHashMap containing all connected clients
	 */
	public ClientConnection(Socket clientSocket, LinkedHashMap<String, Client> clients) {
		this.clientSocket = clientSocket ;
		this.clients = clients;
		this.connectedClientIP = clientSocket.getRemoteSocketAddress() ; 
	}

	/*
	 * Wait for the actions by the client:
	 * - registration
	 * - get files shared by other clients
	 * - get client by his uuid
	 * - quit 
	 */
	@Override
	public void run() {
		// get the logger object
		logger = ServerLogger.getLogger() ; 

		try {
			// open a object input stream 
			objectInput = new ObjectInputStream(clientSocket.getInputStream())  ;

			while(true){
				// get action from client in the ObjectInputStream
				actionChoice = (String) objectInput.readObject();
			
				switch(actionChoice){
					case "registration":

	
						
						try{
							// get the Client Object in the object input stream
							Client c = (Client) objectInput.readObject();
							logger.log(Level.INFO, "Application for registration of client UUID["+c.getUuid()+"]") ;
							
							// register the client in the list of all clients
							registerClient(c);
						}catch(ClassCastException e){
							// Error of registration
							logger.info("Client registration with socket address " + clientSocket.getRemoteSocketAddress() + " impossible");
						}
						
						break;	
					case"getfiles":
						// log the desired action
						logger.log(Level.INFO, "Start to retrieve clients information and files list");
	
						// send to the client the clients list
						objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
						objectOutput.writeObject(clients);
						objectOutput.flush() ;
						
						// log the sent files
						logger.log(Level.INFO, "Clients information and files list sent");
						
						break;						
					case"getclientbyuuid":
						// log the desired action
						logger.log(Level.INFO, "Client informations asked");
						
						// get uuid of desired client
						String uuid = (String) objectInput.readObject();
	
						// send desired client
						objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
						objectOutput.writeObject(clients.get(uuid));
						objectOutput.flush() ;
						
						// log the client's send
						logger.log(Level.INFO, "Clients information and files list sent");
						
						break;						
					case"quit":
						// get uuid of client that wants to disconnect
						uuid = (String) objectInput.readObject();
						
						// log the desired action
						logger.log(Level.INFO, "Client UUID["+ uuid +"] wants to disconnect, client socket will instantly close");
						clientSocket.close() ; 
	
						// remove client of list and log the error or the successful deletion
						if(clients.remove(uuid) != null)						
							logger.log(Level.INFO, "Client UUID["+uuid +"] successfully deleted from users list");
						else
							logger.log(Level.INFO, "Client UUID["+uuid+"] couldn't be deleted");
						
						ServerLogger.closeFH();						
						break;
				}
			}
		}catch ( ClassNotFoundException e) {
			// log severe error
			logger.log(Level.SEVERE, e.getMessage(), e);
			
		}catch(SocketException e ){
			// log severe error
			if(clientSocket.isClosed()){
			logger.log(Level.WARNING, "Socket connection with socket adress "+ connectedClientIP + " closed") ;

			}
		else
			logger.log(Level.SEVERE, e.getMessage(), e);
			
		}catch(EOFException e){
			// log severe error
			logger.log(Level.WARNING, "ObjectInput with socket adress"+connectedClientIP+" closed", e) ;			
		}catch (IOException e) {			
			// verify if the client socket was lost or closed
			if(clientSocket.isClosed())
				logger.log(Level.WARNING, "Socket connection with socket adress "+ connectedClientIP +" closed") ;
			else
				logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * This allows to register a client to the server by adding it to the LinkedHashMap
	 * @param object The client
	 */
	private void registerClient(Client client){		
		// modify client informations if already registered or save its informations
		if(clients.containsKey(client.getUuid()))
			clients.replace(client.getUuid(), client);
		else
			clients.put(client.getUuid(), client) ;
		
		// log the file list sent by the client
		printFilesListArray(client.getFiles(), client.getUuid());
		
		// end of registration
		logger.info("Registration from " + clientSocket.getRemoteSocketAddress() + " with client UUID ["+client.getUuid() +"] complete");
	}

	/**
	 * This method allows to log all shared files by the client
	 * @param array An array of string containing all the files
	 */
	public void printFilesListArray(ArrayList<String> array, String uuid){
		logger.log(Level.INFO, "Files shared by the client UUID["+uuid+"] : ");
		for(String files : array)
			logger.log(Level.INFO, files);
	}
}