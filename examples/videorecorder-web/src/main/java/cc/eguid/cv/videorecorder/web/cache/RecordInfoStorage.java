package cc.eguid.cv.videorecorder.web.cache;

import java.util.List;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;

public interface RecordInfoStorage {

	boolean save(RecordInfo info);

	List<RecordInfo> list();

	RecordInfo get(int id);

}
