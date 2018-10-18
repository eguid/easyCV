package cc.eguid.cv.videoRecorder;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class GrabberTest {


	/**
	 * test
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		ByteBuffer buf=new VideoFrameGrabber("rtmp://live.hkstv.hk.lxdns.com/live/hks").grab();
//		ByteBuffer buf=new VideoFrameGrabber().grabBuffer();

		ImageViewer viwer=new ImageViewer(800,600, BufferedImage.TYPE_3BYTE_BGR);
		FFmpegVideoImageGrabber grabber=new FFmpegVideoImageGrabber();
		grabber.setViwer(viwer);
		viwer.setGrabber(grabber);
//		grabber.grab("rtmp://live.hkstv.hk.lxdns.com/live/hks");
		
//		new FFmpegVideoImageGrabber("http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8").grabBuffer();
//		JavaImgConverter.viewBGR(1280, 720, buf);
//		BufferedImage image=JavaImgConverter.BGR2BufferedImage(buf, 1280,720);
//		JavaImgConverter.viewImage(image);
		
	}


	
}
