package cc.eguid.cv.corelib.videoimageshot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import cc.eguid.cv.corelib.videoimageshot.core.JavaImgConverter;
import cc.eguid.cv.corelib.videoimageshot.threaddata.CurrentThreadData;


/**
 * 线程安全的FFmpeg截图服务
 * @author eguid
 *
 */
public class FFmpegScreenshot implements Screenshot {
	
	@Override
	public boolean shot(String url, String imgurl, String format) throws IOException {
		if (format == null) {
			format =CurrentThreadData.DETAULT_FORMAT;
		}
		BufferedImage img = CurrentThreadData.grabber.get().grabBufferImage(url);
		if (img != null) {
			JavaImgConverter.saveImage(img, format, imgurl);
			return true;
		}
		return false;

	}

	@Override
	public boolean shot(String url, String imgurl) throws IOException {
		String fomrat = null;
		int index = -1;
		if (imgurl != null && (index = imgurl.lastIndexOf(".")) > 0) {
			fomrat = imgurl.substring(index + 1, imgurl.length());
		}
		return shot(url, imgurl, fomrat);
	}

	@Override
	public String getImgBase64(String url, String format) throws IOException {
		if (format == null) {
			format =CurrentThreadData.DETAULT_FORMAT;
		}
		BufferedImage img = CurrentThreadData.grabber.get().grabBufferImage(url);
//		ByteBuffer buffer=CurrentThreadData.grabber.get().grabBuffer(url);
		if (img!= null) {
			String base64=JavaImgConverter.bufferedImage2Base64(img, format);
			return base64;
		}
		return null;
	}

	@Override
	public String getImgBase64(String url) throws IOException {
		return getImgBase64(url, null);
	}

	@Override
	public String shotAndGetBase64(String url, String imgurl)throws IOException {
		return shotAndGetBase64(url,imgurl,null);
	}

	@Override
	public String shotAndGetBase64(String url, String imgurl, String format) throws IOException {
		if (format == null) {
			format = CurrentThreadData.DETAULT_FORMAT;
		}
		BufferedImage img =CurrentThreadData.grabber.get().grabBufferImage(url);
		if (img != null) {
			JavaImgConverter.saveImage(img, format, imgurl);
			return JavaImgConverter.bufferedImage2Base64(img, format);
		}
		return null;
	}
}
