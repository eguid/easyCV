package cc.eguid.cv.videoRecorder;

/**
 * 无法检索到流
 * @author eguid
 *
 */
public class StreamNotFoundException extends RuntimeException {

	public StreamNotFoundException(String message) {
		super(message);
	}
	
	private static final long serialVersionUID = 1L;

}
