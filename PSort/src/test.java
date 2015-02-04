import java.util.Random;


public class test {
	
	
	public static void main(String[]args){
		
		int[] a = {1,12,5,26,7,14,3,7,2,22,124,21342,2153125,53262,24,346,78,421,93,225,1,6,24};
		System.out.println("Psort");
		print(a);
		PSort.parallelSort(a, 0, a.length);
		print(a);
		for(int i=0; i<a.length-1; i++){
			if(a[i]>a[i+1]){
				System.out.println("not sorted");
				return;
			}
		}
		System.out.println("sorted");
		
		System.out.println(PSearch.parallelSearch(-2, a, Runtime.getRuntime().availableProcessors()));
		
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
