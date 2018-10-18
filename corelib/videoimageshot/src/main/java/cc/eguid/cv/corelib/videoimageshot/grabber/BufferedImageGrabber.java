package cc.eguid.cv.corelib.videoimageshot.grabber;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface BufferedImageGrabber {
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
}
