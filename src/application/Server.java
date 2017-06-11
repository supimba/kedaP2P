package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ServerLogger;

/**
 * This class listens for connections on the server Socket and opens a thread for all new clients.
 * @author Daniel
 *
 */
public class Server {
	/**
	 * The server socket object allowing to make a connection with clients
	 */
	private ServerSocket mySkServer;
	
	/**
	 * The port used to make connections with clients
	 */
	private int serverPort;
	
	/**
	 * The client socket to make connections with a client
	 */
	private Socket clientSocket;
	
	/**
	 * The object that will allow to log all the necessary informations
	 */
	private Logger logger;
	
	/**
	 * A LinkedHashMap allowing to store all clients and retrieve them simply with their uuid as the key
	 */
	private LinkedHashMap<String, Client> clients;
	
	/**
	 * Default constructor. It will get the settings from the config file, then create the server socket and finally waits for client connections
	 */
	public Server() {
		// initiate logger and list of all clients
		logger = ServerLogger.getLogger();	
		logger.log(Level.INFO, "The server is running");
		
		getSettings(); 
		listeningServerSocket() ; 
	}

	/**
	 * Create the server socket and wait for client connections
	 */
	private void listeningServerSocket(){
		
		clients = new LinkedHashMap<>();

		try {
			// open server socket and listen
			mySkServer = new ServerSocket(serverPort);			
			logger.log(Level.INFO, "The server is waiting for a connection");
			ClientConnection clientConnection ;

			while(true){
				// accept client connection
				clientSocket = mySkServer.accept();
				logger.log(Level.INFO, "Connection from "+ clientSocket.getRemoteSocketAddress() +" accepted");
				clientConnection = new ClientConnection(clientSocket, clients);
				
				// initiate thread for new client connection
				Thread t = new Thread(clientConnection) ; 
				t.start();
				logger.log(Level.INFO, "Thread handle requests from client");
			}
		}catch (IOException e) {
			// Log the severe error
			logger.log(Level.SEVERE, e.getMessage(),e);			
					
		}
	}
	
	/**
	 * This method open the server settings and set the class properties that are stored in this file
	 */
	private void getSettings(){
		ResourceBundle bundle = ResourceBundle.getBundle("server.properties.config");
		serverPort = Integer.parseInt(bundle.getString("server.port"));
	}
}