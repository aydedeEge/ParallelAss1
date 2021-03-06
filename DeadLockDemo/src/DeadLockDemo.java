import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class DeadLockDemo {
	private static User1 user1 = new User1();
	private static User2 user2 = new User2();
	private static Lock lock1 = new ReentrantLock();
	private static Lock lock2 = new ReentrantLock();

	public static void main(String[] args) {
		user1.start();
		user2.start();
	}
	
	private static class User1 extends Thread{
		public void getLock1ThenLock2(){
			lock1.lock();
			
			System.out.println("User1 acquired lock1");
			try{
				Thread.sleep(5000);
				lock2.lock();
				System.out.println("User1 acquired lock2");
				try{
					Thread.sleep(5000);
				}catch(InterruptedException ex){
				}finally{
					lock2.unlock();
					System.out.println("User1 released lock2");
				}
				
			}catch (InterruptedException ex) {
			}finally{
				lock1.unlock();
				System.out.println("User1 released lock1");
			}
		}
		
		public void run() {
			this.getLock1ThenLock2();
		}
	}
	
	private static class User2 extends Thread{
		public void getLock2ThenLock1(){
			lock2.lock();
			
			System.out.println("User2 acquired lock2");
			try{
				Thread.sleep(5000);
				lock1.lock();
				System.out.println("User2 acquired lock1");
				try{
					Thread.sleep(5000);
				}catch(InterruptedException ex){
				}finally{
					lock1.unlock();
					System.out.println("User2 released lock1");
				}
				
			}catch (InterruptedException ex) {
			}finally{
				lock2.unlock();
				System.out.println("User2 released lock2");
			}
		}
		
		public void run() {
			this.getLock2ThenLock1();
		}
	}
}
