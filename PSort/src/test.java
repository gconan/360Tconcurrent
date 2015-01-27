
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] A = new int[] {1,12,5,26,7,14,3,7,2};
		int i = 0;
		while(i < A.length){
			System.out.println(A[i]);
			i++;
		}
		PSort.parallelSort(A, 0, 8);
		i = 0;
		while(i < A.length){
			System.out.println(A[i]);
			i++;
		}
	}

}
