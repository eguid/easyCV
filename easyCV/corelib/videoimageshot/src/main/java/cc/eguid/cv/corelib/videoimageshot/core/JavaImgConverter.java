package cc.eguid.cv.corelib.videoimageshot.core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import cc.eguid.cv.corelib.videoimageshot.core.Base64Plus.Encoder;
import cc.eguid.cv.corelib.videoimageshot.threaddata.CurrentThreadData;

/**
 * java图像转换器（将ffmpeg图像转为java图像和base64）
 * 
 * @author eguid
 *
 */
public class JavaImgConverter {
	
	private static int createRandomRgb() {
		int[] rgbarr = new int[3];
		rgbarr[0] = (int) (Math.random() * 255);
		rgbarr[1] = (int) (Math.random() * 255);
		rgbarr[2] = (int) (Math.random() * 255);
		return ImagePixelAlgorithm.getRGB(rgbarr);
	}
	
	public static void demoview() {
		int width = 800, height = 600;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = createRandomRgb();
				image.setRGB(i, j, rgb);
			}
		}
		JLabel label = new JLabel();
		label.setSize(width, height);
		label.setIcon(new ImageIcon(image));

		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(label);
		frame.setVisible(true);
		Timer timer=new Timer("定时刷新", true);
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int rgb =createRandomRgb();
						image.setRGB(i, j, rgb);
					}
				}
				label.repaint();
			}
		}, 100, 1000/25);
	}
	
	/**
	 * BGR图像源数据转base64
	 * @param src
	 * @param width
	 * @param height
	 * @param format
	 * @return
	 * @throws IOException 
	 */
	public static String imagedataBGR2Base64(ByteBuffer src,int width,int height,String format) throws IOException {
		BufferedImage image=BGR2BufferedImage(src,width,height);
		String base64=bufferedImage2Base64(image, format);
		return base64;
	}
	
	/**
	 * 使用窗口显示BGR图像
	 * @param width
	 * @param height
	 * @param src
	 */
	public static void viewBGR(int width,int height,ByteBuffer src) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//		image.setRGB(0, 0, width, height, rgbarr, 0,height);
		Raster ra = image.getRaster();
		DataBuffer out = ra.getDataBuffer();
		DataBufferByte db=(DataBufferByte)out;
		ByteBuffer.wrap(db.getData()).put(src);
		viewImage(image);
	}
	
	/**
	 * 使用窗口显示RGB图像
	 * @param width
	 * @param height
	 * @param rgbarr -int 
	 */
	public static void viewRGB(int[] rgbarr,int width,int height){
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, rgbarr, 0,height);
		viewImage(image);
	}

	/**
	 * bufferedImage转base64
	 * @param format -格式（jpg,png等等）
	 * @return
	 * @throws IOException
	 */
	public static String bufferedImage2Base64(BufferedImage image, String format) throws IOException {
		Encoder encoder=CurrentThreadData.localEncoder.get();
		ByteArrayOutputStreamPlus baos = CurrentThreadData.localbaos.get();
		long last=System.currentTimeMillis();
		ImageIO.write(image, format, baos);// 写出到字节流，这个耗时比较长
		long now=System.currentTimeMillis();
		System.err.println("图像转换为字节流耗时："+(now-last));
//		byte[] bytes=baos.toByteArray();
		// 编码成base64
		String jpg_base64 = encoder.encodeToString(baos);
		return jpg_base64;
	}
	
	/**
	 * ByteBuffer直接转成
	 * @param src
	 * @return
	 */
	public static String buffer2Base64(ByteBuffer src) {
		
		Encoder encoder=CurrentThreadData.localEncoder.get();
		// 编码成base64
		String encoded= encoder.encode2String(src);
		return encoded;
	}
	
	/**
	 * 24位BGR转BufferedImage
	 * @param src -源数据
	 * @param width -宽度
	 * @param height-高度
	 * @return
	 */
	public static BufferedImage BGR2BufferedImage(ByteBuffer src,int width,int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Raster ra = image.getRaster();
		DataBuffer out = ra.getDataBuffer();
		DataBufferByte db=(DataBufferByte)out;
		ByteBuffer.wrap(db.getData()).put(src);
		return image;
	}

	
	/**
	 * 24位整型BGR转BufferedImage
	 * @param src -源数据
	 * @param width -宽度
	 * @param height-高度
	 * @return
	 */
	public static  BufferedImage BGR2BufferedImage(IntBuffer src,int width,int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Raster ra = image.getRaster();
		DataBuffer out = ra.getDataBuffer();
		DataBufferInt db=(DataBufferInt)out;
		IntBuffer.wrap(db.getData()).put(src);
		return image;
	}
	
	/**
	 * 24位整型RGB转BufferedImage
	 * @param src -源数据
	 * @param width -宽度
	 * @param height-高度
	 * @return
	 */
	public static  BufferedImage RGB2BufferedImage(IntBuffer src,int width,int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Raster ra = image.getRaster();
		DataBuffer out = ra.getDataBuffer();
		DataBufferInt db=(DataBufferInt)out;
		IntBuffer.wrap(db.getData()).put(src);
		return image;
	}

	/**
	 * 使用窗口显示BufferedImage图片
	 * @param image -BufferedImage
	 */
	public static void viewImage(BufferedImage image) {
		int width=image.getWidth(),height=image.getHeight();
		JLabel label = new JLabel();
		label.setSize(width, height);
		label.setIcon(new ImageIcon(image));

		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(label);
		frame.setVisible(true);
	}
	
	public static void saveImage(BufferedImage image,String format,String file) throws IOException {
		ImageIO.write(image, format, new File(file));
	}
	
	public static void saveImage(BufferedImage image,String format,File file) throws IOException {
		ImageIO.write(image, format, file);
	}
	
	public static void saveImage(BufferedImage image,String format,OutputStream file) throws IOException {
		ImageIO.write(image, format, file);
	}
	
	public static void saveImage(BufferedImage image,String format,ImageOutputStream file) throws IOException {
		ImageIO.write(image, format, file);
	}
}
