import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestServerInput {
	@Rule
	  public ExpectedException exception = ExpectedException.none();

	@Test
	public void testSimpleInputFileWithoutCrash() {
		String file = "1 2 10" +"\n"+ "127.0.0.1:8080" +"\n"+ "128.0.0.1:9005";
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
	}
	
	@Test
	public void testSimpleInputFileWithCrash() {
		int numberOfServices = 10;
		int sleepDuration = 9000;
		String ip1 = "127.0.0.1:8080";
		String ip2 = "128.0.0.1:9005";
		
		
		String file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+numberOfServices+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
		String result2 = server.printDetails();
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
		assertTrue(result2.equals("Number of commands= "+numberOfServices+" and sleep length= "+sleepDuration+" milliseconds"));
	}
	
	@Test
	public void testNotEnoughArgsInInputFile() {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		
		String file = "1 10" +"\n"+ "127.0.0.1:8080" +"\n"+ "128.0.0.1:9005";
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
	    assertEquals("Library server not started: not enough args\n", outContent.toString());
	}
	
	@Test
	public void testIncompleteCrashInputs() {
		int numberOfServices = 10;
		int sleepDuration = 9000;
		String ip1 = "127.0.0.1:8080";
		String ip2 = "128.0.0.1:9005";
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		
		
		String file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Could not determine the crash command. For input string: \"\"\n", outContent.toString());
	}
	
	@Test
	public void testInvalidArgumentsForReplica() {
		int numberOfServices = 10;
		int sleepDuration = 9000;
		String ip1 = "127.0.0.1:8080:000";
		String ip2 = "128.0.0.1:9005";
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		
		
		String file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Bad Input, cant add new replica server\n", outContent.toString());
	}
	
	@Test
	public void testInvalidArgumentsForReplica2() {
		int numberOfServices = 10;
		int sleepDuration = 9000;
		String ip1 = "127.0.0.1";
		String ip2 = "128.0.0.1:9005";
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		
		
		String file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+" "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Bad Input, cant add new replica server\n", outContent.toString());
	}
	
	/**
	 * would have to use jmock to throw unknown host exception
	 */
	@Test
	public void testInvalidIP() {
		int numberOfServices = 10;
		int sleepDuration = 9000;
		String ip = "p";
		String ip1 = ip+":0000";
		String ip2 = "128.0.0.1:9005";
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		
		
		String file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+"10 "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Could not determine the IP address given. "+ip+": nodename nor servname provided, or not known\n", outContent.toString());
	}
	
	@Test
	public void testInvalidPort() {
		int numberOfServices = 10;
		int sleepDuration = 9000;
		String port = "p";
		String ip1 = "127.0.2.1:"+port;
		String ip2 = "128.0.0.1:9005";
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setErr(new PrintStream(outContent));
		
		
		String file = "1 2 10" +"\n"+ ip1 +"\n"+ ip2 +"\n"+ "crash "+"10 "+sleepDuration;
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		assertEquals("Library server not started: Could not determine the port number given. For input string: \""+port+"\"\n", outContent.toString());
	}

}
