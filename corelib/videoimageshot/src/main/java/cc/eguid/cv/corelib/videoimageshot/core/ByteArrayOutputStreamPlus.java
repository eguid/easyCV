package cc.eguid.cv.corelib.videoimageshot.core;

import java.io.ByteArrayOutputStream;

/**
 * ByteArrayOutputStream改进版 增加获取管理的数组
 * 
 * @author eguid
 *
 */
public class ByteArrayOutputStreamPlus extends ByteArrayOutputStream {

	public ByteArrayOutputStreamPlus() {
		super();
	}

	public ByteArrayOutputStreamPlus(int i) {
		super(i);
	}

	public byte[] getBuf() {
		return this.buf;
	}
}
