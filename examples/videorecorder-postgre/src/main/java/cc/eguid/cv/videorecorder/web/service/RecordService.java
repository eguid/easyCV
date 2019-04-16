package cc.eguid.cv.videorecorder.web.service;

import java.io.IOException;
import java.util.List;

import cc.eguid.cv.videorecorder.web.pojo.CameraRecordInfo;

/**
 * 录像服务接口
 * @author eguid
 *
 */
public interface RecordService<T> {

	/**
	 * 录像
	 * @param src
	 * @param out
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	T record(String src, String out) throws IOException, Exception;
	
	/**
	 * 录像
	 * @param src
	 * @param out
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	T record(Integer cameraid,String src, String out) throws IOException, Exception;

	/**
	 * 停止录像
	 * @param id
	 * @return
	 */
	boolean stop(int id);
	
	/**
	 * 暂停录像
	 * @param id
	 * @return
	 */
	boolean pause(int id);
	
	/**
	 * 恢复录像（从暂停中恢复）
	 * @param id
	 * @return
	 */
	boolean carryon(int id);

	void init();

	List<T> list(CameraRecordInfo info);

	T get(Integer id);
	
	/**
	 * 保存信息
	 * @param task
	 * @return
	 */
	boolean save(T info);

	/**
	 * 根据视频地址获取正在工作的录像任务信息
	 * @param src
	 * @return
	 */
	CameraRecordInfo getWork(CameraRecordInfo info);

	/**
	 * 是否存在某个录像任务
	 * @param info
	 * @return
	 */
	boolean exist(CameraRecordInfo info);
	
	/**
	 * 是否存在正在录像的任务
	 * @param info
	 * @return
	 */
	boolean existWorking(String src,String out);
}
