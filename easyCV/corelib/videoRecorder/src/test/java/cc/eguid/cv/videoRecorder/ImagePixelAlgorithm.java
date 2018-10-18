package cc.eguid.cv.videoRecorder;

/**
 * 图像像素算法
 * 
 * @author eguid
 *
 */
public class ImagePixelAlgorithm {

	public static final ThreadLocal<byte[]> sharedArray = new ThreadLocal<byte[]>() {
		byte[] sharedArr;

		protected byte[] initialValue() {
			if(sharedArr==null) {
				sharedArr = new byte[3];
			}
			return sharedArr;
		};
	};

	/*
	 * 像素转换算法
	 */
	/**
	 * rgb整型值转换为rgb数组
	 * 
	 * @param rgb -rgb整型值
	 * @param arr -rgb数组
	 */
	public static void convertRGB(int rgb, byte[] arr) {
		convertRGB(rgb,arr,0);
	}
	
	/**
	 * rgb整型值转换为rgb数组
	 * 
	 * @param rgb -rgb整型值
	 * @param arr -rgb数组（0-红，1-绿，2-蓝）
	 * @param index -rgb数组开始位置（必须保证数组大小大于等于index+2，否则数组溢出）
	 */
	public static void convertRGB(int rgb, byte[] arr,int index) {
		byte r = (byte)((rgb & 0xff0000) >> 16);
		byte g = (byte)((rgb & 0xff00) >> 8);
		byte b = (byte) (rgb & 0xff);
		arr[index] = r;
		arr[index+1] = g;
		arr[index+2] = b;
	}

	/**
	 * rgb整型值转换为rgb数组（0-红，1-绿，2-蓝）
	 * @param rgb
	 * @return
	 */
	public static byte[] convertRGB(int rgb) {
		byte[] arr=sharedArray.get();
		byte r = (byte)((rgb & 0xff0000) >> 16);
		byte  g = (byte)((rgb & 0xff00) >> 8);
		byte  b =(byte)( (rgb & 0xff));
		arr[0]=r;arr[1]=g;arr[2]=b;
		return arr.clone();
	}

	/**
	 * 转换为单个RGB整型值
	 * 
	 * @param r
	 *            -红
	 * @param g
	 *            -绿
	 * @param b
	 *            - 蓝
	 * @return
	 */
	public static int getRGB(int r, int g, int b) {
		int rgb = b & 0xff | (g & 0xff) << 8 | (r & 0xff) << 16 | 0xff000000;
		return rgb;
	}

	/**
	 * 转换为单个RGB整型值
	 * 
	 * @param rgbarr
	 *            -像素数组（数组各位置表示像素：0-红，1-绿，2-蓝）
	 * @return
	 */
	public static int getRGB(int[] rgbarr) {
		int rgb = ((int) rgbarr[0]) & 0xff | (((int) rgbarr[1]) & 0xff) << 8 | (((int) rgbarr[2]) & 0xff) << 16
				| 0xff000000;
		return rgb;
	}
	
	/**
	 * 转换为单个RGB整型值
	 * 
	 * @param rgbarr
	 *            -像素数组（数组各位置表示像素：0-红，1-绿，2-蓝）
	 * @param index -起始位置（必须保证数组大小大于等于index+2，否则数组溢出）
	 * @return
	 */
	public static int getRGB(byte[] rgbarr,int index) {
		int rgb = ((int) rgbarr[index]) & 0xff | (((int) rgbarr[index+1]) & 0xff) << 8 | (((int) rgbarr[index+2]) & 0xff) << 16
				| 0xff000000;
		return rgb;
	}
}
