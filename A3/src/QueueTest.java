
public class QueueTest {
	
	public static void main(String [] args) {
		PQueue pq = new PQueue(5);
		System.out.println("Inser n1 at pos "+pq.insert("n1", 4));
		pq.printQueue();
		System.out.println("Inser n2 at pos "+pq.insert("n2", 2));
		System.out.println("Inser n2 at pos "+ pq.insert("n2", 7));
		pq.printQueue();
		System.out.println("Inser n3 at pos "+ pq.insert("n3", 8));
		pq.printQueue();
		System.out.println("Inser n5 at pos "+ pq.insert("n5", 8));
		pq.printQueue();
		System.out.println("Inser n4 at pos "+ pq.insert("n4", 3));
		pq.printQueue();
		System.out.println("Inser n6 at pos "+ pq.insert("n6", 4));
		pq.printQueue();
		int loc = pq.search("n4");
		System.out.println(loc);
		pq.getFirst();
		pq.printQueue();
		loc = pq.search("n4");
		System.out.println("n4 location "+loc);
		loc = pq.search("n3");
		System.out.println("n3 location "+loc);
		loc = pq.search("n1");
		System.out.println("n1 location "+loc);
		loc = pq.search("n2");
		System.out.println("n2 location "+loc);
	}
	


}
