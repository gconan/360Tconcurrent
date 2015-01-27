
public class PSort {
	public static void parallelSort(int[] A, int begin, int end) {
		//your implementation goes here.
		System.out.println("hello");
		if(begin == end){
			return;
		}
		int[] less =  new int[10000];
		int[] greater = new int[10000];
		int pivot = (begin+end)/2;
		int i = begin;
		int j = end;
		int temp;
		int l = 0;
		int g = 0;
		while(i <= j){
			while(A[i] < A[pivot]){
				i++;
			}
			while(A[j] > A[pivot]){
				j--;
			}
			if(i <= j){
				temp = A[i];
				A[i] = A[j];
				A[j] = temp;
				i++;
				j--;
			}
		}
		
		if(begin < i-1){
			parallelSort(A, begin, i-1);
		}
		if(i < end){
			parallelSort(A, i, end);
		}
		return;
	}
	
	
	
	

}
