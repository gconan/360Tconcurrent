import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Not sure how this one with the threads is supposed to work yet
 * 
 * Progress Constraints:
 * 
 * a) Benjamin cannot plant a seed unless at least one empty hole exists, but 
 * 		Benjamin does not care how far Newton gets ahead of him.
 * 
 * b) Mary cannot fill a hole unless at least one hole exists in which Benjamin 
 * 		has plated a seed. Mary does not care how far Bejamin gets ahead of her.
 * 
 * c) Newton DOES care about the number of unfilled holes. 
 * 		Newton has to wait for Mary if there are MAX unfilled holes.
 * 
 * d) There is only one shovel that can be used to dig and fill holes, and thus Newton and Mary need to coordinate between themseleves for using the shovel; 
 * 		ie. only one of them can use the shovel at any point in time.
 *
 */
public class Garden {
	
	final int max;
	private int dugHoles;
	private int seededHoles;
	private Semaphore shovelMutex = new Semaphore(1);
	
	Lock lock = new ReentrantLock();
	Condition digAvail = lock.newCondition();
	Condition seedAvail = lock.newCondition();
	Condition fillAvail = lock.newCondition();
	
	private AtomicInteger totalHoles = new AtomicInteger(0);
	private AtomicInteger totalSeeds = new AtomicInteger(0);
	private AtomicInteger totalFilled = new AtomicInteger(0);
	
	public Garden(int MAX){
		this.max = MAX;
		dugHoles = 0;
		seededHoles = 0;
	}
	
//*********************Implemented Interface Methods*****************************
	public int totalHolesDugByNewton(){
		return totalHoles.get();
	}

	public int totalHolesSeededByBenjamin(){
		return totalSeeds.get();
	}

	public int totalHolesFilledByMary(){
		return totalFilled.get();
	}
	
//**********************"Worker" Methods*********************************	
	public void startDigging(){
		lock.lock();
		try{
			while(dugHoles >= max){
				try {
					digAvail.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				shovelMutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	public void doneDigging(){
		lock.lock();
		try{
			totalHoles.incrementAndGet();
			dugHoles++;
			shovelMutex.release();
			seedAvail.signal();
		}
		finally{
			lock.unlock();
		}
	}
	
	public void startSeeding(){
		lock.lock();
		try{
			while(dugHoles <= 0){
				try {
					seedAvail.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	public void doneSeeding(){
		lock.lock();
		try{
			totalSeeds.incrementAndGet();
			seededHoles++;
			fillAvail.signal();
		}
		finally{
			lock.unlock();
		}
	}
	
	public void startFilling(){
		lock.lock();
		try{
			while(seededHoles <= 0){
				try {
					fillAvail.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				shovelMutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		finally{
			lock.unlock();
		}
	}
	
	public void doneFilling(){
		lock.lock();
		try{
			totalFilled.incrementAndGet();
			seededHoles--;
			dugHoles--;
			shovelMutex.release();
			digAvail.signal();
		}
		finally{
			lock.unlock();
		}
	}
	
}

