import java.net.InetAddress;


public class ReplicaServers {

	private boolean alive;
	private int id;
	private InetAddress ip;
	private int port;
	private long timeStamp;
	private boolean requested;
	
	public ReplicaServers( int ID, InetAddress IP, int prt){
		this.alive = true;
		this.id = ID;
		this.ip = IP;
		this.port = prt;
		this.timeStamp = 0;
		this.requested = true;
	}
	
	public boolean isCrashed(){
		return !alive;
	}
	
	public int getID(){
		return this.id;
	}
	
	public int getPort(){
		return this.port;
	}
	
	public InetAddress getIP(){
		return this.ip;
	}
	
	public long getTimeStamp(){
		return this.timeStamp;
	}
	
	public void setTimeStamp(int newClockValue){
		this.timeStamp = newClockValue;
	}
	
	public boolean hasRequested(){
		return this.requested;
	}
	
	public void notInterested(){
		this.requested = false;
	}
	
}
