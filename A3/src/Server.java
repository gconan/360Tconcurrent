import java.io.IOException;
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
		
	}
	
//********************************************************NESTED CLASS: TCP MONITOR*********************************************
	
	protected class TCPmonitor implements Runnable{
		
		protected TCPmonitor(Socket sock){
			
		}

		@Override
		public void run() {
			
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
