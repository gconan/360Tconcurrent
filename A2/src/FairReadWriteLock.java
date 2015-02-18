import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

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
	
	private AtomicBoolean readLocked;
	private AtomicBoolean writeLock;	//false if taken; true if available.
	private int numReaders;
	
	public FairReadWriteLock(){
		readLocked = new AtomicBoolean(false);
		writeLock = new AtomicBoolean(true);
		numReaders = 0;
		
	}
	
	public void beginRead(){
		if(writeLock.compareAndSet(true, false)){	//if the write lock is available and set to not available
			if(readLocked.compareAndSet(false, true)){
				numReaders++;
				writeLock.compareAndSet(false, true);
				readLocked.compareAndSet(true, false);
				notifyAll();
			}else{
				try {
					writeLock.compareAndSet(false, true);
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//read
				
		}else{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void endRead(){
		if(readLocked.compareAndSet(false, true)){
			numReaders--;
			readLocked.compareAndSet(true, false);
			notifyAll();
		}else{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void beginWrite(){
		if(writeLock.compareAndSet(false, true)){
			if(readLocked.compareAndSet(false, true)){
				numReaders++;
				readLocked.compareAndSet(true, false);
				notifyAll();
			}else{
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//read
				
		}else{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void endWrite(){
		
	}
}
