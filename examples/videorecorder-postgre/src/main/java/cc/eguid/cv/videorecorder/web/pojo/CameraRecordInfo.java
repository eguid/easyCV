package cc.eguid.cv.videorecorder.web.pojo;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;

/**
 * 摄像机录像信息
 * @author eguid
 *
 */
public class CameraRecordInfo extends RecordInfo {

	private static final long serialVersionUID = 1L;

	protected Integer cameraid;

	public CameraRecordInfo() {
		super();
	}

	public Integer getCameraid() {
		return cameraid;
	}

	public void setCameraid(Integer cameraid) {
		this.cameraid = cameraid;
	}

	@Override
	public String toString() {
		return "CameraRecordInfo [cameraid=" + cameraid + ", id=" + id + ", src=" + src + ", out=" + out + ", playurl="
				+ playurl + ", createtime=" + starttime + ", endtime=" + endtime + "]";
	}
	
	
	
}