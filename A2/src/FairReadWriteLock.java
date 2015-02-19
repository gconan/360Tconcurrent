import java.util.concurrent.Semaphore;

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
	
	private Semaphore mutex;
	private Semaphore writeLock;
	private int numReaders;
	
	public FairReadWriteLock(){
		mutex = new Semaphore(1);
		writeLock = new Semaphore(1);
		numReaders = 0;
		
	}
	
	public void beginRead(){
		try {
			mutex.acquire();
			numReaders++;
			
			if(numReaders==1){
				writeLock.acquire();
			}
				//ensures that other read threads can access
			//READ
			System.out.println("reading"+Thread.currentThread());
			mutex.release();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void endRead(){
		try {
			mutex.acquire();
			numReaders--;
			if(numReaders==0){
				writeLock.release();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void beginWrite(){
		try {
			writeLock.acquire();
			//WRITE
			System.out.println("writing"+Thread.currentThread()); //TODO
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void endWrite(){
		writeLock.release();
	}
}
