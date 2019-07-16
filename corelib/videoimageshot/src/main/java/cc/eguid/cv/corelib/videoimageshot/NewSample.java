package cc.eguid.cv.corelib.videoimageshot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import cc.eguid.cv.corelib.videoimageshot.grabber.Base64Grabber;
import cc.eguid.cv.corelib.videoimageshot.grabber.BufferGrabber;
import cc.eguid.cv.corelib.videoimageshot.grabber.BufferedImageGrabber;
import cc.eguid.cv.corelib.videoimageshot.grabber.BytesGrabber;
import cc.eguid.cv.corelib.videoimageshot.grabber.FFmpeg4VideoImageGrabber;
import cc.eguid.cv.corelib.videoimageshot.util.ImageView;

/**
 * 新版本推荐使用示例
 * 
 * @author eguid
 *
 */
public class NewSample {

	public static byte[] bytesImageSample(String url) throws IOException {
		// create a bytesGrabber
		BytesGrabber grabber=new FFmpeg4VideoImageGrabber(url);
		return grabber.grabBytes();
	}
	
	public static byte[] bytesImageSample2(String url) throws IOException {
		BytesGrabber grabber=new FFmpeg4VideoImageGrabber();
		return grabber.grabBytes(url);
	}

	public static BufferedImage bufferImageSample(String url) throws IOException {
		BufferedImageGrabber grabber=new FFmpeg4VideoImageGrabber(url);
		return grabber.grabBufferImage();
	}

	public static BufferedImage bufferImageSample2(String url) throws IOException {
		BufferedImageGrabber grabber=new FFmpeg4VideoImageGrabber();
		return grabber.grabBufferImage(url);
	}
	
	public static ByteBuffer bufferedImageSample(String url) throws IOException {
		BufferGrabber grabber =new FFmpeg4VideoImageGrabber();
		return grabber.grabBuffer(url);
	}
	
	public static ByteBuffer bufferedImageSample2(String url) throws IOException {
		BufferGrabber grabber =new FFmpeg4VideoImageGrabber(url);
		return grabber.grabBuffer();
	}

	public static String base64ImageSample(String url) throws IOException {
		Base64Grabber grabber=new FFmpeg4VideoImageGrabber();
		return grabber.getBase64Image(url);
	}
	
	public static String base64ImageSample2(String url) throws IOException {
		Base64Grabber grabber=new FFmpeg4VideoImageGrabber();
		return grabber.getBase64Image(url);
	}
	
	public static String base64ImageSample3(String url) throws IOException {
		Base64Grabber grabber=new FFmpeg4VideoImageGrabber();
		//默认格式是jpg
		return grabber.shotAndGetBase64Image(url, "test.jpg");
	}
	
	public static String base64ImageSample4(String url) throws IOException {
		Base64Grabber grabber=new FFmpeg4VideoImageGrabber();
		//截图保存图片到指定位置并返回base64数据
		return grabber.shotAndGetBase64Image(url, "test.png", "png",800, 600);
	}

	public static void main(String[] args) throws IOException {
		String url="rtmp://live.hkstv.hk.lxdns.com/live/hks1";

//		ImageView.showBGR(480,288,bytesImageSample2(url));
		//显示
//		ImageView.show(bufferImageSample(url));
		
		ImageView.showBGR(480,288,bufferedImageSample(url));
//		Console.log(base64ImageSample(url));
//		Console.log(base64ImageSample4(url));
	}
}
