package cc.eguid.cv.corelib.videoimageshot.threaddata;

import cc.eguid.cv.corelib.videoimageshot.grabber.ffmpeg.FFmpegVideoImageGrabber;

/**
 * 当前线程共享数据
 * @author eguid
 *
 */
@Deprecated
public class CurrentThreadData {

	public final static String DETAULT_FORMAT = "jpg";

	public final static ThreadLocal<FFmpegVideoImageGrabber> grabber = new ThreadLocal<FFmpegVideoImageGrabber>() {
		@Override
		protected FFmpegVideoImageGrabber initialValue() {
			return new FFmpegVideoImageGrabber();
		}
	};
}
