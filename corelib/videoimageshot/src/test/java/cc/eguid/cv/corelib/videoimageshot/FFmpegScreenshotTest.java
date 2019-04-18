package cc.eguid.cv.corelib.videoimageshot;

import java.io.IOException;

/**
 * 测试截图
 * @author eguid
 *
 */
public class FFmpegScreenshotTest {
	static Screenshot shoter=new FFmpegScreenshot();
	
	/**
	 * 测试截图保存jpg
	 * @throws IOException
	 */
	public static void shotJpg() throws IOException {
		shoter.shot("rtmp://media3.sinovision.net:1935/live/livestream", "test.jpg");
	}
	
	/**
	 * 测试截图保存png
	 * @throws IOException
	 */
	public static void shotPng() throws IOException {
		shoter.shot("rtmp://media3.sinovision.net:1935/live/livestream","test.png" );
	}
	
	/**
	 * 测试截图保存jpeg
	 * @throws IOException
	 */
	public static void shotJpeg() throws IOException {
		shoter.shot("rtmp://media3.sinovision.net:1935/live/livestream", "test.jpeg");
	}
	
	/**
	 * 测试截图保存bmp
	 * @throws IOException
	 */
	public static void shotBmp() throws IOException {
		shoter.shot("rtmp://media3.sinovision.net:1935/live/livestream", "rtmp.bmp");
	}
	
	/**
	 * 测试截图保存gif
	 * @throws IOException
	 */
	public static void shotGif() throws IOException {
		shoter.shot("rtmp://media3.sinovision.net:1935/live/livestream", "test.gif");
	}
	
	/**
	 * 测试截图并返回base64编码
	 * @throws IOException
	 */
	public static void shotGetBase64() throws IOException {
		String base64=shoter.getImgBase64("rtmp://media3.sinovision.net:1935/live/livestream");
		System.err.println(base64);
	}
	
	/**
	 * 测试截图保存文件并返回base64编码
	 * @throws IOException
	 */
	public static void shotAndGetBase64() throws IOException {
		String base64=shoter.shotAndGetBase64("rtmp://media3.sinovision.net:1935/live/livestream", "shot.jpg");
		System.err.println(base64);
	}
	
	/**
	 * 测试截图缩放保存文件并返回base64编码
	 * @throws IOException
	 */
	public static void shotAndGetBase64Scale() throws IOException {
		String base64=shoter.shotAndGetBase64("rtmp://media3.sinovision.net:1935/live/livestream", "scale.png","png",300,200);
		System.err.println(base64);
	}
	
	public static void main(String[] args) throws IOException {
//		shotJpg();
//		shotPng();
//		shotJpeg();
//		shotBmp();
//		shotGif();
//		shotGetBase64();
//		shotAndGetBase64();
		shotAndGetBase64Scale();
	}
}
