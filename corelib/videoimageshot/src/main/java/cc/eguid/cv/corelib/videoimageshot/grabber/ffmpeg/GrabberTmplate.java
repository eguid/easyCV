package cc.eguid.cv.corelib.videoimageshot.grabber.ffmpeg;

import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avutil.*;
import static org.bytedeco.javacpp.swscale.*;


import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodecParameters;
import org.bytedeco.javacpp.indexer.ByteBufferIndexer;

import cc.eguid.cv.corelib.videoimageshot.exception.CodecNotFoundExpception;
import cc.eguid.cv.corelib.videoimageshot.exception.FileNotOpenException;
import cc.eguid.cv.corelib.videoimageshot.exception.StreamInfoNotFoundException;
import cc.eguid.cv.corelib.videoimageshot.exception.StreamNotFoundException;
import cc.eguid.cv.corelib.videoimageshot.util.Console;

/**
 * 推荐使用GrabberTmplate4来替换该实现
 * @see GrabberTmplate4
 * @author eguid
 *
 */
@Deprecated
public abstract class GrabberTmplate {

	/*
	 * Register all formats and codecs
	 */
	static {
		av_register_all();
		avformat_network_init();
		av_log_set_level(AV_LOG_ERROR);//set log level
	}
	
	protected Integer width;//image width
	protected Integer height;//image height
    
	public GrabberTmplate() {
		super();
	}

