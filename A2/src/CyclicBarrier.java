import java.util.concurrent.Semaphore;


public class CyclicBarrier {
	
	private final int numberOfParties;
	private int partiesArrived;
	private Semaphore mutex;
	private Semaphore everyoneArrived;

	/**
	 * creates a new cyclicbarrier that will trip when the given number of parties (thread) are waiting upon it
	 * @param parties
	 */
	public CyclicBarrier (int parties){
		numberOfParties = parties;
		mutex = new Semaphore(1);
		partiesArrived = 0;
		everyoneArrived = new Semaphore(0);
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
			int result = numberOfParties - partiesArrived;
		mutex.release();
		
		if(everyoneArrived.getQueueLength()<numberOfParties-1){
			everyoneArrived.acquire();
		}else{
			everyoneArrived.release(numberOfParties-1);
			partiesArrived=0;//reset
		}

		return result;
		
	}
}
