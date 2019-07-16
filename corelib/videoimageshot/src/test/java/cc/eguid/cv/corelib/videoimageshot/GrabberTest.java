package cc.eguid.cv.corelib.videoimageshot;

import java.awt.image.BufferedImage;
import java.io.IOException;

import cc.eguid.cv.corelib.videoimageshot.core.JavaImgConverter;
import cc.eguid.cv.corelib.videoimageshot.grabber.FFmpegVideoImageGrabber;
import cc.eguid.cv.corelib.videoimageshot.util.ImageView;

public class GrabberTest {


	/**
	 * test
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		ByteBuffer buf=new VideoFrameGrabber("rtmp://live.hkstv.hk.lxdns.com/live/hks").grab();
//		ByteBuffer buf=new VideoFrameGrabber().grabBuffer();
//		BufferedImage image=new FFmpegVideoImageGrabber("rtmp://live.hkstv.hk.lxdns.com/live/hks").grabBufferImage();
		BufferedImage image=new FFmpegVideoImageGrabber("rtmp://10.23.49.12:1935/live/100200009").grabBufferImage();
//		JavaImgConverter.viewBGR(1280, 720, buf);
//		BufferedImage image=JavaImgConverter.BGR2BufferedImage(buf, 1280,720);
		ImageView.show(image);
	}


	
}
