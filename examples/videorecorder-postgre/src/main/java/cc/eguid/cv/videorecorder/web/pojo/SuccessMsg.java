package cc.eguid.cv.videorecorder.web.pojo;

public class SuccessMsg extends ErrorMsg{
	
	private Object data;

	public SuccessMsg() {
		this(null);
	}

	public SuccessMsg(String msg) {
		super(msg,SUCCESSCODE);
	}
	
	public SuccessMsg(String msg, Object data) {
		super(msg,SUCCESSCODE);
		this.data = data;
	}
	
	public SuccessMsg(String msg, String code, Object data) {
		super(msg, code);
		this.data = data;
	}

	public static final SuccessMsg createSuccessMsg(String msg) {
		return new SuccessMsg (msg, null);
	}
	
	public static final SuccessMsg createSuccessMsg(String msg,Object data) {
		return new SuccessMsg (msg, data);
	}
	
	public static final SuccessMsg createSuccessMsg(String msg, String code, Object data) {
		return new SuccessMsg (msg, code, data);
	}
	
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SuccessMsg [data=" + data + ", msg=" + msg + ", code=" + code + "]";
	}
	
}
