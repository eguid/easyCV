package cc.eguid.cv.videoRecorder;

import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avutil.*;
import static org.bytedeco.javacpp.swscale.*;

import java.util.Map.Entry;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVOutputFormat;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil.AVDictionary;

/**
 * 推流器
 * 
 * @author Administrator
 *
 */
public class TestPusher {
	static {
		// Register all formats and codecs
		av_register_all();
		avformat_network_init();
	}

	// 输入对应一个AVFormatContext，输出对应一个AVFormatContext
	private AVOutputFormat ofmt = null;
	// （Input AVFormatContext and Output AVFormatContext）
	private AVFormatContext ifmt_ctx = new AVFormatContext(null);
	private AVFormatContext ofmt_ctx = new AVFormatContext(null);

	private AVPacket pkt = new AVPacket();

	/**
	 * 打开视频流
	 * 
	 * @param url
	 *            -url
	 * @return
	 * @throws FileNotOpenException
	 */
	protected AVFormatContext openInput(String url) throws FileNotOpenException {
		AVFormatContext pFormatCtx = new AVFormatContext(null);
		if (avformat_open_input(pFormatCtx, url, null, null) == 0) {
			return pFormatCtx;
		}
		throw new FileNotOpenException("Didn't open video file");
	}

	/**
	 * 检索流信息
	 * 
	 * @param pFormatCtx
	 * @return
	 */
	protected AVFormatContext findStreamInfo(AVFormatContext pFormatCtx) throws StreamInfoNotFoundException {
		// 解决rtsp默认udp丢帧导致检索时间过长问题
		AVDictionary options = new AVDictionary();
		av_dict_set(options, "rtsp_transport", "tcp", 0);
		// 解决rtmp检索时间过长问题
		// 限制最大读取缓存
		pFormatCtx.probesize(500 * 1024);// 设置500k能保证高清视频也能读取到关键帧
		// 限制avformat_find_stream_info最大持续时长，设置成3秒
		pFormatCtx.max_analyze_duration(3 * AV_TIME_BASE);
		if (avformat_find_stream_info(pFormatCtx, options) >= 0) {
			return pFormatCtx;
		}
		throw new StreamInfoNotFoundException("Didn't retrieve stream information");
	}

