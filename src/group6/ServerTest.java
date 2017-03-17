package group6;


import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServerTest {
	
private Server server;
private DatagramSocket serverSocket;
private byte[] receiveData;
private int port;
private int count;
private static final int PORT = 6777;




	@Before
	public void setUp() throws Exception {
		server =new Server();
		
	
	}
	
	@Test
	public void ServerTestConstructor() throws IOException{
		Server server2 = new Server(6788);
		assertArrayEquals("both array should be equal and empty", server2.getData(),server.getData());
		assertEquals("Passed port should be equal to orignal port", server2.getPort(), server.getPort());
		assertEquals("count of packets should be equal to 0", server2.getCount(), server.getCount());
	}
	
	@Test
	public void TestProcessPacket(){
		assertEquals("both array should be equal and empty", true,server.processPacket("con message"));
	
	}
	
	@Test
	public void TestRunServer() throws InterruptedException{
		try {
			//Test_Sensor t = new Test_Sensor();
			String[] args = {};
			Test_Sensor.main(args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("check if server thread was created", 1, server.getThreadList().size() );
		
	}


	@Test
	public void TestbytesToInts() {
	
		byte[] bytes = new byte[0];
		int[] ints = new int[0];
		

		assertArrayEquals("Bytes array should be converted to int array.",ints, server.bytesToInts(bytes));
	}
	@Test
	public void TestIntsToBytes() {
		byte[] bytes = new byte[0];
		int[] ints = new int[0];
		assertArrayEquals("Ints array should be converted to bytes array.",bytes, server.intsToBytes(ints));
	}

	@Test
	public void TestSendPacket() throws IOException {
		String s = "con nect";
		
		Test_Sensor t = new Test_Sensor();
		t.sendPacket(s.getBytes());
		
		String message = new String(server.getPacket().getData()).trim();
		//int[] ints = server.bytesToInts(server.getPacket().getData());
		assertEquals("Confirms when the connection is made","ok done", message);
	}
	
	@After
	public void tearDown() throws Exception {
		server.getSocket().close();
	}


}