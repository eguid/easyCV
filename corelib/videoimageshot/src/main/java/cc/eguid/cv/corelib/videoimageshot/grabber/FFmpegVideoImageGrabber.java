package cc.eguid.cv.corelib.videoimageshot.grabber;

import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_BGR24;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.avutil.AVFrame;

import cc.eguid.cv.corelib.videoimageshot.core.JavaImgConverter;

/**
 * 视频帧抓取
 * 
 * @author eguid
 *
 */
public class FFmpegVideoImageGrabber extends GrabberTmplate implements BufferGrabber,BufferedImageGrabber{
	
	@Override
	protected ByteBuffer saveFrame(AVFrame pFrame, int width, int height){
		BytePointer data = pFrame.data(0);
		int size = width * height * 3;
		ByteBuffer buf = data.position(0).limit(size).asBuffer();
		return buf;
	}
	
	@Override
	public ByteBuffer grabBuffer() throws IOException {
		return grabBuffer(url);
	}
	
	@Override
	public ByteBuffer grabBuffer(String url) throws IOException {
		return grabBuffer(url,null);
	}
	
	@Override
	public ByteBuffer grabBuffer(String url,Integer fmt) throws IOException {
		ByteBuffer buf=null;
		if(validateAndInit(url,fmt)) {
			buf= grabVideoFrame(url,this.fmt);
		}
		return buf;
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
	public BufferedImage grabBufferImage() throws IOException {
		return grabBufferImage(url,null);
	}

	@Override
	public BufferedImage grabBufferImage(String url) throws IOException {
		return grabBufferImage(url,null);
	}

	@Override
	public BufferedImage grabBufferImage(String url, Integer fmt) throws IOException {
		BufferedImage image=null;
		long now =System.currentTimeMillis();
		ByteBuffer buf=grabBuffer(url,fmt);
		long cu=System.currentTimeMillis();
		System.err.println("截图耗时："+(cu-now));
		image= JavaImgConverter.BGR2BufferedImage(buf,width,height);
		return image;
	}
	
	private String url;//视频地址
	private Integer fmt;//图像数据结构
	
	public FFmpegVideoImageGrabber() {
	}
	
	public FFmpegVideoImageGrabber(String url) {
		this.url=url;
	}
	
	public FFmpegVideoImageGrabber(String url, Integer fmt) {
		super();
		this.url = url;
		this.fmt = fmt;
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
