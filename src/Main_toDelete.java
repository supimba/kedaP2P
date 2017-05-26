
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main_toDelete {

	public static void main(String[] args) {

		downloadFile("to_replace") ;  




	}

	public static void downloadFile(String filename){
		ObjectOutputStream objectOutput ;
		String clientAdress = "192.168.108.1" ; 
		System.out.println("Is connecting ");

		Socket clientSocket;
		try {
			clientSocket = new Socket(clientAdress, 5600);
			System.out.println("Socket Open");
			
			
			ReceiveFileClient download = new ReceiveFileClient(clientSocket) ;
			Thread t = new Thread(download) ; 
			t.start();
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} 

		
	}
}



