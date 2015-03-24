import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;


public class TestClientInput {

	@Test
	public void test() {
		Client testclient = new Client();
		testclient.inputFirstLine("c1 2");	
		testclient.inputLines("127.0.0.1:8025", 2, 0);
		testclient.inputLines("128.0.0.1:8030", 2, 1);
		String result = testclient.printReplicaSet();
		
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8025 Server 2 is on IP: /128.0.0.1 and port number 8030 "));
	}
		
	@Test
	public void testSimpleInputFile() {
		Client testclient = new Client();
		testclient.inputFirstLine("c1 2");	
		testclient.inputLines("127.0.0.1:8025", 2, 0);
		testclient.inputLines("128.0.0.1:8030", 2, 1);
		testclient.inputLines("sleep 1500", 2, 2);
		String result = testclient.printReplicaSet();
		
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8025 Server 2 is on IP: /128.0.0.1 and port number 8030 "));
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
	}

}
