package cc.eguid.cv.videoRecorder.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 录制信息
 * 
 * @author eguid
 *
 */
public class RecordInfo implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer id;//id
	protected String src;// 视频源
	protected String out;// 保存位置
	protected String playurl;//播放地址
	protected Date starttime;// 任务创建时间
	protected Date endtime;// 任务结束时间

	public RecordInfo() {
		super();
	}

	public RecordInfo(String src, String out) {
		super();
		this.src = src;
		this.out = out;
	}

	public RecordInfo(Integer id, String src, String out) {
		super();
		this.id = id;
		this.src = src;
		this.out = out;
	}

	public RecordInfo(Integer id, String src, String out, Date starttime, Date endtime) {
		super();
		this.id = id;
		this.src = src;
		this.out = out;
		this.starttime = starttime;
		this.endtime = endtime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public Date getstarttime() {
		return starttime;
	}

	public void setstarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public String getPlayurl() {
		return playurl;
	}

	public void setPlayurl(String playurl) {
		this.playurl = playurl;
	}

	@Override
	public RecordInfo clone() throws CloneNotSupportedException {
		return (RecordInfo) super.clone();
	}

	@Override
	public String toString() {
		return "RecordInfo [id=" + id + ", src=" + src + ", out=" + out + ", playurl=" + playurl + ", starttime="
				+ starttime + ", endtime=" + endtime + "]";
	}
	
}
