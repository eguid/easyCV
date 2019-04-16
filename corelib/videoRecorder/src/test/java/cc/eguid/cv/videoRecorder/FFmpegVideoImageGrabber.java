package cc.eguid.cv.videoRecorder;

import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_BGR24;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.avutil.AVFrame;

/**
 * 视频帧抓取
 * 
 * @author eguid
 *
 */
public class FFmpegVideoImageGrabber extends GrabberTmplate {

	ImageViewer viwer=null;
	int index=0;
	
	public ImageViewer getViwer() {
		return viwer;
	}

	public void setViwer(ImageViewer viwer) {
		this.viwer = viwer;
	}

	@Override
	protected ByteBuffer saveFrame(AVFrame pFrame, int width, int height) {
		BytePointer data = pFrame.data(0);
		int size = width * height * 3;
		ByteBuffer buf = data.position(0).limit(size).asBuffer();
		if(viwer==null) {
			viwer=new ImageViewer(width,height, BufferedImage.TYPE_3BYTE_BGR);
		}else {
			if(index==0) {
				index++;
				viwer.setSize(width, height);
			}
		}
		
		viwer.show(buf,width,height);
		return buf;
	}

	public ByteBuffer grabBuffer() throws IOException {
		return grabBuffer(url);
	}

	public ByteBuffer grabBuffer(String url) throws IOException {
		return grabBuffer(url, null);
	}

	public ByteBuffer grabBuffer(String url, Integer fmt) throws IOException {
		ByteBuffer buf = null;
		if (validateAndInit(url, fmt)) {
			buf = grabVideoFrame(url, this.fmt);
		}
		return buf;
	}

	/*
	 * 验证并初始化
	 * 
	 * @param url
	 * 
	 * @param fmt
	 * 
	 * @return
	 */
	private boolean validateAndInit(String url, Integer fmt) {
		if (url == null) {
			throw new IllegalArgumentException("Didn't open video file");
		}
		if (fmt == null) {
			this.fmt = AV_PIX_FMT_BGR24;
		}
		return true;
	}
	
	public BufferedImage grabBufferImage() throws IOException {
		return grabBufferImage(url, null);
	}

	public BufferedImage grabBufferImage(String url) throws IOException {
		return grabBufferImage(url, null);
	}

	public BufferedImage grabBufferImage(String url, Integer fmt) throws IOException {
		BufferedImage image = null;
		ByteBuffer buf = grabBuffer(url, fmt);
		image = JavaImgConverter.BGR2BufferedImage(buf, width, height);
		return image;
	}

	/**
	 * bgr图像帧转BufferedImage图片
	 * 
	 * @param pFrame
	 *            -bgr图像帧
	 * @param width
	 *            -宽度
	 * @param height
	 *            -高度
	 * @return
	 */
	protected BufferedImage frame2BufferImage(AVFrame pFrame, int width, int height) {
		BytePointer data = pFrame.data(0);
		int size = width * height * 3;
		ByteBuffer buf = data.position(0).limit(size).asBuffer();
		return JavaImgConverter.BGR2BufferedImage(buf, width, height);
	}

	private String url;// 视频地址
	private Integer fmt;// 图像数据结构

	public FFmpegVideoImageGrabber() {
	}

	public FFmpegVideoImageGrabber(String url) {
		this.url = url;
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
