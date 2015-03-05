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
	
	private ReadWriteLockLogger logger;
	private AtomicBoolean lock;
	private int numReaders;
	
	public FairReadWriteLock(){
		lock = new AtomicBoolean(false);
		numReaders = 0;
		logger = new ReadWriteLockLogger();
		
	}
	
//*************************READER************************************
	public synchronized void beginRead(){
			logger.logTryToRead();
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
			logger.logBeginRead();
			//READ
	}
	
	public synchronized void endRead(){
			logger.logEndRead();
			numReaders--;
			if(numReaders==0){
				lock.set(false);
			}
			notifyAll();
	}
	
	
//***************************WRITER*********************************************
	public synchronized void beginWrite(){
		logger.logTryToWrite();
		while(numReaders>0 || lock.get()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			lock.set(true);
			logger.logBeginWrite();
			//WRITE
		
	}

	public synchronized void endWrite(){
		logger.logEndWrite();
		lock.set(false);
		notifyAll();
	}
}
