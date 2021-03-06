import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Michael
 *	client that sends requests via TCP to a library server
 */
public class Client {
	private String ID;
	private InetAddress IP;
	int numServs;
	ArrayList<ReplicaServers> servList = new ArrayList<ReplicaServers>();
	byte[] rbuffer = new byte[1024];
	
	/**
	 * simple Client constructor
	 */
	public Client(){
		this.ID = null;
		this.IP = null;
	}
	
	/**
	 * parses first line of client input file
	 * @param line
	 * @return
	 */
	public int inputFirstLine(String line){
		String[] lineOne = line.split(" ");
		this.ID = lineOne[0];
		this.numServs = Integer.parseInt(lineOne[1]);
		return numServs;
	}
	
	/**
	 * parses remaining lines of client input file
	 * @param line
	 * @param servs
	 * @param count
	 */
	public void inputLines(String line, int count){ 
		char[] check = line.toCharArray();
		if(check[0] != 'b' && check[0] != 's'){
			String[] words = line.split(":");
			InetAddress tempIP = null;
			try {
				tempIP = InetAddress.getByName(words[0]);
			} catch (UnknownHostException e) {
				System.err.println("Unknown Host Exception");
			}
			int port = Integer.parseInt(words[1]);
			ReplicaServers tempServ = new ReplicaServers(count+1,tempIP,port);
			servList.add(tempServ);
		} else{
			String[] words = line.split(" ");
			String bookNumber = words[0];
			String action = words[1];
			if(words[0].equals("sleep")){
				try {
					Thread.sleep(Integer.parseInt(words[1]));
				} catch (NumberFormatException e) {
					System.err.println("Bad input for sleep time.");
				} catch (InterruptedException e) {
					System.err.println("Interruped Exception.");
				}
				return;
			}
			serverCall(bookNumber, action);
		}
		
	}
	
	/**
	 * once client input is parsed, send requests to server
	 * @param book
	 * @param action
	 * @param port
	 * @param protocol
	 */
	public void serverCall(String book, String action){
		String call = ID + " " + book + " " + action;
			//TCP stuff
			String output;
			InetAddress serverIP = null;
			int serverPort = 0;
			ReplicaServers temp;
			boolean connected = false;
			//look for closest noncrashed server
			int i = 0;
			while(i < servList.size() && !connected){
				temp = servList.get(i);
				serverIP = temp.getIP();
				serverPort = temp.getPort();
				try {
					Socket server = new Socket();
					server.connect(new InetSocketAddress(serverIP, serverPort), 100);
					//connected = true;
					BufferedReader br = new BufferedReader(new InputStreamReader(
							server.getInputStream()));
					//Scanner din = new Scanner(server.getInputStream());
					PrintWriter pout = new PrintWriter(server.getOutputStream(), true);
					pout.println(call);
					if(!br.ready()){
						Thread.sleep(100);
					}
					if(!br.ready()){
						i++;
					}else{
						output = br.readLine();
						connected = true;
						System.out.println("Server response: " + output);
					}
					server.close();
					br.close();
					pout.close();
				} catch (Exception e) {
					if(e.getClass() == SocketTimeoutException.class){
						//try next server
						servList.get(i).setAck(true); //set ack to true if we are assuming a crash
						i = (i + 1)%servList.size();
						if(i == 0){
						}
					} else{
						i = (i + 1)%servList.size();
						if(i == 0){
						}
					}
				}	
			}
	}
	
	/**
	 * print server list
	 * @return
	 */
	protected String printReplicaSet(){
		String result = "";
		for(ReplicaServers s: this.servList){
			result+=s.toString()+" ";
		}
		System.out.println(result);
		return result;
	}
	
	/**
	 * reads through client files
	 * @param args
	 */
	public static void main(String[] args) {
		Client c = new Client();
		Scanner sc = new Scanner(System.in);
		int servs = c.inputFirstLine(sc.nextLine());
		int count = 0; //number of servers specified in client input file
		while(sc.hasNextLine()){
			c.inputLines(sc.nextLine(), count);
			count++;
		}
		sc.close();
	}
	
}
