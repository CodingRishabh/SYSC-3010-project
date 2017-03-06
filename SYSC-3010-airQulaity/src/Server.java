import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

import java.io.*; 
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

public class Server extends Thread implements ActionListener{
	
	private ServerSocket connection;
	private ArrayList<Integer> list;
	private JTextField field;
	private JTextArea area;
	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel textPanel, controlPanel;
	private JButton sendToApp, sendToDatabase;
	private String info;
	private int value;
	protected  String tableType;
	public Server(){
		frame = new JFrame("Receiver");
		frame.setSize(350,500);
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
		
		sendToApp = new JButton("Send Data to App");
		sendToApp.addActionListener(this);
		controlPanel.add(sendToApp);
		
		sendToDatabase= new JButton("send Data to Databse");
		controlPanel.add(sendToDatabase);
		sendToDatabase.addActionListener(this);
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
		    //send value to the database before closing the serversocket
		}
		}catch (IOException e) {
	         System.out.println("Exception detected: " + e);
	    }
	}
	
	private void clientInteract(ServerSocket server) throws Exception{
		while (true){
			try(final Socket clientSocket = server.accept()){
				// We're in!
				System.out.println("Connection Established.");
				System.out.println();
				
				// Setup to receive info from client
				InputStreamReader isr =  new InputStreamReader(clientSocket.getInputStream());
				BufferedReader reader = new BufferedReader(isr);
				String line = reader.readLine(); 
				System.out.println(line);
				
				// Save the first line to collect info for SQL, parse
				info = line;
				
				// Send to SQL database
				Database q = new Database();
				
				if(tableType.equals("/house")){
					q.houseTable(value,0,0);
				}
				// Prints info from connection on ServerSocket to console
				while (!line.isEmpty()) {
					System.out.println(line);
					line = reader.readLine();
				}
				
				// Send a string to the accepted client Socket
				String response = "HTTP/1.1 200 OK\r\nAccess-Control-Allow-Origin: *\r\n\r\n" + server.toString();
				clientSocket.getOutputStream().write(response.getBytes("UTF-8"));
				
			}
		}	
	}
	
	public static void main(String [] args) {
	         Thread t = new Server();
	         t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(sendToApp)){
			JOptionPane.showMessageDialog (null, "Sending data to app.....", "Title", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("app" );
		}
		else if (e.getSource().equals(sendToDatabase)){
			JOptionPane.showMessageDialog (null, "Sending data to Database.....", "Title", JOptionPane.INFORMATION_MESSAGE);
			
			System.out.println("databse" );
		}
		
	}
	
	
}