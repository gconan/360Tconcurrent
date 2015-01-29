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
	public static int parallelSearch(int x, int[] A, int numThreads) {
		//your implementation goes here	
		ExecutorService es = Executors.newSingleThreadExecutor();
		PSearch[] test = new PSearch[numThreads];
		for(int i = 0; i < numThreads; i++){
			test[i] = new PSearch(x, A, numThreads, i); //send call something to tell which part of the array to search
			Future<Integer> check = es.submit(test[i]);
			try {
				int result = check.get();
				if(result != -1){
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
		int len = b.length;
		//System.out.println(len);
		int size = len/n;
		//System.out.println(size);
		int rem = len%n;
		//System.out.println(rem);
		for(int i = 0; i < len; i++){
			//need to figure out how to search only part of the array
		}
		int start;
		/*if(start !=0 ){
			start++;
		}*/
		start = size*place;
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
//		int[] A = {5,8,3,2,7,45,56,67,12,2,32};
//		print(A);
//		parallelSort(A,0, A.length);
//		print(A);
		int[] b = {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
		parallelSearch(1, b, 8);
	}

}
