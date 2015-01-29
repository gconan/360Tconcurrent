
public class test {
	
	
	public static void main(String[]args){
//		int[] A = {5,8,3,2,7,45,56,67,12,2,32};
//		print(A);
//		parallelSort(A,0, A.length);
//		print(A);
		int[] b = {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
		PSort.parallelSort(b,0, b.length);
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

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		int[] A = new int[] {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
//		int i = 0;
//		while(i < A.length){
//			System.out.println(A[i]);
//			i++;
//		}
//		PSort.parallelSort(A, 0, A.length);
//		i = 0;
//		while(i < A.length){
//			System.out.println(A[i]);
//			i++;
//		}
//	}

}
