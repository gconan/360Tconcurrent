
public class PQueue {
	
	public PQueue(int m){
		
	}

	public int insert(String name, int priority){ 
		// Inserts the name with its priority in the PQueue. // It returns -1 if the name is already present in the list. // Otherwise, returns the current position in the list where the name was inserted. // This method blocks when the list is full.
		return 0;
	}
	public int search(String name){
		// Returns the position of the name in the list. // If the name is not found it returns -1;
		
		return 0;
	}
	public String getFirst(){
		// Returns the name with the highest priority in the list. // If the list is empty, then the method blocks. // The name is deleted from the list.
		return "";
	}
	
	public class Node{
		protected String name;
		protected int priority;
		protected Node next;
		protected Node prev;
	}
	
}
