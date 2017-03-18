package group6;
import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class Server{
	

	private ArrayList<ServerThread> threadList;

	
	private DatagramSocket serverSocket;
	private byte[] receiveData;
	
	private static final int PORT = 6777;
	private int count;
	private InetAddress IPAddress;
	private int port;
	private DatagramPacket receivePacket;
	private DatagramSocket clientSocket;

	String message;

	public Server() throws IOException{
		this(PORT);
	}
	
	public Server(int port) throws IOException{
		this.port = port;
		this.receiveData = new byte[512];
		threadList = new ArrayList<ServerThread>();
		this.count = 0;
		clientSocket = new DatagramSocket();
		message = "";
		receivePacket = new DatagramPacket(receiveData, receiveData.length);	

		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public byte[] getData(){
		return this.receiveData;
	}
	public int getPort(){
		return this.port;
	}
	public int getCount(){
		return this.count;
	}
	public DatagramSocket getSocket(){
		return serverSocket;
	}
	public DatagramPacket getPacket(){
		return receivePacket;
	}
	public String getMessage(){
		return message;
	}
	
	public void runServer() throws IOException{
		System.out.println("\n Server is accepting connection");

		while(true){
    		
    		receivePacket = new DatagramPacket(receiveData, receiveData.length);	
			serverSocket.receive(receivePacket);
			String message = new String(receivePacket.getData());
		
			if(processPacket(message)){
				ServerThread t = new ServerThread(receivePacket.getPort(),receivePacket.getAddress());
				threadList.add(t);
				System.out.println("\n Connected to : " + receivePacket.getPort() + receivePacket.getAddress());
				t.start();
			}
		}
	}
	
	public boolean processPacket(String message){
		return message.startsWith("con");
	}
	
	public ArrayList<ServerThread> getThreadList(){
		return threadList;
	}
	

	
	
	/**
	 * 
	 * @param sendData
	 * @throws IOException 
	 */
	public void sendPacket(byte[] sendData, int port, String ipString) throws IOException{
		InetAddress ip = InetAddress.getByName(ipString);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);	   
		this.clientSocket.send(sendPacket);
	
	}

	
	public static void main(String [] args) throws IOException {	
	         new Server().runServer();
	}
	
	public void sendTo(String host, int port, byte[] array, InetAddress ip) throws UnknownHostException, IOException{
		DatagramSocket clientSocket = new DatagramSocket(port, ip);
		System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
		//netAddress.getByName("localhost");
		//sendPacket(array, clientSocket, port, ip);
			
	}

	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public  static int[] bytesToInts(byte[] bytes) {
	    int[] ints = new int[bytes.length / 4];
	    ByteBuffer.wrap(bytes).asIntBuffer().get(ints);
	    return ints;
	}
	
	/**
	 * 
	 * @param ints
	 * @return
	 */
	public byte[] intsToBytes(int[] ints) {
	    ByteBuffer bb = ByteBuffer.allocate(ints.length * 4);
	    IntBuffer ib = bb.asIntBuffer();
	    for (int i : ints) ib.put(i);
	    return bb.array();
	}

	/**
	 * 
	 * @param right
	 * @param left
	 * @return
	 */
	/*
	public byte[] createPacket(int sata){
		
		int[] data = new int[1];
		data[0] = sata;

		byte[] newData = intsToBytes(data);
		
		return newData;
	}
	*/
}
