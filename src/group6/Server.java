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

public class Server extends Thread{
	

	private ArrayList<Integer> list;
	private JTextField field;
	private JTextArea area;
	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel textPanel, controlPanel;
	private JButton sendToApp, sendToDatabase;
	
	private DatagramSocket serverSocket;
	private byte[] receiveData;
	private byte[] receiveDataAndroid;
	private static final int PORT = 6777;
	private int count;
	private InetAddress IPAddress;
	private int port;
	private DatagramPacket receivePacket;
	private DatagramPacket receivePacketAndroid;
	private DatagramSocket clientSocket;

	public Server() throws IOException{
		this(PORT);
	}
	
	public Server(int port) throws IOException{
		this.port = port;
		this.receiveData = new byte[8];
		this.receiveDataAndroid = new byte[1024];
		this.count = 0;
		clientSocket = new DatagramSocket();

		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(port == 6777){
			
		}else{
			System.out.println("\n To Android");
		}
		initGUI();
		
		
	}
	
	public void initGUI(){
		frame = new JFrame("Server");
		frame.setSize(350,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//list = new ArrayList<JButton>();
		frame.getContentPane().setLayout(new BorderLayout());
		
		textPanel = new JPanel();
		field = new JTextField("Values frome the sensor ");
		//textPanel.add(field);
		frame.getContentPane().add(field, BorderLayout.NORTH) ;
		area = new JTextArea(25,15);
	
		DefaultCaret caret = (DefaultCaret) area.getCaret(); 
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);      

		scrollPane = new JScrollPane(area);
		scrollPane.setViewportView(area);
		
		textPanel.add(scrollPane);
		frame.getContentPane().add(textPanel, BorderLayout.CENTER) ;
		
		controlPanel = new JPanel();
		frame.getContentPane().add(controlPanel, BorderLayout.SOUTH) ;
		frame.setVisible(true);
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
	
	public void run(){
		Server server = null;
		try {
			server = new Server(6799);
			System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        while(true){
        	if(port == 6777){
        		receivePacket = new DatagramPacket(this.receiveData, this.receiveData.length);
        		
        		try {
    				serverSocket.receive(receivePacket);
    				server.sendPacket(receivePacket.getData(), server.port, "172.17.79.92");
    				
                  } catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
                  }
                 
                  System.out.println("Packet [" + this.count + "] arrived with length " + receivePacket.getData().length);
                  this.count++;
                  
                  int[] ints = bytesToInts(receivePacket.getData());
                  System.out.println(ints[0]);
    			  area.append(ints[0] + "ppm \n");
    			  
    		}else{
    			
    			receivePacketAndroid = new DatagramPacket(this.receiveDataAndroid, this.receiveDataAndroid.length);
    			try {
					serverSocket.receive(receivePacketAndroid);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			String str = new String(receivePacketAndroid.getData(), StandardCharsets.UTF_8);
    			System.out.println("\n Android message: " + str);
    			field.setText(str);
    			area.append(str + "ppm \n");
    		}
                              
              
            if(!serverSocket.isBound()){
            	  break;
            }
        }
        System.out.println("\n Finished receving Data...");
	    serverSocket.close();
	    clientSocket.close();
        
    }
	

	
	public static void main(String [] args) throws IOException {	
	         Thread t = new Server();
	         t.start();
	}
	
	public void sendTo(String host, int port, byte[] array, InetAddress ip) throws UnknownHostException, IOException{
		DatagramSocket clientSocket = new DatagramSocket(port, ip);
		System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
		//netAddress.getByName("localhost");
		//sendPacket(array, clientSocket, port, ip);
			
	}
/*
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(sendToApp)){
			JOptionPane.showMessageDialog (null, "Sending data to app.....", "Title", JOptionPane.INFORMATION_MESSAGE);
			try {
				sendTo("localhost",PORT, list);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("app" );
		}
		else if (e.getSource().equals(sendToDatabase)){
			JOptionPane.showMessageDialog (null, "Sending data to Database.....", "Title", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("databse" );
		}
		
	}
	
*/	
}
