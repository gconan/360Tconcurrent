import java.util.concurrent.Semaphore;


public class CyclicBarrier {
	
	private final int numberOfParties;
	private int partiesArrived;
	private Semaphore mutex;

	/**
	 * creates a new cyclicbarrier that will trip when the given number of parties (thread) are waiting upon it
	 * @param parties
	 */
	public CyclicBarrier (int parties){
		numberOfParties = parties;
		mutex = new Semaphore(1);
	}
	
	/**
	 * waits until all parties have invoked await on this barrier. If the current thread is not the last to 
	 * arrive then it is disabled for thread scheduling purposes and lies dormant until the last thread arrives. 
	 * Returns: the arrival index of the current thread, where index (parties-1) indicates the first to arrive and 
	 * zero indicates the last to arrive.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	int await() throws InterruptedException{
		mutex.acquire();
		partiesArrived++;
		mutex.release();
		if(numberOfParties-partiesArrived==0){
			notifyAll();
			partiesArrived = 0;
			return 0;
		}else{
			wait();
		}
		return numberOfParties-partiesArrived;
		
	}
}
