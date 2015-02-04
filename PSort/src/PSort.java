import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PSort implements Runnable{
	public static ExecutorService threadPool = Executors.newCachedThreadPool();
	private int[] A;
	private int begin;
	private int end;
	
	public PSort(int[]a, int b, int e){
		this.A = a;
		this.begin = b;
		this.end = e;
	}
	
	public static void parallelSort(int[] A, int begin, int end) {
		ExecutorService es = Executors.newSingleThreadExecutor();
		if((end-begin)<=1){
			return;
		}
		
		int pivot = A[begin];
		int pivotIndex;
		
		ArrayList<Integer> lessThan = new ArrayList<Integer>();
		ArrayList<Integer> greaterThan = new ArrayList<Integer>();
		for(int i=begin+1; i<end; i++){//O(n)
			if(A[i]<=pivot){lessThan.add(A[i]);}
			if(A[i]>pivot){greaterThan.add(A[i]);}
		}
		
		//move less than numbers before pivot and greater than numbers after pivot
		//save pivot index for recurssive call
		int i=begin;
		for(int j=0; j<lessThan.size(); j++){//O(n)
			A[i+j]=lessThan.get(j);
		}
		i+=lessThan.size();
		
		A[i]=pivot;
		pivotIndex=i;
		i+=1;
		
		for(int j=0; j<greaterThan.size(); j++){//O(n)
			A[i+j]=greaterThan.get(j);
		}
		
		/*PSort f1 = new PSort(A,begin, pivotIndex);
        PSort f2 = new PSort(A, pivotIndex+1, end);
		es.execute(f1);
		es.execute(f2);*/
		
		
		PSort f1 = new PSort(A,begin, pivotIndex);
        Thread t1 = new Thread(f1);
        PSort f2 = new PSort(A, pivotIndex+1, end);
        Thread t2 = new Thread(f2);
       	es.execute(t1);
        es.execute(t2);
        es.shutdown();
        try {
			es.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /*t1.start();
        t2.start();
        try{
        	t1.join();
        	t2.join();
        }catch(Exception e){
        	System.out.println("boooo");
        }*/
	}

	@Override
	public void run() {
		parallelSort(this.A, this.begin, this.end);
		
	}
	

}
