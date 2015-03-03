import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;


public class Server {
	private ServerSocket TCPSocket;
	private DatagramSocket UDPSocket;
	private HashMap<Integer, String> library = new HashMap<Integer, String>();
	private ExecutorService pool;
	
	public Server(){
		Scanner scan = new Scanner(System.in);	//use "standard input"
		try{
			this.configureServer(scan.nextLine());
		}catch(Exception e){
			System.out.println(e.getMessage());	//use "standard output"
			e.printStackTrace();
		}finally{
			scan.close();
		}
	}

	private void configureServer(String configString) throws NumberFormatException, IOException {
		
		String[] configArgs = configString.split(" ");
		
		//initialize our library
			//trim in case of extra white space added by sloppy user
		for(int i=0; i<Integer.parseInt(configArgs[0].trim()); i++){
			library.put(i, "available");
		}
		
		TCPSocket = new ServerSocket(Integer.parseInt(configArgs[1].trim()));
		UDPSocket = new DatagramSocket(Integer.parseInt(configArgs[2].trim()));
		
		
	}
		
	private void openDoorsForBusiness() {
		//create listeners on both TCP and UDP and let the client requests flow
		try{
			
		}finally{
			try {
				TCPSocket.close();
				UDPSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
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
	
	
	
//********************************************************NESTED CLASS: TCP MONITOR*********************************************
	//runnable classes allow for multiple threads to monitor the socket and service requests
	
	
	protected class TCPrequestServicer implements Runnable{
		Socket sock;
		
		protected TCPrequestServicer(Socket soc){
			this.sock = soc;
		}
		
		@Override
		public void run() {	//service client request
			try {	//TODO check Gargs version
				Scanner inputStream = new Scanner(sock.getInputStream());
				PrintWriter outputStream = new PrintWriter(sock.getOutputStream());
				String request = inputStream.nextLine();
				String response = Server.this.process(request);
				outputStream.println(response);
				outputStream.flush();
		        outputStream.close();
		        sock.close();
		        
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	protected class TCPmonitor implements Runnable{
		
		protected TCPmonitor(){ }	//nothing to init

		@Override
		public void run() {
			try {
				Socket sock; 
				while((sock= TCPSocket.accept()) !=null){	//assignment inside the while condition so that it reassigns itself
					pool.submit(new TCPrequestServicer(sock));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	
//***************************************************MAIN FUNCTION FOR RUNNING FROM COMMMAND LINE*******************************
	
	
	/**
	 * starts the server and allows it to run requests
	 * @param args
	 */
	public static void main(String[] args){
		Server server = new Server();
		server.openDoorsForBusiness();
	}
}
