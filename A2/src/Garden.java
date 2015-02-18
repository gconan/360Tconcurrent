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
	private int emptyHoles;
	private int seededHoles;
	
	Lock lock = new ReentrantLock();
	Condition holes = lock.newCondition();
	Condition seeds = lock.newCondition();
	
	
	public Garden(int MAX){
		this.max = MAX;
		emptyHoles = 0;
		seededHoles = 0;
	}
	
	public void startDigging(){
		
	}
	
	public void doneDigging(){
		
	}
	
	public void startSeeding(){
		
	}
	
	public void doneSeeding(){
		
	}
	
	public void startFilling(){
		
	}
	
	public void doneFilling(){
		
	}
	
}

