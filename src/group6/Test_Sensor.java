package group6;

import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ThreadLocalRandom;
import java.io.OutputStream;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class Test_Sensor {
	
	
	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel statusLabel;
	private JPanel controlPanel;
	private Hashtable<Integer, JLabel> dict;
	

	int sliderValue = 400;
	
	private static final int PORT = 6799;
	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	int count;
	
	
	public Test_Sensor() throws IOException, IOException{
		//clientSocket = new Socket("172.17.43.32", port);
		//clientSocket = new Socket("172.17.67.5", port);
		//clientSocket = new Socket("localhost", PORT);
		//old phone 172.17.79.92
		//good phone 172.17.48.38
		System.out.println("Connecting to " + "localhost" + " on port " + PORT);
		
		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName("172.17.48.38");
			
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dict = new Hashtable<Integer, JLabel>();
		prepareGUI();
	}
	
	private void prepareGUI(){
	      mainFrame = new JFrame("Java Swing Examples");
	      mainFrame.setSize(400,400);
	      mainFrame.setLayout(new GridLayout(5, 1));
	      
	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });
	      
	      dict.put(1, new JLabel("Low"));
	      dict.put(2, new JLabel("hi"));
	      

	      headerLabel = new JLabel("", JLabel.CENTER);        
	      statusLabel = new JLabel("",JLabel.CENTER);    
	      statusLabel.setSize(350,100);

	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(statusLabel);

	      mainFrame.setVisible(true);  
	   }
	   private void showSliderDemo(){
	      headerLabel.setText("CO2 Concentration in (ppm)"); 
	      statusLabel.setText("Value : " + sliderValue); 
	      JSlider slider = new JSlider(JSlider.HORIZONTAL,0,1000,400);;
	      slider.setLabelTable(dict);
	      
	      
	      slider.addChangeListener(new ChangeListener() {
	         public void stateChanged(ChangeEvent e) {
	        	sliderValue = ((JSlider)e.getSource()).getValue();
	            statusLabel.setText("Value : " + sliderValue);
	   
	         }
	      });
	      controlPanel.add(slider);      
	      mainFrame.setVisible(true);     
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
		public byte[] createPacket(int sata){
			
			int[] data = new int[1];
			data[0] = sata;

			byte[] newData = intsToBytes(data);
			
			return newData;
		}
		
		/**
		 * 
		 * @param sendData
		 */
		public void sendPacket(byte[] sendData, DatagramSocket clientSocket){
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
		     try {
				clientSocket.send(sendPacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	public static void main(String args[]) throws IOException, InterruptedException{
		System.out.println("send a message" );
		 
		  Test_Sensor t = new Test_Sensor();
		  t.showSliderDemo();
		  System.out.println("Connected to the server" );
		  while(true){
			  //c.clientSocket.setReuseAddress(true);
			  //c.clientSocket.setSoTimeout(1000);
			  byte[] sendData = t.createPacket(t.getInt(t.sliderValue));
			 
				if(!t.clientSocket.isClosed()){
					t.sendPacket(sendData, t.clientSocket);
				}else{
					System.out.println("Server has closed its connection" );
					t.clientSocket.close();
					break;
				}
			  
			
		  }

	}
	
	public int getInt(int value) throws InterruptedException{
		Thread.currentThread().sleep(1000);
		return ThreadLocalRandom.current().nextInt(value -3, value + 3);
		
	}

	
}
