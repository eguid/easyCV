package cc.eguid.cv.videoRecorder.recorder;

import static org.bytedeco.javacpp.avcodec.av_free_packet;
import static org.bytedeco.javacpp.avcodec.avcodec_close;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_find_decoder;
import static org.bytedeco.javacpp.avcodec.avcodec_open2;
import static org.bytedeco.javacpp.avformat.av_read_frame;
import static org.bytedeco.javacpp.avformat.av_register_all;
import static org.bytedeco.javacpp.avformat.avformat_close_input;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_network_init;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_VIDEO;
import static org.bytedeco.javacpp.avutil.AV_TIME_BASE;
import static org.bytedeco.javacpp.avutil.av_dict_set;
import static org.bytedeco.javacpp.avutil.av_frame_alloc;
import static org.bytedeco.javacpp.avutil.av_free;
import static org.bytedeco.javacpp.swscale.sws_freeContext;
import static org.bytedeco.javacpp.swscale.sws_scale;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil.AVDictionary;
import org.bytedeco.javacpp.avutil.AVFrame;

import cc.eguid.cv.videoRecorder.CodecNotFoundExpception;
import cc.eguid.cv.videoRecorder.FileNotOpenException;
import cc.eguid.cv.videoRecorder.StreamInfoNotFoundException;
import cc.eguid.cv.videoRecorder.StreamNotFoundException;

/**
 * ffmpeg录制器
 * 
 * @author eguid
 *
 */
public class FFmpegRecorder {
	static {
		// Register all formats and codecs
		av_register_all();
		avformat_network_init();
	}
	
	protected int width;//宽度
	protected int height;//高度
	
	private final static int PROBESIZE=500*1024;
	private final static int MAX_ANALYZE_DURATION=3 * AV_TIME_BASE;
    private AVDictionary options = new AVDictionary();
   
	
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
	 * 获取第一帧视频位置
	 * @param pFormatCtx
	 * @return
	 */
	protected int findVideoStreamIndex(AVFormatContext pFormatCtx) {
		int i = 0;
		for (i = 0; i < pFormatCtx.nb_streams(); i++) {
			AVStream stream=pFormatCtx.streams(i);
			AVCodecContext codec=stream.codec();
			if (codec.codec_type() == AVMEDIA_TYPE_VIDEO) {
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
	protected AVCodecContext findVideoStream(AVFormatContext pFormatCtx ,int videoStream)throws StreamNotFoundException {
		if(videoStream >=0) {
			// Get a pointer to the codec context for the video stream
			AVStream stream=pFormatCtx.streams(videoStream);
			AVCodecContext pCodecCtx = stream.codec();
			return pCodecCtx;
		}
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
			System.err.println("Codec not found");
			throw new CodecNotFoundExpception("Codec not found");
		}
		AVDictionary optionsDict = null;
		// Open codec
		if (avcodec_open2(pCodecCtx, pCodec, optionsDict) < 0) {
			System.err.println("Could not open codec");
			throw new CodecNotFoundExpception("Could not open codec"); // Could not open codec
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
//		if(url.indexOf("rtmp")>=0) {
			//解决rtmp检索时间过长问题
		    //限制最大读取缓存
		    pFormatCtx.probesize(PROBESIZE);//设置500k能保证高清视频也能读取到关键帧
		  //限制avformat_find_stream_info最大持续时长，设置成3秒
		    pFormatCtx.max_analyze_duration(MAX_ANALYZE_DURATION);
//		}
		// Retrieve stream information
		pFormatCtx=findStreamInfo(pFormatCtx);
		// Dump information about file onto standard error
		//av_dump_format(pFormatCtx, 0, url, 0);

		//Find a video stream
		int videoStream=findVideoStreamIndex(pFormatCtx);
		AVCodecContext pCodecCtx =findVideoStream(pFormatCtx,videoStream);
		
		// Find the decoder for the video stream
		pCodecCtx= findAndOpenCodec(pCodecCtx);
		// Allocate video frame
		AVFrame pFrame = av_frame_alloc();
		AVPacket packet = new AVPacket();
		int[] frameFinished = new int[1];
		
		// Read frames and save first five frames to disk
		while (av_read_frame(pFormatCtx, packet) >= 0) {
			// Is this a packet from the video stream?
			if (packet.stream_index() == videoStream) {
				// Decode video frame
				avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);
				// Did we get a video frame?
				if (frameFinished != null&&frameFinished[0] != 0) {
					
				}
			}
			// Free the packet that was allocated by av_read_frame
			av_free_packet(packet);
		}
		//ge
		av_free(pFrame);// Free the YUV frame
		avcodec_close(pCodecCtx);// Close the codec
		avformat_close_input(pFormatCtx);// Close the video file
		return null;
	}

	/**
	 * 录像
	 */
	public void record() {

	}
}
