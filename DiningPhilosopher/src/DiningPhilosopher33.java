import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.Random;

public class DiningPhilosopher33 {
	private static Random rand = new Random();
	private static Lock chop1 = new ReentrantLock();
	private static Lock chop2 = new ReentrantLock();
	private static Lock chop3 = new ReentrantLock();
	private static Lock chop4 = new ReentrantLock();
	private static Lock chop5 = new ReentrantLock();
	private static Lock priority = new ReentrantLock();
	
	public static void main(String[] args){
		ExecutorService executor = Executors.newCachedThreadPool();
		
		executor.execute(new ThinkandEat(chop1, chop2, "Philo12"));
		executor.execute(new ThinkandEat(chop2, chop3, "Philo23"));
		executor.execute(new ThinkandEat(chop3, chop4, "Philo34"));
		executor.execute(new ThinkandEat(chop4, chop5, "Philo45"));
		executor.execute(new ThinkandEat(chop5, chop1, "Philo51"));
		
		executor.shutdown();
	}
		
	public static class ThinkandEat implements Runnable{
		private Lock left, right;
		private String name;
		
		public ThinkandEat(Lock left, Lock right, String name){
			this.left = left;
			this.right = right;
			this.name = name;
		}
		
		public void run(){
			int priority_count = 0;
			while(true){
				System.out.println(name + " is thinking");
				try{
					Thread.sleep(rand.nextInt(2000));
				}catch(InterruptedException ex){}
				
				System.out.println(name + " is attempting to eat");
				
				left.lock();
				System.out.println(name +" grabbed left chopstick");
				//Added delay to show the deadlock that can occur with this specific implementation
				try{Thread.sleep(2000);
				}catch(InterruptedException ex){
				}
				
				//Preemption
				if(!right.tryLock()){
					//Give priority to those who have waited for 5 iterations without eating
					if(priority_count == 5){
						System.out.println(name + " was given priority");
						priority.lock();
						right.lock();
					}else{
						try{
							System.out.println(name + " couldn't grab right chopstick.");
							System.out.println(name + " dropping left chopstick and restarting");
							left.unlock();
							priority_count++;
							Thread.sleep(rand.nextInt(4000));
							continue;
						}catch(InterruptedException ex){
							continue;
						}
					}
				}
				priority_count = 0;
				System.out.println(name +" grabbed right chopstick");
				
				try{
					Thread.sleep(rand.nextInt(2000));
				}catch(InterruptedException ex){
				}finally {
					left.unlock();
					System.out.println(name +" released left chopstick");
					
					right.unlock();
					System.out.println(name +" released right chopstick");
					
					try{
						priority.unlock();
					}catch(IllegalMonitorStateException ex){
					}
					
				}
			}
		}
	}
}