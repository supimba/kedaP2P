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

public class ClientConnection implements Runnable {

	private static Logger logger;
	private Socket clientSocket; 
	private ArrayList<String> clientFiles;
//	private ArrayList<Object> listAllClients;
	private LinkedHashMap<String, Client> clients;
	private ObjectInputStream objectInput; 
	private ObjectOutputStream objectOutput;
	private Object object;
	private String actionChoice; 
	private SocketAddress connectedClientIP;

	public ClientConnection(Socket clientSocket, LinkedHashMap<String, Client> clients) {
		this.clientSocket = clientSocket ;
		this.clients = clients;
		this.connectedClientIP = clientSocket.getRemoteSocketAddress() ; 
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

			while(true){
				// get action from client in the ObjectInputStream
				actionChoice = (String) objectInput.readObject();
			
				switch(actionChoice){
					case "registration":
						logger.log(Level.INFO, "Action 'registration' chosen") ;
	
						// get the object containing the client information
						object = objectInput.readObject();
	
						// register the client in the list of all clients
						registerClient(object);
						break;
	
					case"getfiles":
						logger.log(Level.INFO, "Start to retrieve clients information and files list");
						LinkedHashMap<String, Client> clientsArrayList = getClientsFilesList();
	
						objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
						objectOutput.writeObject(clientsArrayList);
						objectOutput.flush() ;
						
						logger.log(Level.INFO, "Clients information and files list sent");
						break;
						
					case"getclientbyuuid":
						logger.log(Level.INFO, "Client informations asked");
						String uuid = (String) objectInput.readObject();
	
						objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
						objectOutput.writeObject(clients.get(uuid));
						objectOutput.flush() ;
						
						logger.log(Level.INFO, "Clients information and files list sent");
						break;
						
					case"quit":
						uuid = (String) objectInput.readObject();
						logger.log(Level.INFO, "Client wants to disconnect [" + uuid + "]");						
	
						if(clients.remove(uuid) != null)						
							logger.log(Level.INFO, "Client successfully deleted from users list");
						else
							logger.log(Level.INFO, "Client couldn't be deleted");
						
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
		// Get client object
		Client client = (Client) object ;
		
		// Modify client informations if already registered or save its informations
		if(clients.containsKey(client.getUuid()))
			clients.replace(client.getUuid(), client);
		else
			clients.put(client.getUuid(), client) ;
		
		System.out.println(clients.size());
		
		for (String key : clients.keySet())
		{
			Client c = clients.get(key);
		    System.out.println(c.getClientIp() + " - " + c.getUuid() );
		    for (String string : c.getFiles()) {
				System.out.println(string);
			}
		}
		
		// log the file list sent by the client
		printArray(client.getFiles());
		
		// End of registration
		logger.info("Registration from " + clientSocket.getRemoteSocketAddress() + " complete");
	}
	
	private LinkedHashMap<String, Client> getClientsFilesList() {
		return clients ; 

	}

	public void printArray(ArrayList<String> array){		
		logger.log(Level.INFO, "Shared files:");
		for(String files : array)
			logger.log(Level.INFO, files);

	}
}