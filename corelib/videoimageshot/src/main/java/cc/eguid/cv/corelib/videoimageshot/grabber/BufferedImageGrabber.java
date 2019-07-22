package cc.eguid.cv.corelib.videoimageshot.grabber;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface BufferedImageGrabber{
	/**
	 * 抓取图像(确保已经设置了url参数，默认获取BGR数据)
	 * @return
	 * @throws IOException
	 */
	BufferedImage grabBufferImage() throws IOException;
	
	/**
	 * 抓取图像（默认获取BGR数据）
	 * @param url-视频地址
	 * @return
	 * @throws IOException
	 */
	BufferedImage grabBufferImage(String url) throws IOException;

	/**
	 * 抓取图像
	 * @param url -视频地址
	 * @param fmt -图像数据结构（默认BGR24）
	 * @return
	 * @throws IOException
	 */
	BufferedImage grabBufferImage(String url, Integer fmt) throws IOException;
	
	/**
	 * 连续抓取图像
	 * @param url -视频地址
	 * @param sum 截图总数
	 * @param interval 间隔（每隔几帧截图一次，需要自行确定视频帧率）
	 * @return
	 * @throws IOException
	 */
	BufferedImage[] grabBufferImages(String url, int sum, int interval) throws IOException;
	
	/**
	 * 连续抓取图像
	 * @param url -视频地址
	 * @param fmt -图像数据结构（可选，为空默认BGR24）
	 * @param sum 截图总数
	 * @param interval 间隔（每隔几帧截图一次，需要自行确定视频帧率）
	 * @return
	 * @throws IOException
	 */
	BufferedImage[] grabBufferImages(String url, Integer fmt,int sum, int interval) throws IOException;

}
