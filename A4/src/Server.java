import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * hosts a library server to which a client can make UDP or TCP requests to reserve/return books
 * @author conangammel
 *
 */
public class Server {
	private ServerSocket TCPSocket;
	private ArrayList<ReplicaServers> replicas;
	private int ID;
	private ArrayList<String> library;
	private ExecutorService humanResources;
	private int numOfServers;
	private int numberOfServices;
	private int sleepDuration;
	
	/**
	 * constructor for the libarary server
	 * takes in standard input and configures the server with the given information
	 */
	public Server(Scanner scan){
		humanResources = Executors.newCachedThreadPool();
		this.replicas = new ArrayList<ReplicaServers>();
		try{
			String line = scan.nextLine();
			this.configureServer(line);//TODO use a for loop for number of servers
			for(int i=1; i<=this.numOfServers; i++){
				line = scan.nextLine();
				this.addNewReplica(line, i);
			}
			
			if(scan.hasNextLine()){
					this.setCrash(line);
			}else{
					
			}
		}catch(Exception e){
			System.err.println("Library server not started: "+e);
		}
	}
	
	private void addNewReplica(String line, int id){
		String[] creds = line.split(":");
		if(creds.length!=2){
			System.out.println("Bad Input, cant add new replica server");
			System.exit(0);//TODO ok?
		}else{
			InetAddress ip = null;
			int port = 0;
			try{
				ip = InetAddress.getByName(creds[0]);
			}catch(UnknownHostException e){
				System.out.println("Could not determine the IP address given. "+e.getMessage());
			}
			try{
				port = Integer.parseInt(creds[1].trim());
			}catch(NumberFormatException e){
				System.out.println("Could not determine the port number given. " + e.getMessage());
			}
			//TODO what do we do if the ip is local host vs not local host on the IDth add?
			replicas.add(new ReplicaServers(id,ip, port));
		}
	}
	
	private void setCrash(String line){
		String[] crash = line.split(" ");
		try{
			this.numberOfServices = Integer.parseInt(crash[1]);
			this.sleepDuration = Integer.parseInt(crash[2]);
		}catch(NumberFormatException e){
			System.out.println("Could not determine the crash command. "+e.getMessage());
		}
	}

	/**
	 * takes a string that should be in the format "<numberOfBooks> <TCPsocket> <UDPsocket>
	 * @param configString
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void configureServer(String configString) throws NumberFormatException, IOException {
		//stock our library with books!
			//trim in case of extra white space added by sloppy user
		
		String[] ints = configString.split(" ");
		this.ID = Integer.parseInt(ints[0]);
		this.numOfServers =  Integer.parseInt(ints[1]);
		library = new ArrayList<String>();
		for(int i=0; i<Integer.parseInt(ints[2].trim()); i++){//changed to 1 for A4 requirement
			library.add("available");
		}
//		String[] configArgs = configString.split(" ");
//		library = new ArrayList<String>();
//		for(int i=0; i<Integer.parseInt(configArgs[1].trim()); i++){//changed to 1 for A4 requirement
//			library.add("available");
//		}
//		int tcp = Integer.parseInt(configArgs[2].trim());
//		try{
//			TCPSocket = new ServerSocket(tcp);
//		}catch(Exception e){
//			System.err.println("TCP error "+e);
//		}
		
	}
	
	/**
	 * creates two "librarians"(socket listeners) that wait for client commands
	 * monitors the sockets until client is done
	 */
	private void openDoorsForBusiness() {
		//create socket monitors on both TCP and UDP and let the client requests flow
			TCP_librarian librarian1 = new TCP_librarian(this.numberOfServices, this.sleepDuration);
			
			humanResources.submit(librarian1);
			
			while(true){
				//wait until server shutdown. dont want garbage collection to discard these librarians
				//also dont want to close sockets or the pool until server is done
			}
	}
	
	protected void crash(int duration){
		this.library.clear();
		try{
			Thread.sleep(duration);
		}catch(InterruptedException e){
			System.out.println("Server ID: "+this.ID+" crash interrupted");
		}
	}
	
	/**
	 * string parsing for client commands
	 * @param request
	 * @return
	 */
	public synchronized String process(String request) {
		String[] requestArgs = request.split(" ");
		String clientID = requestArgs[0].trim();
		int bookNum = Integer.parseInt(requestArgs[1].trim().substring(1))-1;
		String action = requestArgs[2].trim();
		
		if(action.equalsIgnoreCase("reserve")){
			if(bookNum<library.size()){
				if(library.get(bookNum).equalsIgnoreCase("available") || library.get(bookNum).equalsIgnoreCase(clientID)){
					library.set(bookNum, clientID);
					return (clientID+" b"+(bookNum+1));
				}else{
					return ("fail "+clientID+" b"+(bookNum+1));
				}
			}else{
				return ("fail "+clientID+" b"+bookNum+1);
			}
		}else if(action.equalsIgnoreCase("return")){
			if(bookNum<library.size()){
				if(library.get(bookNum).equalsIgnoreCase(clientID)){
					library.set(bookNum,"available");
					return ("free "+clientID+" b"+(bookNum+1));
				}else{
					return ("fail "+clientID+" b"+(bookNum+1));
				}
			}else{
				return ("fail "+clientID+" b"+(bookNum+1));
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
	
	/**
	 * Injectrion method
	 */
	protected String printReplicaSet(){
		String result = "";
		for(ReplicaServers s: this.replicas){
			result+=s.toString()+" ";
		}
		return result;
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
		private int numOfCommands;
		private int crashLength;
		
		protected TCP_librarian(int num, int duration){
			this.numOfCommands = num;
			this.crashLength = duration;
		}

		@Override
		public void run() {
			try {
				Socket sock; 
				while((sock= TCPSocket.accept()) !=null){	//assignment inside the while condition so that it reassigns itself
					this.numOfCommands--;
					humanResources.submit(new TCP_librarian_service(sock));
					if(this.numOfCommands==0){
						Server.this.crash(this.crashLength);//TODO not sure if crash is working
					}
				}
			} catch (IOException e) {
				System.err.println("Library server Shutdown: "+e);
			}
			
		}
	}
	
	
//***************************************************MAIN FUNCTION FOR RUNNING FROM COMMMAND LINE*******************************
	
	
	/**
	 * starts the server and allows it to run requests
	 * @param args
	 */
	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		Server libraryServer = new Server(scan);
		libraryServer.openDoorsForBusiness();
	}
}
