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
import java.util.concurrent.atomic.AtomicBoolean;

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
	private InetAddress IP;
	private int port;
	private boolean csReady;
	protected AtomicBoolean finishedCommand;
	
	/**
	 * constructor for the libarary server
	 * takes in standard input and configures the server with the given information
	 */
	public Server(Scanner scan){
		this.finishedCommand = new AtomicBoolean();
		this.csReady = false;
		this.myClock = 0;
		humanResources = Executors.newCachedThreadPool();
		this.replicas = new ArrayList<ReplicaServers>();
		replicas.add(new ReplicaServers());
		this.crashCommands = new ArrayList<int[]>();
		try{
			String line = scan.nextLine();
			this.configureServer(line);
			for(int i=1; i<=this.numOfServers; i++){
				line = scan.nextLine();
				if(this.ID ==  i){
					String[] ipconfig = line.split(":");
					if(InetAddress.getLocalHost().getHostAddress().equals(ipconfig[0]) || ipconfig[0].equals("127.0.0.1") || ipconfig[0].substring(0, 7).equals("127.0.0")){
						port = Integer.parseInt(ipconfig[1].trim());
						try{
							TCPSocket = new ServerSocket(port);
						}catch(Exception e){
							throw new Exception("Possibility: the port is already in use on this machine. "+e.getLocalizedMessage());
						}
					}else{
						throw new Exception("This ID is already linked to another IP address. Server not starting");
					}
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
	
	/**
	 * adds replica at index== id
	 * zero'th replica is a dummy with ack==true all the time
	 * @param line
	 * @param id
	 * @throws Exception
	 */
	private void addNewReplica(String line, int id) throws Exception{
		String[] creds = line.split(":");
		if(creds.length!=2){
			throw new Exception("Bad Input, cant add new replica server");
		}else{
			InetAddress ip = null;
			int port = 0;
			try{
				ip = InetAddress.getByName(creds[0]);
			}catch(UnknownHostException e){
				throw new Exception("Could not determine the IP address given. "+e.getMessage());
			}
			try{
				port = Integer.parseInt(creds[1].trim());
			}catch(NumberFormatException e){
				throw new Exception("Could not determine the port number given. " + e.getMessage());
			}
			replicas.add(new ReplicaServers());
			replicas.set(id,new ReplicaServers(id,ip, port));
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
			//this.library.set(i, "available"); 
			//TA said we do not have to do anything except be a useless server
			//this means that we use the last known data (this helps a lot for one server configuration)
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
		int i = 1;
		while(i < replicas.size()){
			if(replicas.get(i).getID() == this.ID){
				replicas.get(i).setAck(true);
				i++;
			}else{
				int port = replicas.get(i).getPort();
				InetAddress serverIP = replicas.get(i).getIP();
				try {
					Socket server = new Socket();
					server.connect(new InetSocketAddress(serverIP, port), 100);
					PrintWriter pout = new PrintWriter(server.getOutputStream(), true);
					pout.println("request");
					pout.flush();
					pout.println(this.ID);
					pout.flush();
					pout.println(this.getMyClock());
					pout.flush();
					//output = din.nextLine();
					//System.out.println(output);
					pout.flush();
					pout.close();
					server.close();
				} catch (Exception e) {
					if(e.getClass() == SocketTimeoutException.class){
						//try next server
						replicas.get(i).setAck(true); //set ack to true if we are assuming a crash
						i++;
					} else{
						i++;
					}
				}
				i+=1;
			}
		}
		while(!csReady){
			csReady=true;
			for(int j=1; i<replicas.size(); i++){
				if(!replicas.get(j).hasAck()){
					csReady=false;
					break;
				}
			}
		}
	}
	
	/**
	 * send requests to each server for recovery. if one doesn't respond, assume dead and move along.
	 */
	public void sendRequestToServers(String dontSendtoThisID) {
		int _dontSendtoThisID =Integer.parseInt(dontSendtoThisID);
		//send message to servers: request, id, clock
		int i = 1;
		while(i < replicas.size()){
			if(replicas.get(i).getID() == this.ID || replicas.get(i).getID()== _dontSendtoThisID){
				replicas.get(i).setAck(true);
				i++;
			}else{
				int port = replicas.get(i).getPort();
				InetAddress serverIP = replicas.get(i).getIP();
				try {
					Socket server = new Socket();
					server.connect(new InetSocketAddress(serverIP, port), 100);
					Scanner din = new Scanner(server.getInputStream());
					PrintWriter pout = new PrintWriter(server.getOutputStream(), true);
					pout.println("request");
					pout.flush();
					pout.println(this.ID);
					pout.flush();
					pout.println(this.getMyClock());
					pout.flush();
					din.close();
					pout.flush();
					pout.close();
					server.close();
				} catch (Exception e) {
					if(e.getClass() == SocketTimeoutException.class){
						//try next server
						replicas.get(i).setAck(true); //set ack to true if we are assuming a crash
						i++;
					} else{
						i++;
					}
				}
				i+=1;
			}
		}
		while(!csReady){
			csReady=true;
			for(int j=1; i<replicas.size(); i++){
				if(!replicas.get(j).hasAck()){
					csReady=false;
					break;
				}
			}
		}
	}

	public void processRelease(Scanner input) {
		String id = input.nextLine();
		for(int i=0; i<library.size(); i++){
			this.library.set(i, input.nextLine());
		}
		this.replicas.get(Integer.parseInt(id)).setAck(true);
		
		
	}
	

	public void sendAcknowledgment(int id) {
		InetAddress receipientIP = replicas.get(id).getIP();//changed -1 bc of server shift
		int port = replicas.get(id).getPort();//changed -1 bc of the 0 shift
		try {
			Socket socket = new Socket(receipientIP , port);
			PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
			pout.println("acknowledge");
			pout.flush();
			pout.println(this.ID);
			pout.flush();
			pout.close();
			socket.close();
		} catch (IOException e) {
			//e.printStackTrace();
			//could be "sleeping"
		}
	
	}
	
	public void updateReplicaLibraries(){
		int i = 1;
		while(i < replicas.size()){
			if(replicas.get(i).getID() != this.ID){
				int port = replicas.get(i).getPort();
				InetAddress serverIP = replicas.get(i).getIP();
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(serverIP, port), 100);
					PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
					pout.println("update");
					pout.flush();
					this.sendLibrary(pout);
					pout.flush();
					pout.close();
					socket.close();
				}catch(IOException e){
					//System.err.println("Server "+i+" must be dead. In Update");
				}
			}
			i++;
		}
	}
	
	public void sendLibrary(PrintWriter pout){
			for(String s: library){
				pout.println(s);
				pout.flush();
			}
	}
	
	public void recoverLibrary() {
		//send message to a server and ask for library
		//receive message with lib data in the form:
		// available
		// reserved
		//....line number cooresponds to booknum-1
		String message = "recover"+"\n"+ID+"\n"+IP+"\n"+port;
		int timeout = 0;
		boolean connected = false;
		int i=1;
		Socket socket = null;
		
		while((!connected && i<replicas.size())&&timeout<10000){
			timeout++;
			socket = new Socket();
			if(replicas.get(i).getID() == this.ID){
				i= (i+1)%replicas.size();
				if(i==0)i=1;
			}else{
				InetAddress receipientIP = replicas.get(i).getIP();
				int port = replicas.get(i).getPort();
				try {
					socket.connect(new InetSocketAddress(receipientIP , port), 100);
					connected = true;
				}catch(SocketTimeoutException e){
					try {
						socket.close();
					} catch (IOException e1) {
						//System.err.println("Coulnt close socket after Socket Timeout");
					}
					if(e.getClass() == SocketTimeoutException.class){
						i= (i+1)%replicas.size();
						if(i==0)i=1;
					}
				} catch (IOException e) {
					try {
						socket.close();
					} catch (IOException e1) {
						//System.err.println("Coulnt close socket after IO Exception");
					}
					//System.err.println("Server "+i+" must be dead. In Recover. "+e.getLocalizedMessage());
				}
				i= (i+1)%replicas.size();
				if(i==0)i=1;
			}
		}
			//should have a connection to one of the servers
			
			try{
				PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
				Scanner din = new Scanner(socket.getInputStream());
				pout.println(message);
				int j=0;
				while(j<this.library.size()){
					library.set(j, din.nextLine());
					j+=1;
				}
				din.close();
				pout.flush();
				pout.close();
				socket.close();
			} catch (IOException e) {
				//System.err.println("Could not close socket after library recover");
			}
	}
	
	public void updateLib(Scanner din){
		
		for(int j=0; j<library.size(); j++){
			library.set(j, din.nextLine());
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
		for(int i=1; i<replicas.size(); i++){
			result+=replicas.get(i).toString()+" ";
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
		protected ArrayList<Integer> reqQueue;
		protected Boolean imInterested;
		
		protected TCP_librarian_service(Socket soc){
			this.sock = soc;
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
				if(request.equalsIgnoreCase("request")){
					String id = inputStream.nextLine();
					int clock = Integer.parseInt(inputStream.nextLine());
					if(clock<Server.this.getMyClock() || !imInterested){
						Server.this.sendAcknowledgment(Integer.parseInt(id));
						Server.this.replicas.get(Integer.parseInt(id)).setAck(false);
					}else{
						this.reqQueue.add(Integer.parseInt(id));
					}
					
				//UPDATE
				}else if(request.equalsIgnoreCase("update")){
					updateLib(inputStream);
					
				//RELEASE
				}else if(request.equalsIgnoreCase("release")){
					Server.this.processRelease(inputStream);	//should contain library update
					
				//ACKNOWLEDGE
				}else if(request.equalsIgnoreCase("acknowledge")){
					int s = Integer.parseInt(inputStream.nextLine());
					replicas.get(s).setAck(true);
				//RECOVER
				}else if(request.equalsIgnoreCase("recover")){
					String id = inputStream.nextLine();
					Server.this.sendRequestToServers(id);
					Server.this.sendLibrary(outputStream);
					sendRelease();
					
				//CLIENT
				}else{
					imInterested = true;
					Server.this.sendRequestToServers();	//wait (100ms) for all acks
					
					String response = Server.this.process(request);
					Server.this.updateReplicaLibraries();
					sendRelease();
					outputStream.println(response);
					imInterested = false;
					
				}
				outputStream.flush();
		        outputStream.close();
		        inputStream.close();
		        sock.close();
			}catch (IOException e) {
				//System.err.println("Library server Shutdown: "+e);
			}
			Server.this.finishedCommand.set(true);
		}
		
		public void sendRelease(){
			for(int i=0; i< reqQueue.size(); i++){
				InetAddress receipientIP = Server.this.replicas.get(reqQueue.get(i)).getIP();//changed -1 bc of server shift
				int port = Server.this.replicas.get(reqQueue.get(i)).getPort();//changed -1 bc of the 0 shift
				try {
					Socket socket = new Socket(receipientIP , port);
					Scanner in = new Scanner(socket.getInputStream());
					PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
					pout.println("release");
					pout.flush();
					in.nextLine();//wait before sending rest of info TODO
					pout.println(""+Server.this.ID);
					pout.flush();
					for(int j=0; j<library.size(); j++){
						pout.println(library.get(j));
						pout.flush();
					}
					pout.flush();
					pout.close();
					in.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//listens on tcp then services requests by submitting a librarian_service
	/**
	 * Waits for a TCP request then sends it off for processing to TCP_librarian_service 
	 * so it can continue to wait for new requests
	 */
	protected class TCP_librarian implements Runnable{
		private int current_k;
		private long current_delta;
		private ArrayList<int[]> crashes;
		
		protected TCP_librarian(ArrayList<int[]>crashCommands){
			this.crashes = crashCommands;
			if(this.crashes.size()>0){
				this.current_k = this.crashes.get(0)[0];
				this.current_delta = (long) this.crashes.get(0)[1];
			}
		}

		@Override
		public void run() {
			try {
				Socket sock; 
				while((sock= TCPSocket.accept()) !=null){	//assignment inside the while condition so that it reassigns itself
					this.current_k--;
					Server.this.finishedCommand.set(false);
					humanResources.submit(new TCP_librarian_service(sock));
					
					while(!Server.this.finishedCommand.get()){}
					
					if(this.current_k==0){	//if no crash set, then current_k will be negative and never crash
						this.crash();// not sure if crash is working
					}
					Server.this.clockUp();
				}
			} catch (IOException e) {
				//System.err.println("Library server Shutdown: "+e);
			}
			
		}
		
		private void crash(){
			if(crashes.size()>0){
				this.crashes.remove(0);
				//Server.this.crash();
				try{
					TCPSocket.close();
					Thread.sleep(this.current_delta);
				}catch(InterruptedException e){
					//System.err.println("Thread Crash Interrupted: "+e.getLocalizedMessage());
				} catch (IOException e) {
					//System.err.println("Could not close TCP ServerSocket; crash may not be successfull");
				}
				//update to next crash command
				if(this.crashes.size()>0){
					this.current_k = this.crashes.get(0)[0];
					this.current_delta = (long) this.crashes.get(0)[1];
				}else{//if out of crash commands, then set to zero, will not crash again
					this.current_k = 0;
					this.current_delta = 0;
				}
			}else{//could be unreachable code
				this.current_k = 0;
				this.current_delta = 0;
			}
			try {
				TCPSocket = new ServerSocket(port);
			} catch (IOException e) {
				//System.err.println("Could not reopen TCP ServerSocket; probably total system failure....good luck");
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
