package cc.eguid.cv.videoRecorder.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import cc.eguid.cv.videoRecorder.entity.RecordInfo;
import cc.eguid.cv.videoRecorder.entity.RecordTask;
import cc.eguid.cv.videoRecorder.recorder.JavaCVRecord;
import cc.eguid.cv.videoRecorder.recorder.Recorder;
import cc.eguid.cv.videoRecorder.storage.DefaultRecordInfoStorage;
import cc.eguid.cv.videoRecorder.storage.RecordInfoStorage;

/**
 * 默认任务管理（内置对象池管理）
 * 
 * @author eguid
 *
 */
public class DefaultTasksManager implements TasksManager {
	public static final int START_STATUS = 1;
	public static final int PAUSE_STATUS = -1;
	public static final int STOP_STATUS = 2;

	protected static volatile int task_id_index = 0;// id累加

	protected volatile int maxSize = -1;// 最大任务限制，如果小于0则无限大

	private RecordInfoStorage historyStorage=null;//历史任务存储
	
	private String record_dir;//文件存储目录
	
	private String play_url;//播放地址

	public RecordInfoStorage getHistoryStorage() {
		return historyStorage;
	}

	@Override
	public TasksManager setHistoryStorage(RecordInfoStorage historyStorage) {
		this.historyStorage = historyStorage;
		return this;
	}
	
	public String getRecordDir() {
		return record_dir;
	}

	public String getPlay_url() {
		return play_url;
	}

	@Override
	public TasksManager setPlayUrl(String play_url) {
		this.play_url = play_url;
		return this;
	}

	@Override
	public TasksManager setRecordDir(String recordDir) {
		this.record_dir = recordDir;
		return this;
	}

	// 对象池操作
	// 当前任务池大小
	protected volatile int pool_size = 0;
	// 当前任务池中数量
	protected volatile int work_size = 0;
	// 当前空闲任务数量
	protected volatile int idle_size = 0;
	/** 工作任务池 */
	protected Queue<RecordTask> workpool = null;
	/** 空闲任务池 */
	protected Queue<RecordTask> idlepool = null;

	/**
	 * 初始化
	 * @param maxSize -最大工作任务大小
	 * @param historyStorage -历史任务存储
	 * @throws Exception
	 */
	public DefaultTasksManager(int maxSize, RecordInfoStorage historyStorage) throws Exception {
		super();
		if (maxSize < 1) {
			throw new Exception("maxSize不能空不能小于1");
		}
		this.maxSize = maxSize;
		this.historyStorage=(historyStorage==null?new DefaultRecordInfoStorage(maxSize):historyStorage);
		this.workpool = new ConcurrentLinkedQueue<>();
		this.idlepool = new ConcurrentLinkedQueue<>();
	}
	
	public DefaultTasksManager(int maxSize) throws Exception {
		this(maxSize,null);
	}

	@Override
	public synchronized RecordTask createRcorder(String src, String out) throws Exception {
		RecordTask task = null;
		Recorder recorder=null;
		int id = getId();
//		System.out.println("创建时，当前池数量："+pool_size+",空闲数量："+idle_size+",工作数量："+work_size);
		// 限制任务线程数量，先看有无空闲，再创建新的
		String playurl=(play_url==null?out:play_url+out);
		out =(record_dir==null?out:record_dir+out);
		if (idle_size > 0) {// 如果有空闲任务，先从空闲任务池中获取
			idle_size--;//空闲池数量减少
			task=idlepool.poll();
			task.setId(id);
			task.setOut(out);
			task.setSrc(src);
			task.setPlayurl(playurl);
			saveTaskAndinitRecorder(task);
			return task;
		}else if (pool_size <maxSize) {// 池中总数量未超出,则新建,若超出，不创建
			recorder = new JavaCVRecord(src, out);
			task = new RecordTask(id, src, out, recorder);
			task.setPlayurl(playurl);
			saveTaskAndinitRecorder(task);
			pool_size++;// 池中活动数量增加
			return task;
		}
		//池中数量已满，且空闲池以空，返回null
		// 超出限制数量，返回空
		return null;
	}
	

	@Override
	public boolean start(RecordTask task) {
		if(task!=null) {
			Recorder recorder = task.getRecorder();
			task.setCreatetime(now());// 设置开始时间
			task.setStatus(START_STATUS);// 状态设为开始
			recorder.start();
			return true;
		}
		return false;
	}

	@Override
	public boolean pause(RecordTask task) {
		if(task!=null) {
			task.setEndtime(now());
			task.setStatus(PAUSE_STATUS);// 状态设为暂停
			task.getRecorder().pause();
		}
		return false;
	}

	@Override
	public boolean carryon(RecordTask task) {
		if(task!=null) {
			task.getRecorder().carryon();
			task.setStatus(START_STATUS);// 状态设为开始
		}
		return false;
	}
	
	@Override
	public boolean stop(RecordTask task) {
		if (task!=null&&pool_size > 0 && work_size > 0 ) {
			
			task.getRecorder().stop();// 停止录制
			task.setEndtime(now());
			task.setStatus(STOP_STATUS);
			// 工作池中有没有
			if (!workpool.contains(task)) {
				return false;
			}
			// 从工作池中删除，存入空闲池
			if (idlepool.add(task)) {
				idle_size++;
				if (workpool.remove(task)) {
					work_size--;
					System.out.println("归还后，当前池数量："+pool_size+",空闲数量："+idle_size+",工作数量："+work_size);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<RecordInfo> historylist() {
		return historyStorage.list();
	}

	@Override
	public List<RecordTask> list() {
		List<RecordTask> list=new ArrayList<>();
		Iterator<RecordTask> itor=workpool.iterator();
		for(;itor.hasNext();) {
			list.add(itor.next());
		}
		return list;
	}

	@Override
	public RecordTask getRcorderTask(Integer id) {
		Iterator<RecordTask> itor=workpool.iterator();
		for(;itor.hasNext();) {
			RecordTask task=itor.next();
			if(task.getId()==id) {
				return task;
			}
		}
		return null;
	}

	@Override
	public RecordInfo getRcorderInfo(Integer id) {
		return id==null?null:historyStorage.get(id);
	}

	/*
	 * 保存任务并初始化录制器
	 * @param task
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 */
	private void saveTaskAndinitRecorder(RecordTask task) throws CloneNotSupportedException, IOException {
		Recorder recorder=task.getRecorder();
		recorder.from(task.getSrc()).to(task.getOut());//重新设置视频源和输出
		workpool.add(task);
		//由于使用的是池中的引用，所以使用克隆用于保存副本
		historyStorage.save(task.clone());
		work_size++;//工作池数量增加
	}
	
	/*
	 * 获取当前时间
	 * 
	 * @return
	 */
	private long now() {
		return System.currentTimeMillis();
	}

	/*
	 * 获取自增id
	 * @return
	 */
	private int getId() {
		return ++task_id_index;
	}

//	class WorkerThreadTimer{
//		public WorkerThreadTimer(int period){
//			Timer timer=new Timer(false);
//			timer.schedule(new TimerTask() {
//				
//				@Override
//				public void run() {
//					
//				}
//			},period, period);
//		}
//		public void run() {
//			
//		}
//	}
}
