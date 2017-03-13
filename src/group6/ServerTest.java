package group6;


import static org.junit.Assert.*;

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
private int port;
private DatagramSocket serverSocket;
private byte[] receiveData;
private static final int PORT = 6799;
private int count;



	@Before
	public void setUp() throws Exception {
		port = 6666;
		server =new Server(port);
		this.receiveData = new byte[8];
		this.count = 0;
	}
	
	@Test
	public void ServerTestConstructor(){
		assertArrayEquals("both array should be equal and empty", receiveData,server.getData());
		assertEquals("Passed port should be equal to orignal port", port, server.getPort());
		assertEquals("count of packets should be equal to 0", count, server.getCount());
	}


	@After
	public void tearDown() throws Exception {
		server.getSocket().close();
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
	/*
	@Test
	public void TestSendPacket() {
		int[] sendData = new int[]{1,2};
		byte[] newData = server.intsToBytes(sendData);
		server.run();

		try {
			clientSocket = new DatagramSocket();
			server.sendPacket(newData, 6799, "localhost");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		server.getSocket().close();
		clientSocket.close();
		int[] ints = server.bytesToInts(server.getPacket().getData());
		assertArrayEquals("Ints array should be converted to bytes array.",sendData, ints);
	}
*/

}