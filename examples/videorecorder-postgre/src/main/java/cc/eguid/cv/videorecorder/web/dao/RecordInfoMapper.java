package cc.eguid.cv.videorecorder.web.dao;

import java.util.List;

import cc.eguid.cv.videorecorder.web.pojo.CameraRecordInfo;

public interface RecordInfoMapper {
	int delete(CameraRecordInfo info);

	int insert(CameraRecordInfo info);

	int update(CameraRecordInfo info);

	CameraRecordInfo selectById(Integer id);

	List<CameraRecordInfo> selectBySelective(CameraRecordInfo info);
}
