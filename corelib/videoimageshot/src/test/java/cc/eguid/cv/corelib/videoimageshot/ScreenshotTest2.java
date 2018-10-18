package cc.eguid.cv.corelib.videoimageshot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ScreenshotTest2 {

	public static void main(String[] args) throws IOException {
		Screenshot shoter=new FFmpegScreenshot();
//		shoter.shot("http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8", "test.jpg");
//		shoter.shot("rtmp://live.hkstv.hk.lxdns.com/live/hks", "test.jpeg");
//		shoter.shot("http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8", "test.png");
//		shoter.shot("rtmp://live.hkstv.hk.lxdns.com/live/hks", "test.gif");
//		String base64=shoter.getImgBase64("rtmp://live.hkstv.hk.lxdns.com/live/hks");
		long now=System.currentTimeMillis();
//		System.err.println(now);
		String base64=shoter.getImgBase64("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov", "jpg");
		
		File file=new File("test.txt");
		BufferedWriter writer=new BufferedWriter(new FileWriter(file));
		writer.write(base64);
		writer.close();
//		
//		
////		shoter.shot("rtmp://10.23.7.90:1935/live/601001008", "rtmp.jpg");
		System.err.println("第一次总耗时："+((System.currentTimeMillis()-now)));
//		now=System.currentTimeMillis();
//		String base641=shoter.getImgBase64("rtmp://live.hkstv.hk.lxdns.com/live/hks", "jpg");
//		
////		shoter.shot("rtmp://10.23.7.90:1935/live/601001008", "rtmp.jpg");
//		System.err.println("第二次总耗时："+((System.currentTimeMillis()-now)));
//		
//		now=System.currentTimeMillis();
//		String base642=shoter.getImgBase64("rtmp://live.hkstv.hk.lxdns.com/live/hks", "jpg");
//		
////		shoter.shot("rtmp://10.23.7.90:1935/live/601001008", "rtmp.jpg");
//		System.err.println("第三次总耗时："+((System.currentTimeMillis()-now)));
		
	}
}
