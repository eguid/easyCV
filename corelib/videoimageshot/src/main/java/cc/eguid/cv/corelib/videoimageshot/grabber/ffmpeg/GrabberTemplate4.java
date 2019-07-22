package cc.eguid.cv.corelib.videoimageshot.grabber.ffmpeg;

import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avutil.*;
import static org.bytedeco.javacpp.swscale.*;

import java.io.IOException;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVCodecParameters;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil.AVDictionary;
import org.bytedeco.javacpp.avutil.AVFrame;
import org.bytedeco.javacpp.swscale.SwsContext;

import cc.eguid.cv.corelib.videoimageshot.exception.CodecNotFoundExpception;
import cc.eguid.cv.corelib.videoimageshot.exception.FileNotOpenException;
import cc.eguid.cv.corelib.videoimageshot.exception.StreamInfoNotFoundException;
import cc.eguid.cv.corelib.videoimageshot.exception.StreamNotFoundException;
import cc.eguid.cv.corelib.videoimageshot.grabber.Grabber;
import cc.eguid.cv.corelib.videoimageshot.util.Console;

/**
 * Based on the latest features of ffmpeg4.0
 * @author eguid
 *
 */
public abstract class GrabberTemplate4 implements Grabber{

	/*
	 * Register all formats and codecs
	 */
	static {
		avformat_network_init();
		av_log_set_level(AV_LOG_DEBUG);//set log level
	}
	
	protected Integer width;//image width
	protected Integer height;//image height
	private int srcWidth;//video source width
	private int srcHeight;//video source height
	
	private AVFormatContext pFormatCtx;//视频格式上下文
	private AVCodecContext pCodecCtx;//编解码上下文
	private int videoStreamIndex;//视频流所在的通道
	
	private int align=1;// The assumed linesize alignment
	
	private AVPacket packet;//临时的未解码的视频帧数据
	private AVFrame pFrame;//临时的视频帧解码后的图像像素数据，默认yuv420
	
	private SwsContext sws_ctx;//图像缩放和像素格式转换上下文
	private AVFrame outFrameRGB;//用于存储转换后的RGB像素数据，默认转换成RGB
 
	public GrabberTemplate4() {
		super();
	}

