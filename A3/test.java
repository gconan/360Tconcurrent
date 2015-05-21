
/** Import ImplicitMonitor Library **/
import autosynch.*;

public class pqam {        
        /** Create Monitor Object. **/
        private final AbstractImplicitMonitor monitor_1482111392 = 
            new NaiveImplicitMonitor();

	private int maxSize;
	private int size;
	private Node head;
	
	public pqam(int m){
		maxSize = m;
		size = 0;
		head = null;
	}

	public int insert(String name, int priority){
                /* monitor */
                monitor_1482111392.enter();
                try {
 
		// Inserts the name with its priority in the PQueue. // It returns -1 if the name is already present in the list. // Otherwise, returns the current position in the list where the name was inserted. // This method blocks when the list is full.
		Node node = new Node(name, priority);
		int i = 0;
		if(search(name) != -1){
                        {
                          int ret_1892124631 =  -1;

                          return ret_1892124631;
                        }

		}
                if (!(size < maxSize)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_1000828441 = monitor_1482111392.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (size < maxSize);
                      }
                    }
                  );
                  condition_1000828441.await();
                }

		if(size == 0){
			head = node;
			node.next = null;
			size++;
                        {
                          int ret_944706736 =  0;

                          return ret_944706736;
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
                          int ret_1940009821 =  i;

                          return ret_1940009821;
                        }

		}
                } finally {

                /* leave monitor */
                monitor_1482111392.leave();

                }

	}
	public int search(String name){
                /* monitor */
                monitor_1482111392.enter();
                try {

                if (!(this.size<this.maxSize)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_1783184229 = monitor_1482111392.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (this.size<this.maxSize);
                      }
                    }
                  );
                  condition_1783184229.await();
                }

		Node temp = this.head;
		for(int i=0; i<this.size; i++){
			if(temp == null){
                                {
                                  int ret_1904059746 =  -1;

                                  return ret_1904059746;
                                }

			}
			else if(temp.name.equals(name)){
                                {
                                  int ret_1033538625 =  i;

                                  return ret_1033538625;
                                }

			}else{
				temp = temp.next;
			}
		}
                {
                  int ret_1488269177 =  -1;

                  return ret_1488269177;
                }

                } finally {

                /* leave monitor */
                monitor_1482111392.leave();

                }

	}
	public String getFirst(){
                /* monitor */
                monitor_1482111392.enter();
                try {

                if (!(head != null)) {
                  /* Create Condition Variable*/
                  AbstractCondition condition_1512323228 = monitor_1482111392.makeCondition(
                    new Assertion() {
                      public boolean isTrue() {
                        return (head != null);
                      }
                    }
                  );
                  condition_1512323228.await();
                }

		String result = head.name;
		deleteHead();
                {
                  String ret_47104 =  result;

                  return ret_47104;
                }

                } finally {

                /* leave monitor */
                monitor_1482111392.leave();

                }

	}
	
	private void deleteHead() {
                /* monitor */
                monitor_1482111392.enter();
                try {

		this.head = this.head.next;
                } finally {

                /* leave monitor */
                monitor_1482111392.leave();

                }

	}
	
	public void printQueue() {
                /* monitor */
                monitor_1482111392.enter();
                try {

		Node nextNode = head;
		while(nextNode != null) {
			System.out.println("Node Name: "+nextNode.name + " Priority: "+ nextNode.priority);
			nextNode = nextNode.next;
		}
		System.out.println();
                } finally {

                /* leave monitor */
                monitor_1482111392.leave();

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
	
}