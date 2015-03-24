import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;


public class TestClientInput {

	@Test
<<<<<<< HEAD
	public void test() {
		String file = "c1 2" +"\n"+ "127.0.0.1:8025" +"\n"+ "128.0.0.1:8030" +"\n"+ "crash 3 3000";
		Client testclient = new Client();
		testclient.inputFirstLine("c1 2");	
		testclient.inputLines("127.0.0.1:8025", 2, 0);
		testclient.inputLines("128.0.0.1:8030", 2, 1);
		String result = testclient.printReplicaSet();
		
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8025 Server 2 is on IP: /128.0.0.1 and port number 8030 "));
		
=======
	public void testSimpleInputFile() {
		String file = "1 2" +"\n"+ "127.0.0.1:8080" +"\n"+ "128.0.0.1:9005";
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
>>>>>>> 835c6f86be7e50dd20cd217427334293a4d62cd6
	}

}
