package cc.eguid.cv.web.videoimageshotweb.pojo;

import java.util.Date;

public class VideoshotInfo extends BaseEntity{
	private static final long serialVersionUID = 1L;

	private String id;

    private String src;

    private String out;

    private String url;

    private String fmt;

    private Integer width;

    private Integer height;

    private Date createtime;

    private Integer cameraid;

    public VideoshotInfo() {
		super();
	}

	public VideoshotInfo(String id, String src, String out, String url, String fmt, Integer cameraid) {
		super();
		this.id = id;
		this.src = src;
		this.out = out;
		this.url = url;
		this.fmt = fmt;
		this.cameraid = cameraid;
	}


	public VideoshotInfo(String id, String src, String out, String url, String fmt, Integer width, Integer height,
			Integer cameraid) {
		super();
		this.id = id;
		this.src = src;
		this.out = out;
		this.url = url;
		this.fmt = fmt;
		this.width = width;
		this.height = height;
		this.cameraid = cameraid;
	}


	public VideoshotInfo(String id, String src, String out, String url, String fmt, Integer width, Integer height,
			Date createtime, Integer cameraid) {
		super();
		this.id = id;
		this.src = src;
		this.out = out;
		this.url = url;
		this.fmt = fmt;
		this.width = width;
		this.height = height;
		this.createtime = createtime;
		this.cameraid = cameraid;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src == null ? null : src.trim();
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out == null ? null : out.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt == null ? null : fmt.trim();
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

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getCameraid() {
        return cameraid;
    }

    public void setCameraid(Integer cameraid) {
        this.cameraid = cameraid;
    }
}