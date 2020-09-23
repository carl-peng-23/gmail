# gmail
电商项目
import java.util.*;
public class SpinLockDemo 
{
	public static void main(String[] args) 
	{
		final HH h = new HH();
		new Thread(
			new Runnable() {
				public void run() {
					h.myLock();
					// 获取锁之后做五秒钟的事情
					try
					{
						Thread.sleep(5000);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		).start();
		new Thread(
			new Runnable() {
				public void run() {
					h.myLock();
					// 获取锁之后做五秒钟的事情
					try
					{
						Thread.sleep(5000);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		).start();
	}
}

class HH
{
	private AtomicReference<Thread> atomicReference = new AtomicReference<>();
	public void myLock() {
		Thread thread = Thread.currentThread();
		System.out.println(Thread.currentThread().getName() + "尝试获取锁");
		while (!atomicReference.compareAndSet(null,thread))
		{
			// 一直在尝试获取锁
		}
		System.out.println(Thread.currentThread().getName() + "获取锁成功");
		// 解锁
		myUnLock();
	}
	public void myUnLock() {
		atomicReference.compareAndSet(Thread.currentThread(),null);
		System.out.println(Thread.currentThread().getName() + "解锁");
	}
}
