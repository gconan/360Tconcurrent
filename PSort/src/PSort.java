import java.util.ArrayList;


public class PSort implements Runnable{
	
	private int[] A;
	private int begin;
	private int end;
	
	public PSort(int[]a, int b, int e){
		this.A = a;
		this.begin = b;
		this.end = e;
	}
	
	public static void parallelSort(int[] A, int begin, int end) {
		if((end-begin)<=1)return;
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
		//System.out.println("lessThan size = "+lessThan.size());
		for(int j=0; j<lessThan.size(); j++){//O(n)
			A[i+j]=lessThan.get(j);
		}
		i+=lessThan.size();
		
		A[i]=pivot;
		pivotIndex=i;
		i+=1;
		
		//System.out.println("greaterThan size = "+greaterThan.size());
		for(int j=0; j<greaterThan.size(); j++){//O(n)
			A[i+j]=greaterThan.get(j);
		}
		
		PSort f1 = new PSort(A,begin, pivotIndex);
        Thread t1 = new Thread(f1);
        t1.start();
        
        PSort f2 = new PSort(A, pivotIndex+1, end);
        Thread t2 = new Thread(f2);
        t2.start();
	}

	@Override
	public void run() {
		quickSort(A, begin, end);
		
	}
	
	private void quickSort(int[]A, int begin, int end){
		if((end-begin)<=1)return;
		int pivot = A[begin];
		int pivotIndex;
		
		ArrayList<Integer> lessThan = new ArrayList<Integer>();
		ArrayList<Integer> greaterThan = new ArrayList<Integer>();
		for(int i=begin+1; i<end; i++){//O(n)
			if(A[i]<=pivot){lessThan.add(A[i]);}
			if(A[i]>pivot){greaterThan.add(A[i]);}
		}
		
		//move less than numbers before pivot and greater than numbers after pivot
		//save pivot index for recursive call
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
		quickSort(A,begin, pivotIndex);
		quickSort(A, pivotIndex+1, end);
	}
	
	
	
	

}
