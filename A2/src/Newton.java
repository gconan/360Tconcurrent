
public class Newton implements Runnable {
	private static Newton instance = new Newton(); //singleton
	
	
	private Thread newt;
	private Boolean keepDigging;
	
	
	private Newton(){
		newt = new Thread();
		keepDigging = false;
	}
	
	public static Newton getInstance(){
		return instance;
	}

	@Override
	public void run() {
		
		while(keepDigging){
			
			
		}
		
	}

}
