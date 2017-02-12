import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.ByteArrayInputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Receiver extends Thread {
	
	private ServerSocket connection;
	private ArrayList<Integer> list;
	private JTextField field;
	private JTextArea area;
	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel textPanel, controlPanel;
	private JButton button;
	
	public Receiver(){
		frame = new JFrame("Receiver");
		frame.setSize(250,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//list = new ArrayList<JButton>();
		frame.getContentPane().setLayout(new BorderLayout());
		
		textPanel = new JPanel();
		field = new JTextField("Receiver ");
		//textPanel.add(field);
		frame.getContentPane().add(field, BorderLayout.NORTH) ;
		area = new JTextArea(25,15);
		//area.setSize(50,50);
		scrollPane = new JScrollPane(area);
		textPanel.add(scrollPane);
		frame.getContentPane().add(textPanel, BorderLayout.CENTER) ;
		
		controlPanel = new JPanel();
		button = new JButton("press");
		controlPanel.add(button);
		frame.getContentPane().add(controlPanel, BorderLayout.SOUTH) ;
		frame.setVisible(true);
	}
	
	public void run(){
		try{
		connection = new ServerSocket(6789);
		list = new ArrayList<Integer>();
		
		while(true){
			System.out.println("Waiting for connection..");
			Socket serverSocket = connection.accept();
			InetAddress addr = serverSocket.getInetAddress();
			System.out.println("Connection made to " + addr.getHostName() + " (" + addr.getHostAddress() + ")");
			
			InputStream in = serverSocket.getInputStream();
		    DataInputStream dis = new DataInputStream(in);
		    
		    int value;
		    while ((value = dis.readInt()) > -1) {
		    	list.add(value);
		        System.out.println(value);
		        area.append(value + "ppm \n");
		    }
		    
		    
		   // String stuff = dis.readUTF();
		   // System.out.println(stuff);
		    //byte[] data = stuff.getBytes();
		    //for(int i = 0; i < data.length; i++){
				//System.out.print((char)data[i]);
			//}
		    //data = null;
		    
		    in.close();
		    dis.close();
		    serverSocket.close();
		}
		}catch (IOException e) {
	         System.out.println("Exception detected: " + e);
	    }
	}
	
	public static void main(String [] args) {
	         Thread t = new Receiver();
	         t.start();
	}
	
	
}
