import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;


public class TestServerInput {

	@Test
	public void testSimpleInputFile() {
		String file = "1 2 10" +"\n"+ "127.0.0.1:8080" +"\n"+ "128.0.0.1:9005";
		Scanner scan = new Scanner(file);
		Server server = new Server(scan);
		String result = server.printReplicaSet();
		
		assertTrue(result.equals("Server 1 is on IP: /127.0.0.1 and port number 8080 Server 2 is on IP: /128.0.0.1 and port number 9005 "));
		
		
	}

}
