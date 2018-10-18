package cc.eguid.cv.videoRecorder.storage;

import java.util.List;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;

/**
 * 录像信息存储接口
 * @author eguid
 *
 */
public interface RecordInfoStorage {

	List<RecordInfo> list();
	
	boolean save(RecordInfo info);
	
	RecordInfo get(int id);
}
