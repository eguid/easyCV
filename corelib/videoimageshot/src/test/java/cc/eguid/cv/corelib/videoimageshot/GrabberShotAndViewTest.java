package cc.eguid.cv.corelib.videoimageshot;

import java.awt.image.BufferedImage;
import java.io.IOException;

import cc.eguid.cv.corelib.videoimageshot.core.JavaImgConverter;
import cc.eguid.cv.corelib.videoimageshot.grabber.FFmpegVideoImageGrabber;

public class GrabberShotAndViewTest {


	/**
	 * 截图并显示
	 * @throws IOException
	 */
	public static void shotAndView() throws IOException {
		BufferedImage image=new FFmpegVideoImageGrabber("rtmp://10.23.49.12:1935/live/100100015").grabBufferImage();
		JavaImgConverter.viewImage(image);
	}
	
	/**
	 * 截图缩放图像并显示
	 * @throws IOException
	 */
	public static void shotScaleAndView() throws IOException {
		BufferedImage image=new FFmpegVideoImageGrabber("rtmp://10.23.49.12:1935/live/100100015").setWidth(800).setHeight(600).grabBufferImage();
		JavaImgConverter.viewImage(image);
	}
	
	/**
	 * 测试
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		shotAndView();
//		shotScaleAndView();
	}


	
}
