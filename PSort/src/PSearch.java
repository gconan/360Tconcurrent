import java.util.ArrayList;
import java.util.concurrent.*;


public class PSearch implements Callable<Integer>{
	public static ExecutorService threadPool = Executors.newCachedThreadPool();
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
			        threadPool.shutdown();
					return result;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
        es.shutdown ();
        threadPool.shutdown();
        System.out.println("not found");
        return -1;
	}
	
	public Integer call(){
		
		//MATH
		int len = b.length;
		//System.out.println(len);
		int size = len/n;
		//System.out.println(size);
		int rem = len%n;
		//System.out.println(rem);
		int start = size*place;
		/*
		while(((size*place) + size) > (len + 1)){
			size--;
		}
		if(rem > 0){
			if(rem > place){
				size++;
				if(place != 0){
					start = start + place;
				}
			}
			if(place >= rem){
				start = start + rem;
			}
			
		}
		*/
		
		
		//System.out.println(place + " " + len + " " + rem + " " + size + " " + start);
		for(int j = start; j < start+size; j++){
			//System.out.println(b[j]);
			if(b[j] == find){
				System.out.println("FOUND!");
				return j;
			}
		}
		return -1;
	}
	
	public static void main(String[]args){
		int[] b = {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
		parallelSearch(1, b, 8);
	}

}
