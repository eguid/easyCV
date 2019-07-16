package cc.eguid.cv.corelib.videoimageshot.grabber;

import java.io.IOException;

/**
 * base64图像数据抓取器
 * 
 * @author eguid
 *
 */
public interface Base64Grabber {

	/**
	 * 截取已转换为base64的图像数据
	 * 
	 * @param url 视频源
	 * @return
	 * @throws IOException
	 */
	String getBase64Image(String url) throws IOException;

	/**
	 * 截取已转换为base64的图像数据
	 * 
	 * @param url    视频源
	 * @param format -图像格式
	 * @return
	 * @throws IOException
	 */
	String getBase64Image(String url, String format) throws IOException;

	/**
	 * 截图保存文件并转换为base64图像数据
	 * 
	 * @param url
	 * @param format
	 * @param width 输出图像的宽度，支持缩放
	 * @param height 输出图像的高度，支持缩放
	 * @return
	 * @throws IOException
	 */
	String getBase64Image(String url, String format, Integer width, Integer height) throws IOException;

	/**
	 * 截图保存文件并转换为base64图像数据
	 * 
	 * @param url 视频源
	 * @param imgurl 截图保存地址
	 * @return
	 */
	String shotAndGetBase64Image(String url, String imgurl) throws IOException;

	/**
	 * 截图保存文件并转换为base64图像数据
	 * 
	 * @param url 视频源
	 * @param imgurl 截图保存地址
	 * @param format 图像格式
	 * @return
	 * @throws IOException
	 */
	String shotAndGetBase64Image(String url, String imgurl, String format) throws IOException;

	/**
	 * 截图保存文件并转换为base64图像数据
	 * 
	 * @param url 视频源
	 * @param imgurl 截图保存地址
	 * @param format 图像格式
	 * @param width 输出图像的宽度，支持缩放
	 * @param height 输出图像的高度，支持缩放
	 * @return
	 * @throws IOException
	 */
	String shotAndGetBase64Image(String url, String imgurl, String format, Integer width, Integer height) throws IOException;

}
