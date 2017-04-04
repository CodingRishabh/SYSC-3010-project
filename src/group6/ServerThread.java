package group6;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;

/**
 * 
 * @author Rishabh Singh
 *
 */
public class ServerThread extends Thread {
	
	public static final String  CONFIRM_REQUEST = "ok done";  
	private String Android_IP = "172.17.91.121";
	public static final int ANDROID_PORT = 6799;
	public static final String DATABASE_IP = "192.168.43.26";
	public static final int DATABASE_PORT = 2046;
	private final int THRESHOLD=250;
	
	private DatagramSocket socket; // Our socket that receives values
	private int port; // port of the sensor client
	private InetAddress IPAddress; // IP of the sensor client
	private byte[] receiveData;
	
	private int count; // # of packets received from sensor
	private String client;
	
	private JLabel label;
	private JTextArea area; // Text area where sensors values will be appended
	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel textPanel, container1;
	private JTextField ipField;
	private JButton changeIP;
	private DateFormat df;
	private Date dateobj;
	
	/**
	 *  Initialize the Ip/port of the client sensor
	 *  Initialize our socket and start the GUI
	 * @param port
	 * @param ip
	 * @throws SocketException
	 */
	public ServerThread(int port, InetAddress ip, String client) throws SocketException {
		this.IPAddress = ip;
		this.port = port;
		socket = new DatagramSocket();
		receiveData = new byte[512];
	   
		this.client = client;
		df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		initGUI(this.client);
		
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
	
	//portable wifi hotspot
	public void run(){
		 
		//Before running the while loop, send a confirmation message 
		try {
			DatagramPacket sendPacket = new DatagramPacket(CONFIRM_REQUEST.getBytes(), CONFIRM_REQUEST.getBytes().length, IPAddress, port);
			this.socket.send(sendPacket);
			System.out.println("\n Server thread is accepting connection");
			
			// Start receiving values from the sensor
			 while(true){
				 
	    		 
    			// Execute the right code based on who is the client thread communicating with us
	    		if(client.equals("pc")){
	    			 DatagramPacket receivePacket = new DatagramPacket(this.receiveData, this.receiveData.length);
					 Arrays.fill(receiveData, (byte) 1 ); //clear the receiving byte array for the next message
	    			 socket.receive(receivePacket);
	    			 
	    			 System.out.println("Packet [" + this.count + "] arrived with length " + receivePacket.getData().length);
		             this.count++; // increment packet count
		             String str = new String(receivePacket.getData()).trim();
		             System.out.println(str);
		             
	    			area.append(str + " ppm \n"); 
	    			sendPacket(receivePacket.getData(),ANDROID_PORT, Android_IP);// Send message to android

	    			 String str1 = new String(receivePacket.getData()).trim();
	    			 dateobj = new Date();
	    			 String currentTime = df.format(dateobj);
	    			 int value=Integer.parseInt(str1);
	    			 //send sensor value to the database if the value is greater than the threshold
	    			if(value>THRESHOLD){
	    				
	    				String databaseMessage = str1 + "," + currentTime;
	    				byte[] databaseData = databaseMessage.getBytes();
	    				sendPacket(databaseData, DATABASE_PORT, DATABASE_IP);
	    				System.out.println("sent " + databaseMessage + " to database");
	    			}
	    			
	    		}else if (client.equals("android")){
	    			 DatagramPacket receivePacket = new DatagramPacket(this.receiveData, this.receiveData.length);
					 Arrays.fill(receiveData, (byte) 1 ); //clear the receiving byte array for the next message
	    			 socket.receive(receivePacket);
	    		
		             String str = new String(receivePacket.getData()).trim();
	    			 dateobj = new Date();
	    			 String currentTime = df.format(dateobj);
	    			 
	    			 area.append(str + " on [ "+ currentTime + " ]\n");
	    			 
	    			 String databaseMessage = str + "," + currentTime;
	    			 byte[] databaseData = databaseMessage.getBytes();
	    			 sendPacket(databaseData, DATABASE_PORT, DATABASE_IP);
	    			 
	    			 //String databaseMessage = str + "," + currentTime;
	    			 //byte[] databaseData = databaseMessage.getBytes();
	    			 //sendPacket(databaseData, DATABASE_PORT, DATABASE_IP);
	    			 
	    			 // when message arrives to the database, extract data from packet -> convert it to string, parse it by delimeter "," and
	    			 // add it to the database. Have database listening at all times

	    		}

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
	public void initGUI(String client){
		frame = new JFrame("Server");
		frame.setSize(350,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		container1 = new JPanel();
		container1.setLayout(new GridLayout(2,3));
		
		
		textPanel = new JPanel();
		if(client.equals("pc")){
			label = new JLabel("SENSOR VALUES", SwingConstants.CENTER);
			area = new JTextArea(25,15);
			
			ipField = new JTextField("Enter IP here", SwingConstants.CENTER);
			changeIP = new JButton("Set destination IP");
			changeIP.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String input = ipField.getText() ;

				    if(validIP(input)){
				    	Android_IP = input;
				    }else{
				    	JOptionPane.showMessageDialog(frame, "Invalid IP");
				    }
					
				  }
				
			});
			container1.add(new JLabel("Change IP"));
			container1.add(ipField) ;
			container1.add(Box.createRigidArea(new Dimension(5,0)));
			container1.add(changeIP) ;
			frame.getContentPane().add(container1, BorderLayout.SOUTH);
			
		}else{
			label = new JLabel("Ack messages from AirApp", SwingConstants.CENTER);
			area = new JTextArea(25,30);
		}
		
		
		frame.getContentPane().add(label, BorderLayout.NORTH);
		
	
		DefaultCaret caret = (DefaultCaret) area.getCaret(); 
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);      

		scrollPane = new JScrollPane(area);
		scrollPane.setViewportView(area);
		textPanel.add(scrollPane);
		frame.getContentPane().add(textPanel, BorderLayout.CENTER) ;	
		frame.setVisible(true);
	}
	
	public static boolean validIP (String ip) {
	    try {
	        if ( ip == null || ip.isEmpty() ) {
	            return false;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if ( ip.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}

	
}
