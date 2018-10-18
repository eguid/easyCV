package cc.eguid.cv.videoRecorder.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;

/**
 * 默认存储实现（根据实际情况实现RecordInfoStorage接口）
 * @author eguid
 *
 */
public class DefaultRecordInfoStorage implements RecordInfoStorage {
	protected List<RecordInfo> historylist = null;

	public DefaultRecordInfoStorage(int maxSize) {
		historylist = new ArrayList<>(maxSize);
	}

	@Override
	public List<RecordInfo> list() {
		return historylist;
	}

	@Override
	public synchronized boolean save(RecordInfo info) {
		return info==null?false:historylist.add(info);
	}

	@Override
	public synchronized RecordInfo get(int id) {
		RecordInfo info = new RecordInfo(id, null, null);
		int ret = Collections.binarySearch(historylist, info, new Comparator<RecordInfo>() {
			@Override
			public int compare(RecordInfo o1, RecordInfo o2) {
				if (o1.getId() == o2.getId()) {
					return 0;
				}
				return -1;
			}
		});
		if (ret >= 0) {
			return historylist.get(ret);
		}
		return null;
	}

}
