import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;



public class TestServerInput {
	String file;
	String ip1;
	String ip2;
	int numberOfServices;
	int sleepDuration;
	ByteArrayOutputStream outContent;
	
	@Before
	public void setUp(){
		this.numberOfServices = 10;
		this.sleepDuration = 9000;
		this.ip1 = "127.0.0.1:8080";
		this.ip2 = "128.0.0.1:9005";
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+numberOfServices+" "+sleepDuration;
		this.outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
	}

	@Test
	public void testSimpleInputFileWithoutCrash() {
		this.file = "1 2 10" +"\n"+ "127.0.0.1:8080" +"\n"+ "128.0.0.1:9005";
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
	}
	
	@Test
	public void testSimpleInputFileWithCrash() {
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
		String result2 = server.printCurrentCrashDetails();
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
		assertTrue(result2.equals("Number of commands= "+numberOfServices+" and sleep length= "+sleepDuration+" milliseconds"));
	}
	
	@Test
	public void testNotEnoughArgsInInputFile() {
		
		String file = "1 10" +"\n"+ "127.0.0.1:8080" +"\n"+ "128.0.0.1:9005";
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
	    assertEquals("Library server not started: not enough args\n", outContent.toString());
	}
	
	@Test
	public void testIncompleteCrashInputs() {
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Could not determine the crash command. For input string: \"\"\n", outContent.toString());
	}
	
	@Test
	public void testCrashWithWrongCmdInput() {
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "cash "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Server does not accept commands other than \"crash\"\n", outContent.toString());
	}
	
	@Test
	public void testCrashWithCapCmdInput() {
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "CRASH "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printCurrentCrashDetails();
		assertEquals(("Number of commands= "+numberOfServices+" and sleep length= "+sleepDuration+" milliseconds"),result);	}
	
	@Test
	public void testInvalidArgumentsForReplica() {
		this.ip1 = "127.0.0.1:8080:000";
		this.ip2 = "128.0.0.1:9005";
		
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Bad Input, cant add new replica server\n", outContent.toString());
	}
	
	@Test
	public void testInvalidArgumentsForReplica2() {
		String ip1 = "127.0.0.1";
		String ip2 = "128.0.0.1:9005";
		
		
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Bad Input, cant add new replica server\n", outContent.toString());
	}
	
	/**
	 * would have to use jmock to throw unknown host exception
	 */
	@Test
	public void testInvalidIP() {
		String ip = "p";
		this.ip1 = ip+":0000";
		this.ip2 = "128.0.0.1:9005";
		
		
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Could not determine the IP address given. "+ip+": nodename nor servname provided, or not known\n", outContent.toString());
	}
	
	@Test
	public void testInvalidPort() {
		String port = "p";
		this.ip1 = "127.0.2.1:"+port;
		this.ip2 = "128.0.0.1:9005";
		
		
		this.file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Could not determine the port number given. For input string: \""+port+"\"\n", outContent.toString());
	}
	
}
