
public class Driver {
	
	public static void main (String[] args){
		FairReadWriteLock lock = new FairReadWriteLock();
		
		Thread[] threads = new Thread[10];
		for(int i=0; i<10; i++){
			threads[i] = new Thread();
		}
		
		
	}

}
