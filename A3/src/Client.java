import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
	private String ID;
	private InetAddress ip;
	
	public Client(){
		this.ID = null;
		this.ip = null;
	}
	
	
	public void inputFirstLine(String line){
		String[] lineOne = line.split(" ");
		this.ID = 'c' + lineOne[0];
		try {
			this.ip = InetAddress.getByName(lineOne[1]);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void inputLines(String line){
		String[] words = line.split(" ");
		String bookNumber = words[0];
		String action = words[1];
		int port = Integer.parseInt(words[2]);
		String protocol = words[3];
		if(words[0].equals("sleep")){
			try {
				Thread.sleep(Integer.parseInt(words[1]));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		serverCall(bookNumber, action, port, protocol);
	}
	
	public void serverCall(String book, String action, int port, String protocol){
		
		
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		Scanner sc = new Scanner(System.in);
		c.inputFirstLine(sc.nextLine());
		while(true){
			c.inputLines(sc.nextLine());
		}
	}
	
}
