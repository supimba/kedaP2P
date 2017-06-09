package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ServerLogger;

/*
 * This class listen connections on the server Socket
 * and open a thread for all new clients.
 */
public class Server {
	private ServerSocket mySkServer;
	private int serverPort ; 
	private Socket clientSocket;
	private Logger logger ; 
	private ArrayList<Object> listAllClients ;
	
	public Server() {
		getResources(); 
		listeningServerSocket() ; 
	}

	private void listeningServerSocket(){
		// initiate logger and list of all clients
		logger = ServerLogger.getLogger() ;
		listAllClients = new ArrayList<Object>() ; 

		try {
			// open server socket and listen
			mySkServer = new ServerSocket(serverPort) ; 
			
			logger.log(Level.INFO, "The server is waiting for connection") ;
			ClientConnection clientConnection ;

			while(true){
				// accept client connection
				clientSocket = mySkServer.accept() ;
				logger.log(Level.INFO, "Connection from "+ clientSocket.getRemoteSocketAddress() +" was accepted");

				clientConnection = new ClientConnection(clientSocket, listAllClients) ; 
				
				// initiate thread for new client connection
				Thread t = new Thread(clientConnection) ; 
				t.start();
				logger.log(Level.INFO, "Thread handle request from client");
			}
		}catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(),e);
		}
	}
	
	private void getResources(){
		ResourceBundle bundle = ResourceBundle.getBundle("server.properties.config");
		serverPort = Integer.parseInt(bundle.getString("server.port"));
	}
}