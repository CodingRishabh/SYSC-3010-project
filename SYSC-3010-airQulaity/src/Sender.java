import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.io.OutputStream;



public class Sender {
	
	private Socket clientSocket;
	private OutputStream outToServer;
	private DataOutputStream out;
	
	public Sender(int port) throws IOException, IOException{
		clientSocket = new Socket("localhost", port);
	}
	
	public void setUpStream(Socket s) throws IOException{
		setOutputStream(s);
		setDataOutputStream(getOutputStream());
        System.out.println("Just connected to " + s.getRemoteSocketAddress());
	}
	
	//set and get output stream
	public void setOutputStream(Socket s) throws IOException{
		outToServer = s.getOutputStream();
	}
	public OutputStream getOutputStream(){
		return outToServer;
	}
	
	
	//set and get data output stream
	public void setDataOutputStream(OutputStream o){
		out = new DataOutputStream(o);
	}
	public DataOutputStream getDataOutputStream(){
		return out;
	}
	
	
	public Socket getClientSocket(){
		return clientSocket;
	}
	
	public void closeEverything(OutputStream os, DataOutputStream dos, Socket cs) throws IOException{
		dos.flush();
        os.close();
        dos.close();
        cs.close();
	}

	public static void main(String args[]) throws IOException{
		
		try {
	        System.out.println("Connecting to " + "localhost" + " on port " + 6789);
			Sender sender = new Sender(6789);
			sender.setUpStream(sender.clientSocket);

	         for(int i = 0; i < 500; i++){

	        	 sender.getDataOutputStream().writeInt(i);
	        	 System.out.println(i);
	         }
	         
	         sender.closeEverything(sender.getOutputStream(), sender.getDataOutputStream(), sender.getClientSocket());
	      }catch(IOException e) {
	         e.printStackTrace();
	      }
		
	}
	
	public int getInt(){
		
		return ThreadLocalRandom.current().nextInt(9, 999 + 1);
	}
}
