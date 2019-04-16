package cc.eguid.cv.web.videoimageshotweb.pojo;

/**
 * 截图参数
 * 
 * @author eguid
 *
 */
public class ShotParam {
	private String name;// 截图名称（可以包含后缀名，或者摄像机ID）（必须）
	private String src;// 视频地址（必须）
	private String fmt;// 图片格式（可选，默认jpg，图片后缀名）
	private String output;// 保存位置（可选）
	private Integer width;// 宽度（可选）
	private Integer height;// 高度（可选）
	private Integer needBase64;// 是否需要base64编码，0或空只要文件，不要base64编码（默认），1-只返回base64编码，2-base64和文件都需要

	public ShotParam() {
		super();
	}
	
	public ShotParam(String src, String name, String output) {
		super();
		this.src = src;
		this.output = output;
	}

	public ShotParam(String src, String name, String fmt, String output) {
		super();
		this.src = src;
		this.fmt = fmt;
		this.output = output;
	}

	public ShotParam(String src, String name, String fmt, String output, Integer width, Integer height) {
		super();
		this.src = src;
		this.fmt = fmt;
		this.output = output;
		this.width = width;
		this.height = height;
	}

	

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getFmt() {
		return fmt;
	}

	public void setFmt(String fmt) {
		this.fmt = fmt;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getNeedBase64() {
		return needBase64;
	}

	public void setNeedBase64(Integer needBase64) {
		this.needBase64 = needBase64;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ShotParam [name=" + name + ", src=" + src + ", fmt=" + fmt + ", output=" + output + ", width=" + width
				+ ", height=" + height + ", needBase64=" + needBase64 + "]";
	}

}