	public GrabberTemplate4(Integer width, Integer height) {
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
	protected AVFormatContext findStreamInfo(AVFormatContext formatCtx,AVDictionary options) throws StreamInfoNotFoundException{
		if (avformat_find_stream_info(formatCtx, options==null?(AVDictionary)null:options)>= 0) {
			return formatCtx;
		}
		throw new StreamInfoNotFoundException("Didn't retrieve stream information");
	}
	
	/**
	 * 查找视频通道
	 * @param pFormatCtx
	 * @return
	 */
	protected int findVideoStreamIndex(AVFormatContext formatCtx) {
		int size=formatCtx.nb_streams();
		for (int i = 0; i < size; i++) {
			AVStream stream=formatCtx.streams(i);
			AVCodecParameters codec=stream.codecpar();
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
	protected int findAudioStreamIndex(AVFormatContext formatCtx) {
		int size=formatCtx.nb_streams();
		for (int i = 0; i < size; i++) {
			AVStream stream=formatCtx.streams(i);
			AVCodecParameters codec=stream.codecpar();
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
	protected AVCodecParameters findVideoParameters(AVFormatContext formatCtx ,int videoStreamIndex)throws StreamNotFoundException {
		if(videoStreamIndex >=0) {
			// Get a pointer to the codec context for the video stream
			AVStream stream=formatCtx.streams(videoStreamIndex);
//			AVCodecContext pCodecCtx = stream.codec();
			AVCodecParameters codecParam=stream.codecpar();
			return codecParam;
		}
		//if no stream,throws Excetion.
		throw new StreamNotFoundException("Didn't open video file");
	}
	
	/**
	 * 查找并尝试打开解码器
	 * @return 
	 */
	protected AVCodecContext findAndOpenCodec(AVFormatContext formatCtx ,int videoStreamIndex) {
		// Find codec param
		AVCodecParameters codecParameters=findVideoParameters(formatCtx ,videoStreamIndex);

		// Find the decoder for the video stream
		AVCodec codec = avcodec_find_decoder(codecParameters.codec_id());

		if (codec == null) {
			Console.err("Codec not found!");
			throw new CodecNotFoundExpception("Codec not found!");
		}
		AVDictionary optionsDict = null;
		AVCodecContext codecCtx = avcodec_alloc_context3(codec);

		//convert to codecContext
		if(avcodec_parameters_to_context( codecCtx, codecParameters)<0) {
			Console.err("Could not convert parameter to codecContext!");
			throw new CodecNotFoundExpception("Could not convert parameter to codecContext!"); // Could not open codec
		}

		// Open codec
		if (avcodec_open2(codecCtx, codec, optionsDict) < 0) {
			Console.err("Could not open codec!");
			throw new CodecNotFoundExpception("Could not open codec!"); // Could not open codec
		}
		
		return codecCtx;
	}
	
	/**
	 * 查找并尝试打开解码器
	 * @return 
	 */
	protected AVCodecContext findAndOpenCodec(AVCodecContext codecCtx) {
		// Find the decoder for the video stream
		AVCodec pCodec = avcodec_find_decoder(codecCtx.codec_id());
		if (pCodec == null) {
			Console.err("Codec not found!");
			throw new CodecNotFoundExpception("Codec not found!");
		}
		AVDictionary optionsDict = null;
		// Open codec
		if (avcodec_open2(codecCtx, pCodec, optionsDict) < 0) {
			Console.err("Could not open codec!");
			throw new CodecNotFoundExpception("Could not open codec!"); // Could not open codec
		}
		return codecCtx;
	}
	
	/**
	 * free all struct
	 */
	private void freeAndClose() {
		av_packet_unref(packet);// Free the packet that was allocated by av_read_frame
		
		av_free(pFrame);// Free the YUV frame
		av_free(outFrameRGB);// Free the RGB image
		
		sws_freeContext(sws_ctx);//Free SwsContext
		avcodec_close(pCodecCtx);// Close the codec
		avformat_close_input(pFormatCtx);// Close the video file
	}
	
	/**
	 * 开始转码之前的一些初始化操作
	 * @param url
	 * @param fmt
	 * @return
	 */
	private boolean initGrabber(String url,int fmt) {

		// Open video file
		pFormatCtx=openInput(url);
		
		// Find video info
		findStreamInfo(pFormatCtx,null);
		
		// Find a video stream
		videoStreamIndex=findVideoStreamIndex(pFormatCtx);
		
		// Find the decoder for the video stream
		pCodecCtx= findAndOpenCodec(pFormatCtx,videoStreamIndex);
		
		//set image size
		srcWidth = pCodecCtx.width();
		srcHeight = pCodecCtx.height();
		
		//if width/height is null,use the width/height of video source as the default
		if(width==null||height==null) {
			width=srcWidth;
			height=srcHeight;
		}
		
		//scaling/conversion operations by using sws_scale().
		DoublePointer param=null;
		sws_ctx = sws_getContext(srcWidth, srcHeight, pCodecCtx.pix_fmt(), width, height,fmt, SWS_FAST_BILINEAR, null, null, param);
		
		packet = new AVPacket();
		
		// Allocate video frame
		pFrame = av_frame_alloc();
		
		// Allocate an AVFrame structure
		outFrameRGB = av_frame_alloc();
		outFrameRGB.width(width);
		outFrameRGB.height(height);
		outFrameRGB.format(fmt);
		
		return true;
	}
	
	/**
	 * 抓取视频帧（默认跳过音频帧和空帧）
	 * @param url
	 * @param fmt - 像素格式，比如AV_PIX_FMT_BGR24
	 * @return
	 * @throws IOException
	 */
	public byte[] grabVideoFrame(String url,int fmt) throws IOException {
		//如果初始化失败
		if(!initGrabber(url,fmt)) {
			return null;
		}
		
		// Determine required buffer size and allocate buffer
		BytePointer buffer =new BytePointer(av_malloc(av_image_get_buffer_size(fmt, width, height, align)));
		
		// Assign appropriate parts of buffer to image planes in pFrameRGB.
		av_image_fill_arrays(outFrameRGB.data(),outFrameRGB.linesize(),buffer, fmt, width, height, align);
	
		try {
			while (av_read_frame(pFormatCtx, packet) == 0) {
				// Is this a packet from the video stream?
				if (packet.stream_index() == videoStreamIndex) {
					//把需要解码的视频帧送进解码器
					//Send video packet to be decoding
					if(avcodec_send_packet(pCodecCtx, packet)==0) {
						//Receive decoded video frame
						if(avcodec_receive_frame(pCodecCtx, pFrame)==0) {
							//Sucesss.
							// Convert the image from its native format to BGR
							sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, srcHeight, outFrameRGB.data(),outFrameRGB.linesize());
							//Convert BGR to ByteBuffer
							return saveFrame(outFrameRGB, width, height);
						}
					}
				}
				// Free the packet that was allocated by av_read_frame
				av_packet_unref(packet);
			}
			//读取错误或读取完成
			return null;
		}finally {
			av_free(buffer);//Don't free buffer?	Of course not!
			freeAndClose();
		}
	}
	
	/**
	 * 抓取视频帧（默认跳过音频帧和空帧）
	 * @param url
	 * @param fmt - 像素格式，比如AV_PIX_FMT_BGR24
	 * @param sum-连续截图总数量
	 * @param fps -截图帧率
	 * <p>
	 * <li>每秒截图数量，如果该帧率为25，则1000/25=40毫秒截图一次，如果该值为5，则每1000/5=200毫秒截图一次</li>
	 * <li>如果视频没有时间戳只支持顺序帧，假设视频帧率为30，该值为3，则30/3，每隔10帧获取一帧图像</li>
	 * </p>
	 * @return
	 * @throws IOException
	 */
	public byte[][] grabVideoFrame(String url,int fmt,int sum,int interval) throws IOException {

		//初始化存储数组
		byte[][] byteBuffers=null;
		if(sum>0) {
			byteBuffers=new byte[sum][];
		}else {
			return byteBuffers;
		}
		//如果初始化失败
		if(!initGrabber(url,fmt)) {
			return null;
		}
		try {
			for(int i=0,num=0 ;num<sum&&av_read_frame(pFormatCtx, packet) == 0;){
				
				// Is this a packet from the video stream?
				if (packet.stream_index() == videoStreamIndex) {
					if(i%interval==0) {
						//把需要解码的视频帧送进解码器
						//Send video packet to be decoding
						if(avcodec_send_packet(pCodecCtx, packet)==0) {
							//Receive decoded video frame
							if(avcodec_receive_frame(pCodecCtx, pFrame)==0) {
								// Determine required buffer size and allocate buffer
								BytePointer buffer =new BytePointer(av_malloc(av_image_get_buffer_size(fmt, width, height, align)));
								
								// Assign appropriate parts of buffer to image planes in pFrameRGB.
								av_image_fill_arrays(outFrameRGB.data(),outFrameRGB.linesize(),buffer, fmt, width, height, align);
								
								//Sucesss.
								// Convert the image from its native format to BGR
								sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, srcHeight, outFrameRGB.data(),outFrameRGB.linesize());
								//Convert BGR to ByteBuffer
								byteBuffers[num++]= saveFrame(outFrameRGB, width, height);
								
								av_free(buffer);//free buffer
							}
						}
					}
					i++;//Received video packet size.
				}
				// Free the packet that was allocated by av_read_frame
				av_packet_unref(packet);
			}
			//读取错误或读取完成
			return byteBuffers;
		}finally {
			freeAndClose();
		}
	}
	
}
