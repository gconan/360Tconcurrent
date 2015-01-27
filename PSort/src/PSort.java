
public class PSort {
	public static void parallelSort(int[] A, int begin, int end) {
		//your implementation goes here.
		System.out.println("hello");
		int last = end-1;
		int pivot = (begin+(last))/2;
		int i = begin;
		int j = last;
		int temp;
		if(last - begin == 0){
			return;
		}
		while(i <= j){
			while(A[i] <= A[pivot]){
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
		
		if(begin < i - 1){
			parallelSort(A, begin, i);
		}
		if(i < end){
			parallelSort(A, i, last + 1);
		}
		return;
	}
	
	
	
	

}
