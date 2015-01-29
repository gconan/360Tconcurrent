import java.util.ArrayList;
import java.util.concurrent.*;


public class PSearch implements Callable<Integer>{
	public static ExecutorService threadPool = Executors.newCachedThreadPool();
	Integer n;
	int[] b;
	int find;
	public PSearch(int find, int[] b, Integer n) {
	    this.n = n;
	    this.b = b;
	    this.find = find;
	  }
	public static void parallelSearch(int x, int[] A, int numThreads) {
		//your implementation goes here	
		ExecutorService es = Executors.newSingleThreadExecutor();
		PSearch[] test = new PSearch[numThreads];
		for(int i = 0; i < numThreads; i++){
			test[i] = new PSearch(x, A, numThreads); //send call something to tell which part of the array to search
			es.submit(test[i]);
		}
        es.shutdown ();
        threadPool.shutdown();
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
		
		
		System.out.println(n);
		return 0;
	}
	
	public static void main(String[]args){
//		int[] A = {5,8,3,2,7,45,56,67,12,2,32};
//		print(A);
//		parallelSort(A,0, A.length);
//		print(A);
		int[] b = {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
		parallelSearch(20, b, 8);
	}

}
