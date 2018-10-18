package cc.eguid.cv.web.videoimageshotweb.pojo;

public class ErrorMsg {
	private String msg;
	private String code;

	public ErrorMsg() {
		super();
	}

	public ErrorMsg(String msg) {
		super();
		this.msg = msg;
	}


	public ErrorMsg(String msg, String code) {
		super();
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ErrorMsg [msg=" + msg + ", code=" + code + "]";
	}

}
