package cc.eguid.cv.videorecorder.web.service;

import java.io.IOException;
import java.util.List;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;
import cc.eguid.cv.videoRecorder.entity.RecordTask;
import cc.eguid.cv.videorecorder.web.cache.RecordInfoStorage;

/**
 * 录像服务接口
 * @author eguid
 *
 */
public interface RecordService {

	RecordTask record(String src, String out) throws IOException, Exception;

	boolean stop(int id);

	List<?> list(boolean isWork);

	RecordInfo get(Integer id);

	RecordInfoStorage initCache();
	
	void init();
}
