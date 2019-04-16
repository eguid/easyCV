package cc.eguid.cv.web.videoimageshotweb.pojo;

/**
 * 截图信息
 * 
 * @author eguid
 *
 */
public class ScreenshotInfo extends BaseEntity{
	private String imgfile;// 截图保存位置
	private String base64;// 图片的base64编码

	public ScreenshotInfo() {
		super();
	}

	public ScreenshotInfo(String imgfile, String base64) {
		super();
		this.imgfile = imgfile;
		this.base64 = base64;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public String getImgfile() {
		return imgfile;
	}

	public void setImgfile(String imgfile) {
		this.imgfile = imgfile;
	}

}
