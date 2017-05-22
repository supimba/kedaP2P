import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;


import util.ServerLogger;


public class Server {
	private static InetAddress localAdress =null;
	private static ServerSocket mySkServer; 
	private static Socket clientSocket  = null ;
	private static NetworkInterface ni;
	private static Logger logger = ServerLogger.getLogger() ; 
	
	

	public static void main(String[] args) {
		ArrayList<Object> listAllClients = new ArrayList<Object>();

		try {
			ni = NetworkInterface.getByName("eth1");
			Enumeration<InetAddress> inetAdress = ni.getInetAddresses() ;

			while(inetAdress.hasMoreElements())
			{
				InetAddress adress = inetAdress.nextElement() ; 
				if(!adress.isLoopbackAddress() && !adress.isLinkLocalAddress())localAdress = adress ;
			}

			mySkServer = new ServerSocket(45000, 5, localAdress) ; 
			ClientConnection clientConnection ; 

			//			mySkServer.setSoTimeout(300000);

			//TODO Mettre un log d'info pour la connexion
			logger.info("Wait for connection") ; 



			while(true){
				clientSocket = mySkServer.accept() ;
				System.out.println("Client accepted");

				clientConnection = new ClientConnection(clientSocket, listAllClients) ; 

				Thread t = new Thread(clientConnection) ; 
				t.start();

			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

}
