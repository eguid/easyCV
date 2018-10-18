package cc.eguid.cv.videorecorder.web.service;

import org.springframework.stereotype.Service;

import cc.eguid.cv.videoRecorder.storage.RecordInfoStorage;
import cc.eguid.cv.videorecorder.web.cache.DefaultCache;

/**
 * 默认服务实现(自定义持久化实现)
 * @author eguid
 *
 */
@Service("recordService")
public class DefaultRecordServiceImpl extends RecordServiceTemplate{

	@Override
	public RecordInfoStorage initCache() {
		DefaultCache cache=new DefaultCache(maxSize);
		return cache;
	}

}
