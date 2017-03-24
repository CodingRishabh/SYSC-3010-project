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
			String message = new String(receivePacket.getData()); 
		
			// If it is valid message then create and start server thread which will be communicating
			//with the sensor. Server returns back to listening for other connections
			if(processPacket(message)){
				ServerThread t = new ServerThread(receivePacket.getPort(),receivePacket.getAddress());
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
		return message.startsWith("con");
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
