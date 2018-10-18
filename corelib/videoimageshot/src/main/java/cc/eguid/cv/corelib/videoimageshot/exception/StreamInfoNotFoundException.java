package cc.eguid.cv.corelib.videoimageshot.exception;

/**
 * 无法检索流信息
 * @author eguid
 *
 */
public class StreamInfoNotFoundException extends RuntimeException {

	public StreamInfoNotFoundException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
