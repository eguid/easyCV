package cc.eguid.cv.videoRecorder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 录制工作线程池，确实没啥用
 * @author eguid
 *
 */
public class WorkGroup {
	public static final int KEEPALIVETIME = 30;//默认保活时间30分钟
	public static final int DEFAULTMINSIZE=10;//默认最少保活10个工作线程
	public static final int DEFAULTMAXSIZE=10;//默认一百个工作线程
	private ThreadPoolExecutor pool = null;//工作线程池
	private BlockingQueue<Runnable> workQueue;//空闲线程队列
	
	protected int minPoolSize,maxPoolSize;//保活线程最少/最多数量
	
	public WorkGroup(int size) {
		if(size<1) {
			minPoolSize=DEFAULTMINSIZE;
			maxPoolSize=DEFAULTMAXSIZE;
		}else {
			minPoolSize=size;
			maxPoolSize=size;
		}
		workQueue=new ArrayBlockingQueue(maxPoolSize);//工作线程队列，每次执行完成一个runnable都会存放在工作队列中用于下次操作
		pool = new ThreadPoolExecutor(minPoolSize, maxPoolSize, KEEPALIVETIME, TimeUnit.MINUTES, workQueue);
	}
	
	public static void main(String[] args) {
		
	}
}
