package group6;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;


public class ServerThread extends Thread {
	
	public static final String  CONFIRM_REQUEST = "ok done";  
	
	private DatagramSocket socket; // Our socket that receives values
	private int port; // port of the sensor client
	private InetAddress IPAddress; // IP of the sensor client
	private byte[] receiveData;
	private int count; // # of packets received from sensor
	
	private JTextField field;
	private JTextArea area; // Text area where sensors values will be appended
	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel textPanel, controlPanel;
	private JButton sendToApp, sendToDatabase;
	

	/**
	 *  Initialize the Ip/port of the client sensor
	 *  Initialize our socket and start the GUI
	 * @param port
	 * @param ip
	 * @throws SocketException
	 */
	public ServerThread(int port, InetAddress ip) throws SocketException {
		this.IPAddress = ip;
		this.port = port;
		socket = new DatagramSocket();
		receiveData = new byte[512];
		initGUI();
		
	}
	
	/**
	 * Uses parameter values to create a data-gram packet and the sends it to dst
	 * @param sendData
	 * @param port
	 * @param ipString
	 * @throws IOException
	 */
	public void sendPacket(byte[] sendData, int port, String ipString) throws IOException{
		InetAddress ip = InetAddress.getByName(ipString);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);	   
		socket.send(sendPacket);
	}
	
	
	public void run(){
		 
		//Before running the while loop, send a confirmation message 
		try {
			DatagramPacket sendPacket = new DatagramPacket(CONFIRM_REQUEST.getBytes(), CONFIRM_REQUEST.getBytes().length, IPAddress, port);
			this.socket.send(sendPacket);
			System.out.println("\n Server thread is accepting connection");
			
			// Start receiving values from the sensor
			 while(true){
	        	 DatagramPacket receivePacket = new DatagramPacket(this.receiveData, this.receiveData.length);
				 Arrays.fill(receiveData, (byte) 1 ); //clear the receiving byte array for the next message

	    		 socket.receive(receivePacket);
	    		 sendPacket(receivePacket.getData(), 6799, "172.17.88.232");// Send message to android

	             System.out.println("Packet [" + this.count + "] arrived with length " + receivePacket.getData().length);
	             this.count++; // increment packet count
	             String str = new String(receivePacket.getData()).trim();
	             System.out.println(str);
	    		 area.append(str + " ppm \n"); 

	            if(!socket.isBound()){ // Stop receiving values if socket is not bound
	            	  break;
	            }
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Create GUI
	 */
	public void initGUI(){
		frame = new JFrame("Server");
		frame.setSize(350,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		
		textPanel = new JPanel();
		field = new JTextField("Values frome the sensor ");
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

	
}