	/**
	 * 获取第一帧视频位置
	 * 
	 * @param pFormatCtx
	 * @return
	 */
	protected int findVideoStreamIndex(AVFormatContext pFormatCtx) {
		int i = 0, videoStream = -1;
		for (i = 0; i < pFormatCtx.nb_streams(); i++) {
			AVStream stream = pFormatCtx.streams(i);
			AVCodecContext codec = stream.codec();
			if (codec.codec_type() == AVMEDIA_TYPE_VIDEO) {
				videoStream = i;
				break;
			}
		}
		return videoStream;
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

	public void start() {
//		 AVDictionary metadata = new AVDictionary(null);
//        for (Entry<String, String> e : this.metadata.entrySet()) {
//            av_dict_set(metadata, e.getKey(), e.getValue(), 0);
//        }
//        /* write the stream header, if any */
//        avformat_write_header(oc.metadata(metadata), options);
//        av_dict_free(options);
	}
	
	public void push() {
		
	}
	public boolean push(String in_filename, String out_filename) {
		int videoindex = -1;

		ifmt_ctx = openInput(in_filename);
		ifmt_ctx = findStreamInfo(ifmt_ctx);
		videoindex = findVideoStreamIndex(ifmt_ctx);
		//输入流的编解码器
		AVCodecContext inCodecCtx =findVideoStream(ifmt_ctx,videoindex);
		// Find the decoder for the video stream
		inCodecCtx= findAndOpenCodec(inCodecCtx);
		// 打印
		 av_dump_format(ifmt_ctx, 0, in_filename, 0);
		 //初始化输出流上下文
		 avformat_alloc_output_context2(ofmt_ctx, null,null,out_filename); 
		 if(ofmt_ctx.isNull()) {
			// 输出（Output）
			if (avformat_alloc_output_context2(ofmt_ctx, null, "flv", out_filename) < 0) { // RTMP或flv
				//初始化失败
				return false;
			}
		 }
		 //把输入流的编解码器复制给输出流
		 av_dump_format(ofmt_ctx, 0, out_filename, 1);
		 AVStream out_stream = avformat_new_stream(ofmt_ctx,null);
		 if(out_stream==null) {//fail to create new stream
			 return false;
		 }
		 av_dump_format(ofmt_ctx, 0, out_filename, 1);
		 
//		 avcodec_parameters_copy(out_stream.codecpar(), ifmt_ctx.streams(videoindex).codecpar());
//		 if(avcodec_parameters_from_context(out_stream.codecpar(), inCodecCtx)<0) {
//			 return false;
//		 }
		 if (avcodec_copy_context(out_stream.codec(), inCodecCtx) < 0) {
			return false;
		 }

		// Dump Format------------------
		 av_dump_format(ofmt_ctx, 0, out_filename, 1);
//		 System.out.println("尝试打开输入文件");
		/* open the output file, if needed */
//		if(avio_open2(ifmt_ctx.pb(), in_filename, AVIO_FLAG_READ_WRITE, null, null) < 0) {
//			 System.out.println("打开输入流失败");
//			return false;
//		}
//		 System.out.println("已经打开了输入流");
		 //打开输出URL（Open output URL）
//		 System.out.println("尝试打开输出流");
//		 AVIOContext pb = new AVIOContext(null);
//		 if(avio_open(ofmt_ctx.pb(), out_filename, AVIO_FLAG_WRITE)<0) {
//			 System.out.println("打开输出流失败");
//			 //fail
//			 return false;
//		 }
		 System.out.println("已经打开输出流");
		//
		// 写文件头（Write file header）
//		AVDictionary metadata = new AVDictionary(null);
		/* write the stream header, if any */
		System.out.println("写入头");
//		AVDictionary options = new AVDictionary(null);
		if (avformat_write_header(ofmt_ctx, (PointerPointer<?>)null) < 0) {
			return false;
		}
		System.out.println("写入头完成");
		AVStream in_stream;
		try {
			for (long errorindex=0; av_read_frame(ifmt_ctx, pkt) >= 0;) {
				in_stream = ifmt_ctx.streams(pkt.stream_index());
				out_stream = ofmt_ctx.streams(pkt.stream_index());
				pkt.dts(av_rescale_q_rnd(pkt.dts(), in_stream.time_base(), out_stream.time_base(),(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX)));
				pkt.pos(-1);
				if (av_interleaved_write_frame(ofmt_ctx, pkt) < 0) {
					// 错误计数
					errorindex++;
				}
				av_free_packet(pkt);
			}
			return true;
		} finally {
			// 写文件尾（Write file trailer）
			av_write_trailer(ofmt_ctx);
			avformat_close_input(ifmt_ctx);
			/* close output */
			avio_close(ofmt_ctx.pb());
			avformat_free_context(ofmt_ctx);
		}
	}

	public static void main(String[] args) {
		// in_filename = "cuc_ieschool.mov";
		// in_filename = "cuc_ieschool.mkv";
		// in_filename = "cuc_ieschool.ts";
		// in_filename = "cuc_ieschool.mp4";
		// in_filename = "cuc_ieschool.h264";
		String in_filename = "rtmp://media3.sinovision.net:1935/live/livestream";// 输入URL（Input file URL）
		// in_filename = "shanghai03_p.h264";

		String out_filename = "rtmp://106.14.182.20:1935/rtmp/tomcat";// 输出 URL（Output URL）[RTMP]
		// out_filename = "rtp://233.233.233.233:6666";//输出 URL（Output URL）[UDP]

		new TestPusher().push(in_filename, out_filename);
	}
}
