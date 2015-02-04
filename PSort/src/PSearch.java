import java.util.ArrayList;
import java.util.concurrent.*;


public class PSearch implements Callable<Integer>{
	Integer n;
	int[] b;
	int find;
	int place;
	public PSearch(int find, int[] b, Integer n, int place) {
	    this.n = n;
	    this.b = b;
	    this.find = find;
	    this.place = place;
	  }
	public static int parallelSearch(int x, int[] A, int numThreads) { //add math to divide up array into parallelSearch and take it out of call()
		//your implementation goes here	
		ExecutorService es = Executors.newSingleThreadExecutor();
		PSearch[] test = new PSearch[numThreads];
		for(int i = 0; i < numThreads; i++){
			test[i] = new PSearch(x, A, numThreads, i); 
			
			//moving math up here
			int len = A.length;
			//System.out.println(len);
			int size = len/numThreads;
			//System.out.println(size);
			int rem = len%numThreads;
			//System.out.println(rem);
			int start = size*i;
			while(((size*i) + size) > (len + 1)){
				size--;
			}
			if(rem > 0){
				if(rem > i){
					size++;
					if(i != 0){
						start = start + i;
					}
				}
				if(i >= rem){
					start = start + rem;
				}
				
			}
			
			Future<Integer> check = es.submit(test[i]);
			try {
				int result = check.get();
				if(result != -1){
					es.shutdown ();
					return result;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
		}
        es.shutdown ();
        System.out.println("not found");
        return -1;
	}
	
	public Integer call(){
		
		//MATH
		int len = b.length;
		int size = len / n;
		int rem = len % n;
		int start = size*place;

		for(int j = start; j < start+size; j++){
			if(b[j] == find){
				System.out.println("FOUND!");
				return j;
			}
		}
		return -1;
	}
}
