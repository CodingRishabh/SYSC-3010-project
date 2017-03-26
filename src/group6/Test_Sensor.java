package group6;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class Test_Sensor {
	
	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel statusLabel;
	private JPanel controlPanel;
	private JSlider slider;
	private int sliderValue = 400; //display slider value on GUI
	
	private static final int PORT = 6777;// port of the server
	private DatagramSocket clientSocket;// our socket
	private InetAddress IPAddress;
	byte[] receiveData; // data from UDP packet received here
	private DatagramPacket receivePacket;
	
	
	
	/**
	 * Initialize our data-gram socket, IP address and start the GUI
	 * @throws IOException
	 */
	public Test_Sensor() throws IOException{
		//clientSocket = new Socket("172.17.43.32", port);
		//clientSocket = new Socket("172.17.67.5", port);
		//clientSocket = new Socket("localhost", PORT);
		//old phone 127.17.88.251
		//good phone 172.17.48.38
		//use broad cast ip in computer lab linux machine not ethernet ip
		System.out.println("Connecting to " + "localhost" + " on port " + PORT);
		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");
			receiveData = new byte[512];

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prepareGUI();
	}
	
	
	/**
	 * Create a GUI and initialize all the GUI variables
	 */
	private void prepareGUI(){
	      mainFrame = new JFrame("Test_Sensor");
	      mainFrame.setSize(300,300);
	      mainFrame.setLayout(new GridLayout(5, 1));
	      
	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });
	      
	            
	      headerLabel = new JLabel("CO2 Concentration in (ppm)", JLabel.CENTER);        
	      statusLabel = new JLabel("Value : " + sliderValue,JLabel.CENTER);    
	      statusLabel.setSize(350,300);

	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(statusLabel);
	      
	      mainFrame.setVisible(true);  
	   }
	
		/**
		 * Create a JSlider that ranges from 0 - 1000
		 */
	   private void showSliderDemo(){
	      slider = new JSlider(JSlider.HORIZONTAL,0,1000,400);;
	      slider.setSize(20,10);
	      
	      slider.setMinorTickSpacing(100);
	      slider.setMajorTickSpacing(500);
	      slider.setPaintTicks(true);
	      slider.setPaintLabels(true);

	      // standard numeric labels for the slider...
	      slider.setLabelTable(slider.createStandardLabels(200)); 
	      
	      
	      slider.addChangeListener(new ChangeListener() {
	         public void stateChanged(ChangeEvent e) {
	        	sliderValue = ((JSlider)e.getSource()).getValue();
	            statusLabel.setText("Value : " + sliderValue); // match the slider label with current slider value
	   
	         }
	      });
	      controlPanel.add(slider);      
	      mainFrame.setVisible(true);     
	   } 
	   
	   public byte[] getData(){
			return this.receiveData;
		}
	   
	  
	   /**
	    *  takes in data and creates a data-gram packet and sends it to destination
	    * @param sendData data to be sent
	    * @throws IOException
	    */
		public void sendPacket(byte[] sendData) throws IOException{
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
			clientSocket.send(sendPacket);
		
		}
		/**
		 * creates a packet the excepts data from the server
		 * @throws IOException
		 */
		public void receivePacket() throws IOException{
			receivePacket = new DatagramPacket(receiveData, receiveData.length);	
			clientSocket.receive(receivePacket);
		}
		
		/**
		 * If the packet contains "ok", the server has approved
		 * our connection request
		 * @param packet sent by the server
		 * @return
		 */
		private boolean processPacket(DatagramPacket packet){
			String message = new String(packet.getData());
			if(message.startsWith("ok")){
				return true;
			}
			return false;
		}

		public static void main(String args[]) throws IOException, InterruptedException{
		  Test_Sensor t = new Test_Sensor();
		  t.showSliderDemo();
		  
		  String conn = "pc";
		  System.out.println("sending a connection request" );
		  t.sendPacket(conn.getBytes());
		  System.out.println("Waiting for connection" );
		  t.receivePacket();
		  
		  // if our connection request has been approved
		  if(t.processPacket(t.receivePacket)){
			  System.out.println("Connection made" );
			  while(true){ // Take the current slider value and send it to the server to simulate a sensor
				  String message = t.getInt(t.sliderValue) + "";
				  System.out.println(message);
				  byte[] sendData = message.getBytes();
					if(!t.clientSocket.isClosed()){
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, t.receivePacket.getAddress(), t.receivePacket.getPort());
						t.clientSocket.send(sendPacket);
					}else{
						System.out.println("Server has closed its connection" );
						t.clientSocket.close();
						break;
					}
			  }
		  }
	}
	
	/**
	 * Returns a random integer that may be lesser or greater than the value
	 * @param value
	 * @return
	 * @throws InterruptedException
	 */
	public int getInt(int value) throws InterruptedException{
		Thread.currentThread().sleep(1000);
		return ThreadLocalRandom.current().nextInt(value -3, value + 3);
		
	}
	
}
