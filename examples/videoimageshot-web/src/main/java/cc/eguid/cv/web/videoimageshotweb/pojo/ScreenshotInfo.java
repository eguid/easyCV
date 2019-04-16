package cc.eguid.cv.web.videoimageshotweb.pojo;

/**
 * 截图信息
 * 
 * @author eguid
 *
 */
public class ScreenshotInfo {
	private String src;// 截图保存位置
	private String base64;// 图片的base64编码

	public ScreenshotInfo() {
		super();
	}

	public ScreenshotInfo(String src, String base64) {
		super();
		this.src = src;
		this.base64 = base64;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	@Override
	public String toString() {
		return "ScreenshotInfo [src=" + src + ", base64=" + base64 + "]";
	}

}
