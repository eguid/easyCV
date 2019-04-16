package cc.eguid.cv.videoRecorder.manager;

import java.io.IOException;
import java.util.List;

import cc.eguid.cv.videoRecorder.entity.RecordTask;

/**
 * 录制任务管理（可以从该处获录制器，并管理这些录制器）
 * @author eguid
 *
 */
public interface TasksManager {
	
	/**
	 * 设置录像目录
	 * @param recordDir -目录位置，如果为空，则只根据out存放
	 * @return TasksManager
	 */
	TasksManager setRecordDir(String recordDir);
	
	/**
	 * 设置播放地址
	 * @param play_url
	 * @return
	 */
	TasksManager setPlayUrl(String play_url);
	
	/**
	 * 获取一个新的录制器，如果为空表示工作线程已满
	 * @param src -视频源
	 * @param out -输出地址（如果recordDir为空，以out为准）
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	RecordTask createRcorder(String src,String out) throws IOException, Exception;

	/**
	 * 根据id获取录制任务
	 * @param id
	 * @return
	 */
	RecordTask getRcorderTask(Integer id);
	
	/**
	 * 正在工作的录制任务列表
	 * @return
	 */
	List<RecordTask> list();
	
	/**
	 * 是否存在某个正在工作的任务（以src和out判断）
	 * @param task
	 * @return
	 */
	boolean exist(String src,String out);
	
	/**
	 * 停止并归还录像任务
	 * @param task
	 * @return 
	 */
	boolean stop(RecordTask task);
	
	/**
	 * 暂停
	 * @param task
	 * @return
	 */
	boolean pause(RecordTask task);
	
	/**
	 * 继续（从暂停中恢复，停止后无法继续）
	 * @param task
	 * @return
	 */
	boolean carryon(RecordTask task);
	
	/**
	 * 开始任务
	 * @param task
	 * @return 
	 */
	boolean start(RecordTask task);
	
}
