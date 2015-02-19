import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Follows these constraints:
 * 
 * a) There is no read-write or write-write conflicts
 * 
 * b) A writer thread that invokes beginWrite() will be blocked until 
 * 		all preceding reader and writer threads have acquired and released the lock.
 * 
 * c) A reader thread that invokes beginRead() will be blocked until 
 * 		all preceding writer threads have acquired and released the lock.
 * 
 * d) A reader thread cannot be blocked if all preceding writer threads 
 * 		have acquired and released the lock or no preceding writer thread exists.
 */
public class FairReadWriteLock {
	
	private AtomicBoolean lock;
	private int numReaders;
	
	public FairReadWriteLock(){
		lock = new AtomicBoolean(false);
		numReaders = 0;
		
	}
	
	public synchronized void beginRead(){
		
			while(numReaders<=0 && lock.get()){
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			numReaders++;
			
			if(numReaders==1){
				lock.set(true);
			}
			notifyAll();
			//READ
//			System.out.println("reading "+Thread.currentThread());
//			System.out.println("numReaders = "+numReaders +"\n");
	}
	
	public synchronized void endRead(){
			numReaders--;
			if(numReaders==0){
				lock.set(false);
			}
			notifyAll();
	}
	
	public synchronized void beginWrite(){
		while(numReaders>0 || lock.get()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			lock.set(true);
			//WRITE
//			System.out.println("writing "+Thread.currentThread());
//			System.out.println("numReaders = "+numReaders +"\n");
		
	}

	public synchronized void endWrite(){
		lock.set(false);
		notifyAll();
	}
}
