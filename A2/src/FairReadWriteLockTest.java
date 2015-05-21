import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;


public class FairReadWriteLockTest {
	public static class Readers extends Thread{
		public void run() {
			clock.beginRead();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clock.endRead();
			
		}
		
	}
	
	public static class Writers extends Thread{
			public void run() {
				clock.beginWrite();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				clock.endWrite();
				
			}
			
	}
	
	static FairReadWriteLock clock = new FairReadWriteLock();
	Thread[] threads;
	static AtomicInteger order = new AtomicInteger(0);
	public void resetOrder(){
		// System.out.println("Resetting order.");
		order.set(0);
	}
	public static class TestWriter implements Callable<Integer>{
		FairReadWriteLock lock;
		int duration;
		
		public TestWriter(FairReadWriteLock lock, int duration){
			this.lock = lock;
			this.duration = duration;
		}
		@Override
		public Integer call() throws Exception {
			lock.beginWrite();
			int tmp = order.getAndAdd(1);
			try {
				TimeUnit.SECONDS.sleep(duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lock.endWrite();
			return tmp;
		}
	}
	public static class TestReader implements Callable<Integer>{
		FairReadWriteLock lock;
		int duration;
		
		public TestReader(FairReadWriteLock lock, int duration){
			this.lock = lock;
			this.duration = duration;
		}
		@Override
		public Integer call() throws Exception {
			lock.beginRead();
			int tmp = order.getAndAdd(1);
			try {
				TimeUnit.SECONDS.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.endRead();
			return tmp;
		}
	}
	@Test
	public void singleReaderTest() throws InterruptedException, ExecutionException{
		resetOrder();
		FairReadWriteLock lock = new FairReadWriteLock();
		ExecutorService threadpool = Executors.newCachedThreadPool();
		Future<Integer> result = threadpool.submit(new TestReader(lock, 1));
		assertEquals(0, (int)result.get());
		threadpool.shutdown();
	}
	
	@Test
	public void multiReaderSingleWriterTest() throws InterruptedException, ExecutionException {
		resetOrder();
		FairReadWriteLock lock = new FairReadWriteLock();
		ExecutorService threadpool = Executors.newCachedThreadPool();
		List<Future<Integer>> readerResults = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 5; ++i){
			readerResults.add(threadpool.submit(new TestReader(lock, 1)));
		}
		Future<Integer> writerResult = threadpool.submit(new TestWriter(lock, 1));
		assertEquals(5, (int)writerResult.get());
		for (int i = 0; i < 5; ++i){
			// using sleep to ensure the correct order of submission
			readerResults.add(threadpool.submit(new TestReader(lock, i)));
			TimeUnit.SECONDS.sleep(1);
		}
		
		assertEquals(10, (int)readerResults.get(9).get());
		threadpool.shutdown();
		
	}
	@Test
	public void multiWriter() throws InterruptedException, ExecutionException {
		resetOrder();
		FairReadWriteLock lock = new FairReadWriteLock();
		ExecutorService threadpool = Executors.newCachedThreadPool();
		List<Future<Integer>> writerResults = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 5; ++i ){
			writerResults.add(threadpool.submit(new TestWriter(lock, i)));
			TimeUnit.SECONDS.sleep(1);
		}
		for (int i = 0; i < 5; ++i) {
			//writerResults.get(i).get();
			assertEquals(i, (int)writerResults.get(i).get());
		}
		threadpool.shutdown();
	}

//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//import org.junit.Before;
//import org.junit.Test;
//
//
//public class FairReadWriteLockTest {
//	static FairReadWriteLock lock = new FairReadWriteLock();
//	Thread[] threads;
//	
	
	
	@Before
	public void setUp(){
		
		threads = new Thread[10];
		for(int i=0; i<5; i++){
			threads[i] = new Readers();
		}
		for(int i=5; i<10; i++){
			threads[i] = new Writers();
		}
	}
	
	@Test(timeout=1000*41)
	public void test1(){
		try{
			for(int i=0; i<10; i++){
				threads[i].run();
				threads[(i+1)%10].run();
				threads[(int)(Math.random()*10)].run();
				threads[9-i].run();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}
	
	@Test(timeout=1000*21)
	public void test2(){
		try{
			for(int i=0; i<5; i++){
				threads[i].run();
				threads[(i+1)%10].run();
				threads[(int)(Math.random()*10)].run();
				threads[9-i].run();
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}
}