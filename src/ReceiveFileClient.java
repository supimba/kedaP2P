import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;


public class ReceiveFileClient implements Runnable{
	private Socket clientSocket = null ; 
	private InetAddress srvAdress ;
	private String srvName = "192.168.108.10";

	private String messageIn;
	private BufferedReader in = null ; 

	private int bytesRead;
	private int current = 0;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private File filename ; 



	public ReceiveFileClient(Socket clientSocket) {
		this.clientSocket = clientSocket ; 
//		this.filename = filename ; 
	}


	@Override
	public void run() {
		System.out.println("In run from ReceiveFileClient");

		try{
			byte [] mybytearray  = new byte [1024];
			InputStream is = clientSocket.getInputStream();
			
			
			fos = new FileOutputStream("Untitled4.txt");
			bos = new BufferedOutputStream(fos);
			bytesRead = is.read(mybytearray,0,mybytearray.length);
			current = bytesRead;

			bos.write(mybytearray, 0 , bytesRead);
			bos.flush();
			bos.close();
			
			do {
				bytesRead =
						is.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead >= 0) current += bytesRead;
			} while(bytesRead > -1);


			System.out.println("File " 
					+ " downloaded (" + current + " bytes read)");
		}catch (IOException e) {
			// TODO: handle exception
		}

	}

}
