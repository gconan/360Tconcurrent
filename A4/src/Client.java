import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
	private String ID;
	private InetAddress IP;
	int numServs;
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
	
	public void inputLines(String line, int servs){
		if(servs > 0){
			
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
			InetAddress serverIP;
			int serverPort;
			//look for closest noncrashed server
			try {
				Socket server = new Socket(IP , port);
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
		while(sc.hasNextLine() && servs > 0){
			c.inputLines(sc.nextLine(), servs);
			servs--;
		}
		while(sc.hasNextLine()){
			c.inputLines(sc.nextLine(), servs);
		}
	}
	
}
