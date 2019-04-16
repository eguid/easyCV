package cc.eguid.cv.videorecorder.web.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;
import cc.eguid.cv.videoRecorder.entity.RecordTask;
import cc.eguid.cv.videoRecorder.manager.DefaultTasksManager;
import cc.eguid.cv.videoRecorder.manager.TasksManager;
import cc.eguid.cv.videorecorder.web.cache.RecordInfoStorage;

/**
 * 录像服务模板
 * @author eguid
 *
 */
public abstract class RecordServiceTemplate implements RecordService{
	
	@Value("${record.maxsize}")
	protected Integer maxSize;
	
	@Value("${record.dir}")
	protected String dir;
	
	@Value("${play.url}")
	protected String playurl;
	
	protected TasksManager manager = null;
	
	static RecordInfoStorage cache;
	
	public static volatile int status = 0;
	
	@Override
	public synchronized void init() {
		try {
			if(manager==null) {
				cache=initCache();//持久化实现
				manager = new DefaultTasksManager(maxSize).setPlayUrl(playurl).setRecordDir(dir);
				status = 1;
			}
		} catch (Exception e) {
			status = -1;
		}
	}
	
	/**
	 * 开始录像
	 * @param src
	 * @param out
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	@Override
	public RecordTask record(String src,String out) throws IOException, Exception {
		init();
		RecordTask task=manager.createRcorder(src, out);
		if(task!=null) {
			manager.start(task);
		}	
		return task;
	}
	/**
	 * 停止录像
	 * @param id
	 * @return
	 */
	@Override
	public boolean stop(int id) {
		init();
		RecordTask task=manager.getRcorderTask(id);
		return manager.stop(task);
	}
	
	/**
	 * 获取列表
	 * @param isWork -是否工作，true:工作列表，false-历史列表
	 * @return
	 */
	@Override
	public List<?> list(boolean isWork) {
		init();
		List<?> list=isWork?manager.list():cache.list();
		return list;
	}
	

	@Override
	public RecordInfo get(Integer id) {
		RecordInfo info=  cache.get(id);
		return info;
	}


	public int getMaxSize() {
		return maxSize;
	}

	public static int getStatus() {
		return status;
	}

}