	public GrabberTmplate(Integer width, Integer height) {
		super();
		this.width = width;
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	/**
	 * 打开视频流
	 * @param url -url
	 * @return
	 * @throws FileNotOpenException
	 */
	protected AVFormatContext openInput(String url) throws FileNotOpenException{
		AVFormatContext pFormatCtx = new AVFormatContext(null);
		if(avformat_open_input(pFormatCtx, url, null, null)==0) {
			return pFormatCtx;
		}
		throw new FileNotOpenException("Didn't open video file");
	}
	
	/**
	 * 检索流信息（rtsp/rtmp检索时间过长问题解决）
	 * @param pFormatCtx
	 * @return
	 */
	protected AVFormatContext findStreamInfo(AVFormatContext pFormatCtx) throws StreamInfoNotFoundException{
		if (avformat_find_stream_info(pFormatCtx, (PointerPointer<?>)null)>= 0) {
			return pFormatCtx;
		}
		throw new StreamInfoNotFoundException("Didn't retrieve stream information");
	}
	
	/**
	 * 查找视频通道
	 * @param pFormatCtx
	 * @return
	 */
	protected int findVideoStreamIndex(AVFormatContext pFormatCtx) {
		int size=pFormatCtx.nb_streams();
		for (int i = 0; i < size; i++) {
			AVStream stream=pFormatCtx.streams(i);
			AVCodecContext codec=stream.codec();//update to a new function : stream.codecpar();
//			AVCodecParameters codec=stream.codecpar();
			int type=codec.codec_type();
			if (type == AVMEDIA_TYPE_VIDEO) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 查找音频通道
	 * @param pFormatCtx
	 * @return
	 */
	protected int findAudioStreamIndex(AVFormatContext pFormatCtx) {
		int size=pFormatCtx.nb_streams();
		for (int i = 0; i < size; i++) {
			AVStream stream=pFormatCtx.streams(i);
			AVCodecContext codec=stream.codec();//ffmpeg 4.x update to a new function : stream.codecpar();
//			AVCodecParameters codec=stream.codecpar();
			int type=codec.codec_type();
			if (type == AVMEDIA_TYPE_AUDIO) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 指定视频帧位置获取对应视频帧
	 * @param pFormatCtx
	 * @param videoStream
	 * @return
	 */
	protected AVCodecContext findVideoStream(AVFormatContext pFormatCtx ,int videoStreamIndex)throws StreamNotFoundException {
		if(videoStreamIndex >=0) {
			// Get a pointer to the codec context for the video stream
			AVStream stream=pFormatCtx.streams(videoStreamIndex);
			AVCodecContext pCodecCtx = stream.codec();
			return pCodecCtx;
		}
		//if no stream,throws Excetion.
		throw new StreamNotFoundException("Didn't open video file");
	}
	
	/**
	 * 查找并尝试打开解码器
	 * @return 
	 */
	protected AVCodecContext findAndOpenCodec(AVCodecContext pCodecCtx) {
		// Find the decoder for the video stream
		AVCodec pCodec = avcodec_find_decoder(pCodecCtx.codec_id());
		if (pCodec == null) {
			System.err.println("Codec not found!");
			throw new CodecNotFoundExpception("Codec not found!");
		}
		AVDictionary optionsDict = null;
		// Open codec
		if (avcodec_open2(pCodecCtx, pCodec, optionsDict) < 0) {
			System.err.println("Could not open codec!");
			throw new CodecNotFoundExpception("Could not open codec!"); // Could not open codec
		}
		return pCodecCtx;
	}
	

	/**
	 * 抓取视频帧（默认跳过音频帧和空帧）
	 * @param url
	 * @param fmt - 像素格式，比如AV_PIX_FMT_BGR24
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer grabVideoFrame(String url,int fmt) throws IOException {
		
		// Open video file
		AVFormatContext pFormatCtx=openInput(url);
		//不再使用减少缓存和检索时长方法，该方法导致高清/高清视频无法获取到i帧的问题
//		if(url.indexOf("rtmp")>=0) {
			//解决rtmp检索时间过长问题
		    //限制最大读取缓存，
//		    pFormatCtx.probesize(PROBESIZE);//设置500k能保证高清视频也能读取到关键帧
		  //限制avformat_find_stream_info最大持续时长，设置成3秒
//		    pFormatCtx.max_analyze_duration(MAX_ANALYZE_DURATION);
//		}
		// Retrieve stream information
		pFormatCtx=findStreamInfo(pFormatCtx);
		// Dump information about file onto standard error
		//av_dump_format(pFormatCtx, 0, url, 0);

		//Find a video stream
		final int videoStream=findVideoStreamIndex(pFormatCtx);
		
		AVCodecContext pCodecCtx =findVideoStream(pFormatCtx,videoStream);
		
		// Find the decoder for the video stream
		pCodecCtx= findAndOpenCodec(pCodecCtx);
		// Allocate video frame
		AVFrame pFrame = av_frame_alloc();
		//Allocate an AVFrame structure
		AVFrame pFrameRGB = av_frame_alloc();

		int srcWidth = pCodecCtx.width();
		int srcHeight = pCodecCtx.height();
		//如果分辨率为空，则保持图片尺寸保持不变
		if(width==null||height==null) {
			width=srcWidth;
			height=srcHeight;
		}
		pFrameRGB.width(width);
		pFrameRGB.height(height);
		pFrameRGB.format(fmt);

		// Determine required buffer size and allocate buffer
		int numBytes = avpicture_get_size(fmt, width, height);
		DoublePointer param=null;
		SwsContext sws_ctx = sws_getContext(srcWidth, srcHeight, pCodecCtx.pix_fmt(), width, height,fmt, SWS_FAST_BILINEAR, null, null, param);

		BytePointer buffer = new BytePointer(av_malloc(numBytes));
		// Assign appropriate parts of buffer to image planes in pFrameRGB
		// Note that pFrameRGB is an AVFrame, but AVFrame is a superset
		// of AVPicture
		avpicture_fill(new AVPicture(pFrameRGB), buffer, fmt, width, height);
		AVPacket packet = new AVPacket();
		int[] frameFinished = new int[1];
		try {
			while (av_read_frame(pFormatCtx, packet) >= 0) {
				// Is this a packet from the video stream?
				if (packet.stream_index() == videoStream) {
					//Is i frame?
					if(packet.flags()==AV_PKT_FLAG_KEY) {
						// Decode video frame
						avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);
						// Did we get a video frame?
						if (frameFinished[0] >= 0) {
							// Convert the image from its native format to BGR
							sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, srcHeight, pFrameRGB.data(),pFrameRGB.linesize());
							//Convert BGR to ByteBuffer
							return saveFrame(pFrameRGB, width, height);
						}
					}
				}
				// Free the packet that was allocated by av_read_frame
				av_free_packet(packet);
			}
			//读取错误或读取完成
			return null;
		}finally {
//			av_free(buffer);//Don't free buffer
			av_free_packet(packet);// Free the packet that was allocated by av_read_frame
			av_free(pFrameRGB);// Free the RGB image
			av_free(pFrame);// Free the YUV frame
			sws_freeContext(sws_ctx);//Free SwsContext
			avcodec_close(pCodecCtx);// Close the codec
			avformat_close_input(pFormatCtx);// Close the video file
		}
	}

	/**
	 * BGR图像帧转字节缓冲区（BGR结构）
	 * 
	 * @param pFrame
	 *            -bgr图像帧
	 * @param width
	 *            -宽度
	 * @param height
	 *            -高度
	 * @return
	 * @throws IOException
	 */
	abstract ByteBuffer saveFrame(AVFrame pFrameRGB, int width, int height);
}
