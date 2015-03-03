
public monitor class PQueue {
	private int maxSize;
	private int size;
	private Node head;
	
	public PQueue(int m){
		maxSize = m;
		size = 0;
		head = null;
	}

	public int insert(String name, int priority){ 
		// Inserts the name with its priority in the PQueue. // It returns -1 if the name is already present in the list. // Otherwise, returns the current position in the list where the name was inserted. // This method blocks when the list is full.
		Node node = new Node(name, priority);
		int i = 0;
		if(search(name) == -1){
			return -1;
		} 
		waituntil(size < maxSize);
		if(size == 0){
			head = node;
			size++;
			return 0;
		} else{
			Node next = head;
			Node prev = head;
			while(next!=null && next.priority >= priority){
				prev = next;
				next = next.next;
				i++;
			}
			prev.next = node;
			node.next = next;
			size++;
			return i;
		}
	}
	public int search(String name){
		// Returns the position of the name in the list. 
		// If the name is not found it returns -1;
		
		Node temp = this.head;
		for(int i=0; i<this.size; i++){
			if(temp == null){
				return -1;
			}
			else if(temp.name.equals(name)){
				return i;
			}else{
				temp = temp.next;
			}
		}
		return -1;
	}
	public String getFirst(){
		// Returns the name with the highest priority in the list. 
		// If the list is empty, then the method blocks. 
		// The name is deleted from the list.
		waituntil(head != null);
		String result = head.name;
		deleteHead();
		return result;
	}
	
	private void deleteHead() {
		this.head = this.head.next;
		this.head.prev = null;
	}
	
	public class Node{
		protected String name;
		protected int priority;
		protected Node next;
		protected Node prev;
	}
	
}
