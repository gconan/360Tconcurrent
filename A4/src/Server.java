import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	private ArrayList<int[]> crashCommands;
	private int myClock;
	private InetAddress IP;//TODO init
	private int port;//TODO init
	private boolean csReady;
	
	/**
	 * constructor for the libarary server
	 * takes in standard input and configures the server with the given information
	 */
	public Server(Scanner scan){
		this.csReady = false;
		this.myClock = 0;
		humanResources = Executors.newCachedThreadPool();
		this.replicas = new ArrayList<ReplicaServers>();
		this.crashCommands = new ArrayList<int[]>();
		try{
			String line = scan.nextLine();
			this.configureServer(line);
			for(int i=1; i<=this.numOfServers; i++){
				line = scan.nextLine();
				if(this.ID ==  i){
					String[] ipconfig = line.split(":");
					//if(InetAddress.getLocalHost()==InetAddress.getByName(ipconfig[0])){
						int port = Integer.parseInt(ipconfig[1].trim());
						try{
							TCPSocket = new ServerSocket(port);
							System.out.println("socket did not throw an exception, this socket open");//TODO remove
						}catch(Exception e){
							throw new Exception("Possibility: the port is already in use on this machine. "+e.getLocalizedMessage());
						}
//					}else{
//						throw new Exception("This ID is already linked to another IP address. Server not starting");
//					}
				}
				this.addNewReplica(line, i);
			}
			
			if(scan.hasNextLine()){
				this.setCrashCommands(scan);
			}
			scan.close();
		}catch(Exception e){
			System.err.println("Library server not started: "+e.getMessage());
		}
	}
	
	private void addNewReplica(String line, int id) throws Exception{
		String[] creds = line.split(":");
		System.out.println("starting to add replica server. ip: "+creds[0]+" port: "+creds[1]);//TODO remove
		if(creds.length!=2){
			throw new Exception("Bad Input, cant add new replica server");
		}else{
			InetAddress ip = null;
			int port = 0;
			try{
				ip = InetAddress.getByName(creds[0]);
				System.out.println("ip after inet conversion: "+ip.toString());//TODO remove
			}catch(UnknownHostException e){
				throw new Exception("Could not determine the IP address given. "+e.getMessage());
			}
			try{
				port = Integer.parseInt(creds[1].trim());
				System.out.println("port after int conversion: "+port);//TODO remove
			}catch(NumberFormatException e){
				throw new Exception("Could not determine the port number given. " + e.getMessage());
			}
			replicas.add(new ReplicaServers(id,ip, port));
			System.out.println("replica set: \n"+this.printReplicaSet());//TODO remove
		}
	}
	
	private void setCrashCommands(Scanner scan) throws Exception{
		while(scan.hasNextLine()){
			String[] crash = scan.nextLine().split(" ");
			if(crash[0].equalsIgnoreCase("crash")){
				try{
					//				  .add(*********** K *********************, ********* Delta************);
					this.crashCommands.add(new int[]{Integer.parseInt(crash[1]),Integer.parseInt(crash[2])});
				}catch(Exception e){
					throw new Exception("Could not determine the crash command. "+e.getMessage());
				}
			}else{
				throw new Exception("Server does not accept commands other than \"crash\"");
			}
		}
	}

	/**
	 * takes a string that should be in the format "<numberOfBooks> <TCPsocket> <UDPsocket>
	 * @param configString
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void configureServer(String configString) throws Exception {
		//stock our library with books!
			//trim in case of extra white space added by sloppy user
		
		String[] ints = configString.split(" ");
		if(ints.length!=3){
			throw new IOException("not enough args");
		}
		this.ID = Integer.parseInt(ints[0]);
		this.numOfServers =  Integer.parseInt(ints[1]);
		library = new ArrayList<String>();
		for(int i=0; i<Integer.parseInt(ints[2].trim()); i++){//changed to 1 for A4 requirement
			library.add("available");
		}
	}
	
	/**
	 * creates two "librarians"(socket listeners) that wait for client commands
	 * monitors the sockets until client is done
	 */
	private void openDoorsForBusiness() {
		//create socket monitors on both TCP and UDP and let the client requests flow
			TCP_librarian librarian1 = new TCP_librarian(this.crashCommands);
			
			humanResources.submit(librarian1);
			
			while(true){
				//wait until server shutdown. dont want garbage collection to discard these librarians
				//also dont want to close sockets or the pool until server is done
			}
	}
	
	protected void crash(){
		for(int i=0; i<this.library.size(); i++){
			this.library.set(i, "available");
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
	 * send requests to each server. if one doesn't respond, assume dead and move along.
	 */
	public void sendRequestToServers() {
		//send message to servers: request, id, clock
		System.out.println("sending request to other server" + this.printReplicaSet());
		int i = 0;
		while(i < replicas.size()){
			int id = this.ID;
			if(replicas.get(i).getID() == id){
				replicas.get(i).setAck(true);
				i++;
			}
			String output;
			int port = replicas.get(i).getPort();
			InetAddress serverIP = replicas.get(i).getIP();
			int clock = this.getMyClock();
			String message = "request" + "\n" + id + "\n" + clock;
			System.out.println("requesting server " + serverIP.toString() + " " + port + " try " + i);
			try {
				Socket server = new Socket();
				server.connect(new InetSocketAddress(serverIP, port), 100);
				Scanner din = new Scanner(server.getInputStream());
				PrintWriter pout = new PrintWriter(server.getOutputStream(), true);
				pout.println(message);
				output = din.nextLine();
				//System.out.println(output);
				server.close();
				din.close();
				pout.flush();
				pout.close();
			} catch (Exception e) {
				if(e.getClass() == SocketTimeoutException.class){
					//try next server
					System.out.println("socket timed out");
					replicas.get(i).setAck(true); //set ack to true if we are assuming a crash
					i++;
				} else{
					System.err.println("Socket issues when sending requests");//TODO remove
					i++;
				}
			}	
		}
		
		while(!csReady){
			csReady=true;
			for(ReplicaServers s: replicas){
				if(!s.hasAck()){
					csReady=false;
					break;
				}
			}
		}
		System.out.println("Out of ack loop");
	}

	public void processRelease(ArrayList<String> messageLines) {
		int releaseID = Integer.parseInt(messageLines.get(1));
		int bookNum = Integer.parseInt(messageLines.get(2));
		String status = messageLines.get(3);
		
		this.replicas.get(releaseID-1).setAck(true);
		this.library.set(bookNum-1, status);
		
	}

	public void sendAcknowledgment(ArrayList<String> messageLines) {
		String message = "acknowledge"+"\n"+this.ID;
		System.out.println("Server " + this.ID + " " + messageLines.toString());
		int receipientId = Integer.parseInt(messageLines.get(1));
		InetAddress receipientIP = replicas.get(receipientId-1).getIP();
		int port = replicas.get(receipientId-1).getPort();
		System.out.println("Server " + receipientIP.toString() + " port " + port);
		
		try {
			Socket socket = new Socket(receipientIP , port);
			PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
			pout.println(message);
			socket.close();
			pout.flush();
			pout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public void sendLibraryRecover(ArrayList<String> messageLines){
		String message = "";
		for(String s: this.library){
			message+=s +"\n";
		}
		message= message.substring(0, message.lastIndexOf("\n"));
		
		InetAddress ip = replicas.get(Integer.parseInt(messageLines.get(1))).getIP();
		int prt = replicas.get(Integer.parseInt(messageLines.get(1))).getPort();
		
		try {
			Socket socket = new Socket(ip , prt);
			PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
			pout.println(message);
			pout.flush();
			pout.close();
			socket.close();
		}catch(Exception e){
			
		}
	}
	
	public void recoverLibrary() {
		//send message to a server and ask for library
		//receive message with lib data in the form:
		// available
		// reserved
		//....line number cooresponds to booknum-1
		String message = "recover"+"\n"+ID+"\n"+IP+"\n"+port;
		
		boolean connected = false;
		int i=0;
		Socket socket = new Socket();
		
		while(!connected && i<replicas.size()){
			InetAddress receipientIP = replicas.get(0).getIP();
			int port = replicas.get(0).getPort();
			
			try {
				socket.connect(new InetSocketAddress(receipientIP , port), 100);
				connected = true;
			}catch(Exception e){
				if(e.getClass() == SocketTimeoutException.class){
					i+=1;
				}else{
					System.err.println("Socket issue");
				}
			}
			//should have a connection to one of the servers
			try{
				PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
				Scanner din = new Scanner(socket.getInputStream());
				pout.println(message);
				int j=0;
				while(din.hasNextLine()){
					library.set(j, din.nextLine());
					j+=1;
				}
				din.close();
				pout.flush();
				pout.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void clockUp(){
		this.myClock++;
	}
	
	protected int getMyClock(){
		return this.myClock;
	}
	
	/**
	 * Injection method
	 */
	protected String printReplicaSet(){
		String result = "";
		for(ReplicaServers s: this.replicas){
			result+=s.toString()+" ";
		}
		return result;
	}
	
	/**
	 * Injection method
	 */
	protected String printCurrentCrashDetails(){
		return ("Number of commands= "+this.crashCommands.get(0)[0]+" and sleep length= "+this.crashCommands.get(0)[1]+" milliseconds");
	}
	
	
	
//********************************************************NESTED TCP HANDLER CLASSES*********************************************
	//runnable classes allow for multiple threads to monitor the socket and service requests
	
	

	/**
	 * takes a request from the TCP_libarian and processes it
	 * @author conangammel
	 *
	 */
	protected class TCP_librarian_service implements Runnable{
		protected Socket sock;
		protected ArrayList<String> messageLines;
		protected ArrayList<Integer> reqQueue;
		protected Boolean imInterested;
		
		protected TCP_librarian_service(Socket soc){
			this.sock = soc;
			messageLines = new ArrayList<String>();
			this.reqQueue = new ArrayList<Integer>();
			this.imInterested = false;
		}
		
		@Override
		public void run() {	//service client request
			try {	//similar to serverThread on Professor Garg's github
				Scanner inputStream = new Scanner(sock.getInputStream());
				PrintWriter outputStream = new PrintWriter(sock.getOutputStream());
				String request = inputStream.nextLine();
				//REQUEST
				if(request.split(" ")[0].equalsIgnoreCase("request")){
					System.out.println("WE GOT A REQUEST");
					getFullMessage(request, inputStream, 3);
					if(Integer.parseInt(messageLines.get(2))<Server.this.getMyClock() || !imInterested){
						System.out.println("Sending ack from server " + ID + " to server " + messageLines.get(1));
						Server.this.sendAcknowledgment(messageLines);
					}else{
						this.reqQueue.add(Integer.parseInt(messageLines.get(1)));
					}
					
				//RELEASE
				}else if(request.split(" ")[0].equalsIgnoreCase("release")){
					getFullMessage(request, inputStream, 4);
					Server.this.processRelease(messageLines);	//should contain library update
					
				//ACKNOWLEDGE
				}else if(request.split(" ")[0].equalsIgnoreCase("acknowledge")){
					System.out.println("Received an ack hooray");
					getFullMessage(request, inputStream, 2);
					System.out.println(messageLines.toString());
					int s = Integer.parseInt(messageLines.get(1));
					System.out.println("Trying to set ack of server " + s);
					replicas.get(s-1).setAck(true);
					System.out.println("After recieved ack: " + printReplicaSet());
				//RECOVER
				}else if(request.split(" ")[0].equalsIgnoreCase("recover")){
					getFullMessage(request, inputStream, 4);
					Server.this.sendRequestToServers();
					Server.this.sendLibraryRecover(messageLines);
					
				//CLIENT
				}else{
					System.out.println("request from client");
					imInterested = true;
					Server.this.sendRequestToServers();	//wait (100ms) for all acks
					
					String response = Server.this.process(request);
					outputStream.println(response);
				}
				outputStream.flush();
		        outputStream.close();
		        inputStream.close();
		        sock.close();
			}catch (IOException e) {
				System.err.println("Library server Shutdown: "+e);
			}
		}
		
		protected void getFullMessage(String request, Scanner inputStream, int messageLength){
			messageLines.add(request);
			for(int i=0; i<messageLength-1; i++){
				messageLines.add(inputStream.nextLine());
			}
			System.out.println(messageLines.toString());//TODO remove
		}
	}
	
//listens on tcp then services requests by submitting a librarian_service
	/**
	 * Waits for a TCP request then sends it off for processing to TCP_librarian_service 
	 * so it can continue to wait for new requests
	 */
	protected class TCP_librarian implements Runnable{
		private int current_k;
		private int current_delta;
		private ArrayList<int[]> crashes;
		
		protected TCP_librarian(ArrayList<int[]>crashCommands){
			System.out.println("starting the librarian");//TODO remove
			this.crashes = crashCommands;
			if(this.crashes.size()>0){
				System.out.println("setting crash stats");//TODO remove
				this.current_k = this.crashes.get(0)[0];
				this.current_delta = this.crashes.get(0)[1];
				System.out.println("current k: " + current_k + " current delta: " + current_delta);
			}
		}

		@Override
		public void run() {
			try {
				Socket sock; 
				System.out.println("Waiting for tcp socket to accept");//TODO remove
				while((sock= TCPSocket.accept()) !=null){	//assignment inside the while condition so that it reassigns itself
					System.out.println("socket accepted!");//TODO remove
					this.current_k--;
					humanResources.submit(new TCP_librarian_service(sock));
					if(this.current_k==0){	//if no crash set, then current_k will be negative and never crash
						this.crash();// not sure if crash is working
					}
					Server.this.clockUp();
				}
			} catch (IOException e) {
				System.err.println("Library server Shutdown: "+e);
			}
			
		}
		
		private void crash(){
			if(crashes.size()>0){
				this.crashes.remove(0);
				Server.this.crash();
				try{
					Thread.sleep(this.current_delta);
				}catch(InterruptedException e){
					System.err.println("Thread Crash Interrupted: "+e.getLocalizedMessage());
				}
				//update to next crash command
				if(this.crashes.size()>0){
					this.current_k = this.crashes.get(0)[0];
					this.current_delta = this.crashes.get(0)[1];
				}else{//if out of crash commands, then set to zero, will not crash again
					this.current_k = 0;
					this.current_delta = 0;
				}
			}else{//could be unreachable code
				this.current_k = 0;
				this.current_delta = 0;
			}
			Server.this.recoverLibrary();
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
