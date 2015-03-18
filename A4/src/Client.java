import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;


public class Client {
	private String ID;
	private InetAddress IP;
	int numServs;
	ArrayList<ReplicaServers> servList = new ArrayList<ReplicaServers>();
	byte[] rbuffer = new byte[1024];
	
	public Client(){
		this.ID = null;
		this.IP = null;
	}
	
	
	public int inputFirstLine(String line){
		String[] lineOne = line.split(" ");
		this.ID = lineOne[0];
		this.numServs = Integer.parseInt(lineOne[1]);
		return numServs;
	}
	
	public void inputLines(String line, int servs, int count){
		if(servs > 0){
			String[] words = line.split(":");
			InetAddress tempIP = null;
			try {
				tempIP = InetAddress.getByName(words[0]);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			int port = Integer.parseInt(words[2]);
			String protocol = words[3];
			
			serverCall(bookNumber, action, port, protocol);
		}
		
	}
	
	public void serverCall(String book, String action, int port, String protocol){
		String call = ID + " " + book + " " + action;
			//TCP stuff
			String output;
			InetAddress serverIP = null;
			int serverPort = 0;
			ReplicaServers temp;
			//look for closest noncrashed server
			if(servList.size() == 0){
				return;
			}
			for(int i = 0; i < servList.size(); i ++){
				temp = servList.get(i);
				if(temp.isCrashed()){
					i++;
				} else{
					serverIP = temp.getIP();
					serverPort = temp.getPort();
					break;
				}
			}
			
			
			try {
				Socket server = new Socket(serverIP , serverPort);
				Scanner din = new Scanner(server.getInputStream());
				PrintWriter pout = new PrintWriter(server.getOutputStream(), true);
				pout.println(call);
				output = din.nextLine();
				System.out.println(output);
				din.close();
				pout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		Scanner sc = new Scanner(System.in);
		int servs = c.inputFirstLine(sc.nextLine());
		int i = 0;
		while(sc.hasNextLine() && servs > 0){
			c.inputLines(sc.nextLine(), servs, i);
			servs--;
			i++;
		}
		while(sc.hasNextLine()){
			c.inputLines(sc.nextLine(), servs, i);
		}
	}
	
}
