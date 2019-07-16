package cc.eguid.cv.corelib.videoimageshot.grabber;

import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_BGR24;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.avutil.AVFrame;

import cc.eguid.cv.corelib.videoimageshot.core.JavaImgConverter;

/**
 * 使用新版本的ffmpeg-api重写了整个流程
 * @author eguid-matebook
 *
 */
public class FFmpeg4VideoImageGrabber extends GrabberTemplate4 implements Base64Grabber,BufferedImageGrabber,BufferGrabber,BytesGrabber{

	public final static String DETAULT_FORMAT = "jpg";
	
	@Override
	byte[] saveFrame(AVFrame frameRGB, int width, int height) {
		BytePointer data = frameRGB.data(0);
		int size = width * height * 3;
		//复制虚拟机外内存数据到java虚拟机中，因为这个方法之后会清理内存
		byte[] bytes=new byte[size];
		data.position(0).limit(size).get(bytes,0,size);
		return bytes;
	}
	
	/*
	 * 验证并初始化
	 * @param url
	 * @param fmt
	 * @return
	 */
	private boolean validateAndInit(String url,Integer fmt) {
		if (url == null) {
			throw new IllegalArgumentException("Didn't open video file");
		}
		if(fmt == null) {
			this.fmt=AV_PIX_FMT_BGR24;
		}
		return true;
	}
	
	@Override
	public byte[] grabBytes() throws IOException {
		return grabBytes(this.url);
	}

	@Override
	public byte[] grabBytes(String url) throws IOException {
		return grabBytes(url,null);
	}

	@Override
	public byte[] grabBytes(String url, Integer fmt) throws IOException {
		byte[] buf=null;
		if(validateAndInit(url,fmt)) {
			buf = grabVideoFrame(url,this.fmt);
		}
		return buf;
	}

	@Override
	public byte[][] grabBytes(String url, Integer fmt, int sum, int interval) throws IOException {
		return grabVideoFrame(url, fmt, sum, interval);
	}
	
	@Override
	public ByteBuffer grabBuffer() throws IOException {
		return grabBuffer(this.url);
	}

	@Override
	public ByteBuffer grabBuffer(String url) throws IOException {
		return grabBuffer(url,null);
	}

	@Override
	public ByteBuffer grabBuffer(String url, Integer fmt) throws IOException {
		byte[] bytes=grabBytes(url, fmt);
		ByteBuffer buf=ByteBuffer.wrap(bytes);
		return buf;
	}

	@Override
	public BufferedImage grabBufferImage() throws IOException {
		return grabBufferImage(this.url,null);
	}

	@Override
	public BufferedImage grabBufferImage(String url) throws IOException {
		return grabBufferImage(url,null);
	}

	@Override
	public BufferedImage grabBufferImage(String url, Integer fmt) throws IOException {
		BufferedImage image=null;
		byte[] buf=grabBytes(url,fmt);
		image= JavaImgConverter.BGR2BufferedImage(buf,this.width,this.height);
		return image;
	}
	
	@Override
	public String getBase64Image(String url) throws IOException {
		return getBase64Image(url, null);
	}

	@Override
	public String getBase64Image(String url, String format) throws IOException {
		return getBase64Image(url, format,this.width,this.height);
	}

	@Override
	public String getBase64Image(String url, String format, Integer width, Integer height) throws IOException {
		if (format == null) {
			format =DETAULT_FORMAT;
		}
		BufferedImage img =grabBufferImage(url);
		if (img!= null) {
			String base64=JavaImgConverter.bufferedImage2Base64(img, format);
			return base64;
		}
		return null;
	}

	@Override
	public String shotAndGetBase64Image(String url, String imgurl) throws IOException {
		return shotAndGetBase64Image(url, imgurl, null);
	}

	@Override
	public String shotAndGetBase64Image(String url, String imgurl, String format) throws IOException {
		return shotAndGetBase64Image(url, imgurl, format,null,null);
	}

	@Override
	public String shotAndGetBase64Image(String url, String imgurl, String format, Integer width, Integer height)
			throws IOException {
		if (format == null) {
			format = DETAULT_FORMAT;
		}
		BufferedImage img =grabBufferImage(url);
		if (img != null) {
			JavaImgConverter.saveImage(img, format, imgurl);
			return JavaImgConverter.bufferedImage2Base64(img, format);
		}
		return null;
	}
	
	private String url;//视频地址
	private Integer fmt;//图像数据结构
	
	public FFmpeg4VideoImageGrabber() {}
	
	public FFmpeg4VideoImageGrabber(String url) {
		this.url=url;
	}
	
	public FFmpeg4VideoImageGrabber(String url, Integer fmt) {
		super();
		this.url = url;
		this.fmt = fmt;
	}
	
	public FFmpeg4VideoImageGrabber(String url, Integer fmt,Integer width,Integer height) {
		super(width,height);
		this.url = url;
		this.fmt = fmt;
		this.width=width;
		this.height=height;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public int getFmt() {
		return fmt;
	}

	public void setFmt(int fmt) {
		this.fmt = fmt;
	}
	
}
