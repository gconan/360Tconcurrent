
/** Import ImplicitMonitor Library **/
import autosynch.*;

public class PQueue {
/** Create Monitor Object. **/
private final AbstractImplicitMonitor monitor_1183343791 = 
    new NaiveImplicitMonitor();

private int maxSize;
	private int size;
	private Node head;
	
	public PQueue(int m){
		maxSize = m;
		size = 0;
		head = null;
	}

	public int insert(String name, int priority){
                /* monitor */
                monitor_1183343791.enter();
                try {
 
		// Inserts the name with its priority in the PQueue. // It returns -1 if the name is already present in the list. // Otherwise, returns the current position in the list where the name was inserted. // This method blocks when the list is full.
		Node node = new Node(name, priority);
		int i = 0;
		if(search(name) != -1){
                        {
                          int ret_1000828441 =  -1;

                          return ret_1000828441;
                        }

		}
                if (!(size < maxSize)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_1831684583 = monitor_1183343791.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (size < maxSize);
                      }
                    }
                  );
                  condition_1831684583.await();
                }

		if(size == 0){
			head = node;
			node.next = null;
			size++;
                        {
                          int ret_1339509681 =  0;

                          return ret_1339509681;
                        }

		} else{
			Node next = head;
			Node prev = head;
			while(next!=null && next.priority >= priority){
				prev = next;
				next = next.next;
				i++;
			}
			if(i == 0){
				node.next = head;
				head = node;
				
			} else{
				prev.next = node;
				node.next = next;
			}
			
			
			size++;
                        {
                          int ret_500849146 =  i;

                          return ret_500849146;
                        }

		}
                } finally {

                /* leave monitor */
                monitor_1183343791.leave();

                }

	}
	public int search(String name){
                /* monitor */
                monitor_1183343791.enter();
                try {

                if (!(this.size<this.maxSize)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_1843042918 = monitor_1183343791.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (size<maxSize);
                      }
                    }
                  );
                  condition_1843042918.await();
                }

		Node temp = this.head;
		for(int i=0; i<this.size; i++){
			if(temp == null){
                                {
                                  int ret_1878327075 =  -1;

                                  return ret_1878327075;
                                }

			}
			else if(temp.name.equals(name)){
                                {
                                  int ret_1835933439 =  i;

                                  return ret_1835933439;
                                }

			}else{
				temp = temp.next;
			}
		}
                {
                  int ret_1598021230 =  -1;

                  return ret_1598021230;
                }

                } finally {

                /* leave monitor */
                monitor_1183343791.leave();

                }

	}

	public String getFirst(){
                /* monitor */
                monitor_1183343791.enter();
                try {

		// Returns the name with the highest priority in the list. 
		// If the list is empty, then the method blocks. 
		// The name is deleted from the list.
		//waituntil(head != null);
		String result = head.name;
		deleteHead();
                {
                  String ret_47104 =  result;

                  return ret_47104;
                }

                } finally {

                /* leave monitor */
                monitor_1183343791.leave();

                }

	}
	
	private void deleteHead() {
                /* monitor */
                monitor_1183343791.enter();
                try {

		this.head = this.head.next;
                } finally {

                /* leave monitor */
                monitor_1183343791.leave();

                }

	}

	
	public class Node{
		protected String name;
		protected int priority;
		protected Node next;
		
		public Node(String n, int pri){
			this.name = n;
			this.priority = pri;
			this.next = null;
		}
	}

	public void printQueue() {
		Node nextNode = head;
		int i=0;
		while(nextNode != null) {
			System.out.println("Node Name: "+nextNode.name + " Priority: "+ nextNode.priority + " Index "+ i++);
			nextNode = nextNode.next;
		}
		System.out.println();
	}
	
}
