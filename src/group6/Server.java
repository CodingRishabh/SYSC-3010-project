package group6;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 
 * @author Rishabh Singh
 *
 */
public class Server{
	
	private ArrayList<ServerThread> threadList; // list of all the clients connected to server
	private DatagramSocket serverSocket;
	private byte[] receiveData;
	private static final int PORT = 6777;
	private DatagramPacket receivePacket;

	public Server() throws IOException{
		this(PORT);
	}
	
	public Server(int port) throws IOException{
		this.receiveData = new byte[512];
		threadList = new ArrayList<ServerThread>();
		receivePacket = new DatagramPacket(receiveData, receiveData.length);	

		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runServer() throws IOException{
		System.out.println("\n Server is accepting connection");

		while(true){
    		receivePacket = new DatagramPacket(receiveData, receiveData.length);	
			serverSocket.receive(receivePacket);
			String clientName = new String(receivePacket.getData()).trim(); 
		
			// If it is valid message then create and start server thread which will be communicating
			//with the sensor. Server returns back to listening for other connections
			if(processPacket(clientName)){
				ServerThread t = new ServerThread(receivePacket.getPort(),receivePacket.getAddress(), clientName);
				threadList.add(t);
				System.out.println("\n Connected to : " + receivePacket.getPort() + receivePacket.getAddress());
				t.start();
			}
		}
	}
	
	/**
	 * return true if the message contains "con"
	 * @param message that needs to be verified
	 * @return
	 */
	public boolean processPacket(String message){
		return message.startsWith("pc") || message.startsWith("android");
	}
	
	public ArrayList<ServerThread> getThreadList(){
		return threadList;
	}
	
	
	public static void main(String [] args) throws IOException {	
	         new Server().runServer();
	}
	
	public byte[] getData(){
		return this.receiveData;
	}

	public DatagramSocket getSocket(){
		return serverSocket;
	}
	
	public DatagramPacket getPacket(){
		return receivePacket;
	}
	
}
