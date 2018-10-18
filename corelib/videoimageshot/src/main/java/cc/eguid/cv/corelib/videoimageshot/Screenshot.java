package cc.eguid.cv.corelib.videoimageshot;

import java.io.IOException;

/**
 * 视频截图（根据指定视频流截图，返回base64，并支持保存到指定位置的图片）
 * 
 * @author eguid
 *
 */
public interface Screenshot {

	/**
	 * 截图
	 * 
	 * @param url -视频地址
	 * @param imgurl -保存的图片地址（不带后缀）
	 * @param format 图片格式（图片后缀，如果为空默认jpg）
	 */
	boolean shot(String url, String imgurl,String format) throws IOException;

	/**
	 * 截图
	 * 
	 * @param url -视频地址
	 * @param imgurl-图片地址（带后缀，如果不带后缀默认jpg格式）
	 * @throws IOException
	 */
	boolean shot(String url,String imgurl) throws IOException;
	
	/**
	 * 截图（只返回图像的base64编码，默认jpg格式）
	 * @param url -视频地址
	 * @return
	 * @throws IOException 
	 */
	String getImgBase64(String url) throws IOException;

	/**
	 * 截图（只返回图像的base64编码，默认jpg格式）
	 * @param url -视频地址
	 * @param format-图片格式（如果为空，默认jpg格式）
	 * @return
	 * @throws IOException 
	 */
	String getImgBase64(String url, String fommat)throws IOException;
	
	/**
	 * 截图并返回base64数据
	 * @param url -视频地址
	 * @param imgurl-图片地址（带后缀，如果不带后缀默认jpg格式）
	 * @return
	 */
	String shotAndGetBase64(String url,String imgurl) throws IOException;
	
	/**
	 * 截图并返回base64数据
	 * @param url -视频地址
	 * @param imgurl-图片地址（带后缀，如果不带后缀默认jpg格式）
	 * @param format-图片格式（如果为空，默认jpg格式）
	 * @return
	 * @throws IOException 
	 */
	String shotAndGetBase64(String url,String imgurl, String fommat) throws IOException ;
	
}
