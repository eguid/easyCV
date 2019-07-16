package cc.eguid.cv.corelib.videoimageshot.grabber;

import java.io.IOException;

/**
 * 图像字节数组抓取
 * @author eguid-matebook
 *
 */
public interface BytesGrabber {
	/**
	 * 抓取图像缓冲区(确保已经设置了url参数，默认获取BGR数据)
	 * @return
	 * @throws IOException
	 */
	byte[] grabBytes() throws IOException;
	
	/**
	 * 抓取图像缓冲区（默认获取BGR数据）
	 * @param url-视频地址
	 * @return
	 * @throws IOException
	 */
	byte[] grabBytes(String url) throws IOException;

	/**
	 * 抓取图像缓冲区
	 * @param url -视频地址
	 * @param fmt -图像数据结构（默认BGR24）
	 * @return
	 * @throws IOException
	 */
	byte[] grabBytes(String url, Integer fmt) throws IOException;

	/**
	 * 连续获取图像帧
	 * @param url -视频地址
	 * @param fmt -图像数据结构（默认BGR24）
	 * @param sum -截图总次数
	 * @param interval -间隔（每隔几帧截图一次，需要自行确定视频帧率）
	 * @return
	 * @throws IOException
	 */
	byte[][] grabBytes(String url, Integer fmt, int sum, int interval) throws IOException;
}
