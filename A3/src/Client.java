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
	byte[] rbuffer = new byte[1024];
	
	public Client(){
		this.ID = null;
		this.IP = null;
	}
	
	
	public void inputFirstLine(String line){
		String[] lineOne = line.split(" ");
		this.ID = 'c' + lineOne[0];
		try {
			this.IP = InetAddress.getByName(lineOne[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void inputLines(String line){
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
	
	public void serverCall(String book, String action, int port, String protocol){
		String call = ID + " " + book + " " + action;
		if(protocol.equals("T")){
			//TCP
			String output;
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
		} else if(protocol.equals("U")){
			//UDP
			DatagramPacket sPacket, rPacket;
			try {
				DatagramSocket datasocket = new DatagramSocket();
				byte[] buffer = new byte[call.length()];
	        	buffer = call.getBytes();
				sPacket = new DatagramPacket(buffer, buffer.length, IP, port);
				datasocket.send(sPacket);            	
	        	rPacket = new DatagramPacket(rbuffer, rbuffer.length);
	        	datasocket.receive(rPacket);
	        	String retstring = new String(rPacket.getData(), 0,
	        			rPacket.getLength());
	        	System.out.println(retstring);
				
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else{
			//welp
		}
		
		
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		Scanner sc = new Scanner(System.in);
		c.inputFirstLine(sc.nextLine());
		while(sc.hasNextLine()){
			c.inputLines(sc.nextLine());
		}
	}
	
}
