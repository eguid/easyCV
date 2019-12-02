package cc.eguid.cv.videoRecorder.recorder;

import java.io.IOException;

import org.bytedeco.javacv.FrameGrabber.Exception;

/**
 * 录制器接口
 * 
 * @author eguid
 *
 */
public interface Recorder {

	/**
	 * 设置视频源
	 * 
	 * @param src
	 *            视频源（文件或流媒体拉流地址）
	 * @return
	 */
	Recorder from(String src) throws Exception;

	/**
	 * 设置输出文件或推流到流媒体服务
	 * 
	 * @param out
	 *            输出文件或流媒体服务推流地址
	 * @return
	 * @throws IOException
	 */
	Recorder to(String out) throws IOException;

	/**
	 * 转发源视频到输出（复制）
	 * 
	 * @param src
	 *            视频源
	 * @param out
	 *            输出文件或流媒体服务推流地址
	 * @return
	 * @throws IOException
	 */
	Recorder stream(String src, String out) throws IOException;

	/**
	 * 设置音频参数
	 * 
	 * @param audioChannels
	 * @param audioBitrate
	 * @param sampleRate
	 * @return
	 */
	Recorder audioParam(int audioChannels, int audioBitrate, int sampleRate);

	/**
	 * 设置视频参数
	 * 
	 * @param width
	 * @param height
	 * @param framerate
	 * @param bitrate
	 * @return
	 */
	Recorder videoParam(Integer width, Integer height, int framerate, int bitrate);

	/**
	 * 开始录制
	 * 
	 * @return
	 */
	Recorder start();

	/**
	 * 暂停录制
	 * 
	 * @return
	 */
	Recorder pause();

	/**
	 * 继续录制（从暂停中恢复）
	 * 
	 * @return
	 */
	Recorder carryon();

	/**
	 * 停止录制
	 * 
	 * @return
	 */
	Recorder stop();

	/**
	 * 线程是否可用
	 * @return
	 */
	boolean alive();

	/**
	 * 工作线程实时状态，用于保活（状态码：0-初始状态，1-运行，2-停止）
	 * @return
	 */
	int status();
}
