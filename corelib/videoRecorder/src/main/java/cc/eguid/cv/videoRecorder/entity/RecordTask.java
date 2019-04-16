package cc.eguid.cv.videoRecorder.entity;

import java.util.Date;

import cc.eguid.cv.videoRecorder.recorder.Recorder;

/**
 * 录制任务
 * 
 * @author eguid
 *
 */
public class RecordTask extends RecordInfo{

	private static final long serialVersionUID = -8883251081940691664L;
	private Recorder recorder;//录制器
	private int status;//录制器状态

	public RecordTask(Integer id, String src, String out) {
		super(id, src, out);
	}

	public RecordTask(Integer id, String src, String out, Recorder recorder) {
		super(id, src, out);
		this.recorder = recorder;
	}

	public RecordTask(Integer id, String src, String out, Date createtime, Date endtime) {
		super(id, src, out, createtime, endtime);
	}

	public RecordTask(Integer id, String src, String out,Date createtime, Date endtime, Recorder recorder) {
		super(id, src, out, createtime, endtime);
	}
	
	public Recorder getRecorder() {
		return recorder;
	}

	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RecordTask [recorder=" + recorder + ", status=" + status + ", toString()=" + super.toString() + "]";
	}
}
