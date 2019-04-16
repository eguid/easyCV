package cc.eguid.cv.videorecorder.web.service;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import cc.eguid.cv.videoRecorder.entity.RecordTask;
import cc.eguid.cv.videoRecorder.manager.DefaultTasksManager;
import cc.eguid.cv.videoRecorder.manager.TasksManager;
import cc.eguid.cv.videorecorder.web.pojo.CameraRecordInfo;

/**
 * 录像服务模板
 * @author eguid
 *
 */
public abstract class RecordServiceTemplate implements RecordService<CameraRecordInfo>{
	
	Logger log =LoggerFactory.getLogger(this.getClass());
	
	@Value("${record.maxsize}")
	protected Integer maxSize;
	
	@Value("${record.dir}")
	protected String dir;
	
	@Value("${play.url}")
	protected String playurl;
	
	protected static TasksManager manager = null;
	
	public static volatile int status = 0;
	
	
	@Override
	public synchronized void init() {
		if(status<1) {
			try {
				if(manager==null) {
					manager = new DefaultTasksManager(maxSize).setPlayUrl(playurl).setRecordDir(dir);
					status = 1;
				}
			} catch (Exception e) {
				status = -1;
			}
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
	public CameraRecordInfo record(String src,String out) throws IOException, Exception {
		init();
		RecordTask task=manager.createRcorder(src, out);
		CameraRecordInfo info=null;
		if(task!=null) {
			if(manager.start(task)) {
				log.info("已经正常开启录像任务，录像任务详情："+task);
				info =new CameraRecordInfo();
				info.setId(task.getId());
				info.setSrc(task.getSrc());
				info.setOut(task.getOut());
				info.setPlayurl(task.getPlayurl());
				info.setstarttime(now());
				if(save(info)) {
					log.info("保存数据成功，数据详情："+info);
					return info;
				}
				log.error("持久化数据失败");
			}
		}	
		return info;
	}


	@Override
	public CameraRecordInfo record(Integer cameraid, String src, String out) throws IOException, Exception {
		init();
		RecordTask task=manager.createRcorder(src, out);
		CameraRecordInfo info=null;
		if(task!=null) {
			if(manager.start(task)) {
				log.info("已经正常开启录像任务，录像任务详情："+task);
				info =new CameraRecordInfo();
				info.setId(task.getId());
				info.setSrc(task.getSrc());
				info.setOut(out);
				info.setPlayurl(task.getPlayurl());
				info.setstarttime(task.getstarttime());
				info.setCameraid(cameraid);
				if(save(info)) {
					log.info("保存数据成功，数据详情："+info);
					return info;
				}
				log.error("持久化数据失败");
			}
		}	
		return info;
	}
	
	protected final Date now() {
		return new Date();
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
		CameraRecordInfo info=null;
		if(task!=null) {
			if(manager.stop(task)) {
				info =new CameraRecordInfo();
				info.setId(task.getId());
				info.setSrc(task.getSrc());
				info.setOut(task.getOut());
				info.setPlayurl(task.getPlayurl());
				info.setstarttime(task.getstarttime());
				info.setEndtime(now());
				if(save(info)) {
					return true;
				}
			}
		}
		return false;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public static int getStatus() {
		return status;
	}
	

	@Override
	public boolean pause(int id) {
		init();
		RecordTask task=manager.getRcorderTask(id);
		if(task!=null) {
			return manager.pause(task);
		}
		return false;
	}

	@Override
	public boolean carryon(int id) {
		init();
		RecordTask task=manager.getRcorderTask(id);
		if(task!=null) {
			return manager.carryon(task);
		}
		return false;
	}
	
	@Override
	public boolean existWorking(String src,String out) {
		init();
		log.info("是否存在正在工作的录像任务："+src+","+out);
		return manager.exist(src, out);
	}

}
