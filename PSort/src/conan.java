import java.util.ArrayList;


public class conan {
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
		parallelSort(A,begin, pivotIndex);
		parallelSort(A, pivotIndex+1, end);
		
	}
	
	public static void main(String[]args){
//		int[] A = {5,8,3,2,7,45,56,67,12,2,32};
//		print(A);
//		parallelSort(A,0, A.length);
//		print(A);
		int[] b = {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
		print(b);
		parallelSort(b,0, b.length);
		print(b);
	}

	private static void print(int[] a) {
		System.out.print("{");
		System.out.print(a[0]);
		for(int i=1; i<a.length; i++){
			System.out.print(", "+a[i]);
		}
		System.out.print("}\n");
		
	}
}
