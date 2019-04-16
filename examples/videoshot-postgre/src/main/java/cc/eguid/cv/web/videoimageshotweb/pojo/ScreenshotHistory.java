package cc.eguid.cv.web.videoimageshotweb.pojo;

import java.util.Date;

/**
 * 截图历史记录
 * @author eguid
 *
 */
public class ScreenshotHistory extends ShotParam {
	private String id;// id=名称+时间
	private String base64;// 图片的base64编码
	private Date shottime;// 截图时间

	public ScreenshotHistory() {
		super();
	}

	public ScreenshotHistory(String id, String src, String name, String output, String base64, Date shottime) {
		super(src, name, output);
		this.id = id;
		this.base64 = base64;
		this.shottime = shottime;
	}

	public ScreenshotHistory(String id, String src, String name, String fmt, String output, String base64,Date shottime) {
		super(src, name, fmt, output);
		this.id = id;
		this.base64 = base64;
		this.shottime = shottime;
	}

	public ScreenshotHistory(ShotParam param) {
		super(param.getSrc(), param.getName(), param.getFmt(), param.getOutput(), param.getWidth(), param.getHeight());
		super.setNeedBase64(param.getNeedBase64());
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public Date getShottime() {
		return shottime;
	}

	public void setShottime(Date shottime) {
		this.shottime = shottime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ScreenshotHistory [id=" + id + ", base64=" + base64 + ", shottime=" + shottime + ", getSrc()="
				+ getSrc() + ", getFmt()=" + getFmt() + ", getOutput()=" + getOutput() + ", getWidth()=" + getWidth()
				+ ", getHeight()=" + getHeight() + ", getNeedBase64()=" + getNeedBase64() + ", getName()=" + getName()
				+ ", toString()=" + super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ "]";
	}

}
