package cc.eguid.cv.videoRecorder.entity;

import java.io.Serializable;

/**
 * 录制信息
 * 
 * @author eguid
 *
 */
public class RecordInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = 2593917989727263187L;
	private Integer id;
	private String src;// 视频源
	private String out;// 保存位置
	private String playurl;//播放地址
	private Long createtime;// 任务创建时间
	private Long endtime;// 任务结束时间

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

	public RecordInfo(Integer id, String src, String out, long createtime, long endtime) {
		super();
		this.id = id;
		this.src = src;
		this.out = out;
		this.createtime = createtime;
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

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public Long getEndtime() {
		return endtime;
	}

	public void setEndtime(Long endtime) {
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
		return "RecordInfo [id=" + id + ", src=" + src + ", out=" + out + ", playurl=" + playurl + ", createtime="
				+ createtime + ", endtime=" + endtime + "]";
	}
}
