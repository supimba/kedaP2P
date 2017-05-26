import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;




import util.ServerLogger;


public class Server {

	private static ServerSocket mySkServer; 
	private static Socket clientSocket  = null ;
	private static Logger logger = ServerLogger.getLogger() ; 
	private static ArrayList<Object> listAllClients = new ArrayList<Object>();


	public static void main(String[] args) {
		listeningSocket() ; 
	}



	private static void listeningSocket(){

		try {
			mySkServer = new ServerSocket(45006) ; 

			ClientConnection clientConnection ;
			logger.log(Level.INFO, "The server is waiting for connection") ; 

			while(true){
				clientSocket = mySkServer.accept() ;
				logger.log(Level.INFO, "Connection from "+ clientSocket.getRemoteSocketAddress() +" was accepted");

				ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream())  ;
				clientConnection = new ClientConnection(clientSocket, listAllClients, objectInput) ; 

				Thread t = new Thread(clientConnection) ; 
				t.start();
				logger.log(Level.INFO, "Thread handle request from client");
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(),e);
		}

	}

}
