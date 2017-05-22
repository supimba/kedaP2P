import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.spi.RegisterableService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;


public class ClientConnection implements Runnable {

	private Socket clientSocket  ; 
	private ArrayList<String> clientFiles; 
	private PrintWriter out ; 
	private BufferedReader in ;  
	private ArrayList<Object> listAllClients ; 
	private String ipAdress ; 
	//TODO :  Envoie l'adresse ip puis envoie la liste des fichiers après confirmation
	//TODO : Demande confirmation de rengistrement



	public ClientConnection(Socket clientSocket, ArrayList<Object> listAllClients) {
		this.clientSocket = clientSocket ;
		this.listAllClients = listAllClients; 
		this.ipAdress = clientSocket.getRemoteSocketAddress().toString() ;
		
		
	}

	/*
	 * The class run() may ask clients if they want to 
	 * register (1)
	 * or get the files list (2)from the others clients
	 */
	@Override
	public void run() {
		String actionChoice ; 

		try {

			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) ;
			out = new PrintWriter(clientSocket.getOutputStream(), true) ; 

			out.println("Chose option :");
			out.flush();
			out.println("1 to register");
			out.flush();
			out.println("2 to get file list");
			out.flush();
			out.println("3 to to quit ");


			while(true){
				actionChoice = in.readLine() ; 

				System.out.println("Client choice : ");
				System.out.println(actionChoice);

				switch(actionChoice){
				case "1": 
					System.out.println("ready to register");
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
		} catch (IOException e) {
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

