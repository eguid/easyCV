package cc.eguid.cv.videorecorder.web.pojo;

public class ErrorMsg {
	/**成功状态码 */
	public static final String SUCCESSCODE="1";
	/**失败状态码 */
	public static final String ERRORCODE="0";
	
	String msg;
	String code;

	public ErrorMsg() {
		this(null);
	}

	public ErrorMsg(String msg) {
		this(msg,ERRORCODE);
	}

	public ErrorMsg(String msg, String code) {
		super();
		this.msg = msg;
		this.code = code;
	}
	
	public static final ErrorMsg createErrorMsg(String msg) {
		return new ErrorMsg(msg);
	}
	
	public static final ErrorMsg createErrorMsg(String msg,String code) {
		return new ErrorMsg(msg,code);
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
