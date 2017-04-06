package group6;


import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServerTest {
	
private Server server;
private DatagramSocket serverSocket;
private byte[] receiveData;
private int port;
private int count;
private Test_Sensor sensor;
private static final int PORT = 6777;

	@Before
	public void setUp() throws Exception {
		server =new Server();
		sensor = new Test_Sensor();
	
	}
	@Test
	public void ServerTestConstructor() throws IOException{

		assertEquals("Both port should be same", PORT,server.PORT);
	
	}
	
	@Test
	public void TestProcessPacket(){
		assertEquals("string contains pc", true,server.processPacket("pc mes"));
		assertEquals("string contains android", true,server.processPacket("android mes"));
		assertEquals("string contains hacker", false,server.processPacket("fake"));

	}
	
	@Test
	public void TestRunServer() throws InterruptedException, IOException{
	
		assertEquals("Server thread is created and should be 1", 1,server.getThreadList().size());	
		assertEquals("server thread should have stored the client", "pc",server.getThreadList().get(0).getClient());	
		assertEquals("Server thread check the stored client", "localhost",server.getThreadList().get(0).getIP());	
		
		
	}

	@Test
	public void TestvalidIP() throws IOException {
		assertEquals("checks validity if ip",true, server.getThreadList().get(0).validIP("169.150.40.39"));
		assertEquals("Confirms when the connection is made",false, server.getThreadList().get(0).validIP("-1.-2.-3"));
	}

	@Test
	public void TestSendPacket() throws IOException {
		String message = new String(server.getPacket().getData()).trim();
		assertEquals("checks validity if ip", "", message);
	}
	
	
	
	
	@After
	public void tearDown() throws Exception {
		server.getSocket().close();

	}


}