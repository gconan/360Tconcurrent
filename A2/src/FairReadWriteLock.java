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
	
	
	Semaphore mutex = new Semaphore(1); //this is analogous to a binarySemaphore
	
	
	
	
	public void beginRead(){
		
	}
	
	public void endRead(){
		
	}
	
	public void beginWrite(){
		
	}

	public void endWrite(){
		
	}
}
