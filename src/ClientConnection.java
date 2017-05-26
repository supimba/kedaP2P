import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.ServerLogger;


public class ClientConnection implements Runnable {

	private Socket clientSocket  ; 
	private ArrayList<String> clientFiles; 
	private PrintWriter out ; 
	private BufferedReader in ;  
	private ArrayList<Object> listAllClients ; 
	private static Logger logger = ServerLogger.getLogger() ;
	private ObjectInputStream objectInput ; 
	private ObjectOutputStream objectOutput ; 
	//TODO :  Envoie l'adresse ip puis envoie la liste des fichiers après confirmation
	//TODO : Demande confirmation de rengistrement

	public ClientConnection(Socket clientSocket, ArrayList<Object> listAllClients, ObjectInputStream objectInput) {
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

			//in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) ;
			//			out = new PrintWriter(clientSocket.getOutputStream(), true) ;
			while(true){
				actionChoice = (String) objectInput.readObject() ;
				System.out.println("Action " +actionChoice);
				switch(actionChoice){

				case "1":
					logger.log(Level.INFO, "Action registration (1) chosen") ;

					object = objectInput.readObject() ;
					registerClient(object);
					objectInput.close();

					logger.log(Level.INFO, "ObjectInputStream closed");
					break;

				case"2":
					System.out.println("ready to get files");
					ArrayList<Object> clientsArrayList = getClientsFilesList() ;

					objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
					objectOutput.writeObject(clientsArrayList);
					objectOutput.flush() ; 

					System.out.println("Flush");

					break;

				case "3":
					clientSocket.close();
					break;
				}

			}

		} catch ( ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}catch(SocketException e ){
			logger.log(Level.SEVERE, e.getMessage(), e);

		} catch (IOException e) {
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

		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println("File list was registred.");
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}

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

