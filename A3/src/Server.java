import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * hosts a library server to which a client can make UDP or TCP requests to reserve/return books
 * @author conangammel
 *
 */
public class Server {
	private ServerSocket TCPSocket;
	private DatagramSocket UDPSocket;
	private HashMap<Integer, String> library = new HashMap<Integer, String>();
	private ExecutorService humanResources;
	
	/**
	 * constructor for the libarary server
	 * takes in standard input and configures the server with the given information
	 */
	public Server(){
		Scanner scan = new Scanner(System.in);	//use "standard input"
		try{
			this.configureServer(scan.nextLine());
		}catch(Exception e){
			System.err.println("Library server not started: "+e);
		}finally{
			scan.close();
		}
	}

	/**
	 * takes a string that should be in the format "<numberOfBooks> <TCPsocket> <UDPsocket>
	 * @param configString
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void configureServer(String configString) throws NumberFormatException, IOException {
		
		String[] configArgs = configString.split(" ");
		
		//stock our library with books!
			//trim in case of extra white space added by sloppy user
		for(int i=0; i<Integer.parseInt(configArgs[0].trim()); i++){
			library.put(i, "available");
		}
		
		TCPSocket = new ServerSocket(Integer.parseInt(configArgs[1].trim()));
		UDPSocket = new DatagramSocket(Integer.parseInt(configArgs[2].trim()));
		
		
	}
	
	/**
	 * creates two "librarians"(socket listeners) that wait for client commands
	 * monitors the sockets until client is done
	 */
	private void openDoorsForBusiness() {
		//create socket monitors on both TCP and UDP and let the client requests flow
		try{
			TCP_librarian librarian1 = new TCP_librarian();
			UDP_librarian librarian2 = new UDP_librarian();
			
			humanResources.submit(librarian1);
			humanResources.submit(librarian2);
			
			while(true){
				//wait until server shutdown. dont want garbage collection to discard these librarians
				//also dont want to close sockets or the pool until server is done
			}
		}finally{
			try {
				TCPSocket.close();
				UDPSocket.close();
				humanResources.shutdown();
			} catch (IOException e) {
				System.err.println("Library server not started: "+e);
			}
			
		}
	}
	
	/**
	 * string parsing for client commands
	 * @param request
	 * @return
	 */
	public String process(String request) {
		String[] requestArgs = request.split(" ");
		
		String clientID = requestArgs[0];
		int bookNum = Integer.parseInt(requestArgs[1]);
		String action = requestArgs[2];
		
		if(action.equalsIgnoreCase("reserve")){
			if(library.containsKey(bookNum)){
				if(library.get(bookNum).equalsIgnoreCase("available")){
					return (clientID+" "+bookNum);
				}else{
					return ("fail "+clientID+" "+bookNum);
				}
			}else{
				return ("fail "+clientID+" "+bookNum);
			}
		}else if(action.equalsIgnoreCase("return")){
			if(library.containsKey(bookNum)){
				if(library.get(bookNum).equalsIgnoreCase("reserved")){
					return ("free "+clientID+" "+bookNum);
				}else{
					return ("fail "+clientID+" "+bookNum);
				}
			}else{
				return ("fail "+clientID+" "+bookNum);
			}
		}else{
			//TODO what to return?
			
		}
		return null;
	}
	
	/**
	 * converts a byte to a string and sends it off for parsing
	 * @see String process(String s)
	 * @param data
	 * @return
	 */
	public byte[] process(byte[] data) {
		String request = new String(data);	//stack overflow answer on conversion
		String returnValue = process(request);
		return returnValue.getBytes();
	}
	
	
	
//********************************************************NESTED CLASS: TCP MONITOR*********************************************
	//runnable classes allow for multiple threads to monitor the socket and service requests
	
	/**
	 * takes a request from the TCP_libarian and processes it
	 * @author conangammel
	 *
	 */
	protected class TCP_librarian_service implements Runnable{
		Socket sock;
		
		protected TCP_librarian_service(Socket soc){
			this.sock = soc;
		}
		
		@Override
		public void run() {	//service client request
			try {	//similar to serverThread on Professor Garg's github	
				Scanner inputStream = new Scanner(sock.getInputStream());
				PrintWriter outputStream = new PrintWriter(sock.getOutputStream());
				String request = inputStream.nextLine();
				String response = Server.this.process(request);
				outputStream.println(response);
				outputStream.flush();
		        outputStream.close();
		        inputStream.close();
		        sock.close();
		        
			}catch (IOException e) {
				System.err.println("Library server Shutdown: "+e);
			}
		}
	}
	
	/**
	 * Waits for a TCP request then sends it off for processing to TCP_librarian_service 
	 * so it can continue to wait for new requests
	 */
	protected class TCP_librarian implements Runnable{
		
		protected TCP_librarian(){ }	//nothing to init

		@Override
		public void run() {
			try {
				Socket sock; 
				while((sock= TCPSocket.accept()) !=null){	//assignment inside the while condition so that it reassigns itself
					humanResources.submit(new TCP_librarian_service(sock));
				}
			} catch (IOException e) {
				System.err.println("Library server Shutdown: "+e);
			}
			
		}
	}
	
//********************************************************NESTED CLASS: UDP MONITOR*********************************************
	/**
	 * listens for UDP requests and processes them
	 * @author conangammel
	 *
	 */
	protected class UDP_librarian implements Runnable{
		
		private final int length = 1024;
		DatagramPacket dataPacket, returnPacket;
		
		protected UDP_librarian(){ }	//nothing to init

		@Override
		public void run() {
			byte[] inBuffer = new byte[length];
			byte[] outBuffer;
			
			while(true){	//used example from page 93 in textbook
				try {
					dataPacket = new DatagramPacket(inBuffer, inBuffer.length);
					UDPSocket.receive(dataPacket);
					outBuffer = Server.this.process(dataPacket.getData());
					returnPacket = new DatagramPacket(
										outBuffer,
										outBuffer.length,
										dataPacket.getAddress(),
										dataPacket.getPort());
					
					UDPSocket.send(returnPacket);
					
				} catch (IOException e) {
					System.err.println("Library server shutdown: "+e);
				}
			}
		}
	}
	
	
//***************************************************MAIN FUNCTION FOR RUNNING FROM COMMMAND LINE*******************************
	
	
	/**
	 * starts the server and allows it to run requests
	 * @param args
	 */
	public static void main(String[] args){
		Server libraryServer = new Server();
		libraryServer.openDoorsForBusiness();
	}
}
