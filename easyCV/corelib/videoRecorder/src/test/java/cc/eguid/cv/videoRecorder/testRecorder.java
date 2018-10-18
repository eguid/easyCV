package cc.eguid.cv.videoRecorder;

import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avutil.*;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVCodecParameters;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVOutputFormat;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil.AVDictionary;
import org.bytedeco.javacpp.avutil.AVRational;
import org.bytedeco.javacv.FrameRecorder.Exception;

/**
 * 录制器必须原型创建，不能单例
 * 
 * @author eguid
 *
 */
public class testRecorder {

	static {
		avcodec_register_all();
		avformat_network_init();
	}
	private final static int PROBESIZE = 500 * 1024;
	private final static int MAX_ANALYZE_DURATION = 3 * AV_TIME_BASE;

	// 输入参数
	private AVFormatContext in_fc;// 输入格式上下文
	// 视频编码id，帧率，音频编码id
	private int out_videoCodecId = 0, out_audioCodecId=0;// 输入视频编码id
	private AVStream in_videoStream = null;// 输入视频流
	private AVStream in_audioStream = null;// 输入音频流
	private AVCodecContext in_audioCodec;// 输入的音频编码器
	private AVCodecContext in_videoCodec;// 输入的视频编码器
	private AVCodecParameters in_videoCodecpar = null;// 输入流视频编码器参数
	private AVCodecParameters in_audioCodecpar = null;// 输入流音频编码器参数
	// private AVCodecContext incodec=null;//输入流编解码上下文

	// 输出参数
	private AVOutputFormat of;// 输出格式
	private AVFormatContext fc;// 输出格式上下文

	// 视频参数
	private AVCodecContext out_videoCodec=new AVCodecContext();// 输出编解码器上下文
	private AVStream out_videoStream; /* 视频输出流 */

	protected String out_filename;// 视频名称
	protected int imageWidth;
	protected int imageHeight;
	protected String format;// 格式
	protected int pixelFormat = AV_PIX_FMT_NONE;// 像素格式
	protected int maxBFrames = -1;// 最大B帧
	protected int maxDelay = -1;// 最大延迟
	protected int frameRate = 25;// 帧率
	protected int videoBitrate;// 视频比特率
	protected int videoQuality;// 视频质量
	protected int gopSize = 2;// 关键帧间隔（默认低延迟关键帧为2；假如帧率为25，gop为2，则一个gop帧等于50帧图像帧）

	// 音频参数
	private AVCodecContext out_audioCodec;// 音频编码上下文
	private AVStream out_audioStream;// 输出音频流
//	private AVCodec out_audioCodec;
	AVCodec out_Codec = null;

	protected String audioCodecName = null;// 音频编码器名（用于手动设置）
	protected int audioCodec;// 音频编码器
	protected int audioChannels=2;// 音频通道数量
	protected int audioBitrate;// 音频比特率
	protected int sampleRate;// 采样率
	protected int audioQuality;// 音频质量
	protected int sampleFormat = AV_SAMPLE_FMT_NONE;// 音频采样格式

	/* 局部参数 */
	int ret;
	int frame_index=0;//帧计数
	int videoindex =-1,audioindex=-1;//视频帧和音频帧位置
	long start_time = 0;//计时

	/**
	 * 开始
	 * 
	 * @param filename
	 *            -url或文件地址
	 * @param imageWidth
	 *            -宽度
	 * @param imageHeight
	 *            -高度
	 * @param frameRate
	 *            -帧率，默认25
	 * @param gopSize
	 *            -关键帧间隔，默认2（最小延迟优化）
	 * @return
	 * @throws Exception
	 */
	public testRecorder start(String filename, String format, int imageWidth, int imageHeight, int frameRate, int gopSize)
			throws Exception {
		this.format = format;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.frameRate = frameRate;
		this.gopSize = gopSize;
		this.out_filename = filename;
		start();
		return this;
	}

	/**
	 * 根据文件名和格式查找对应的AVOutputFormat，如果没找到，返回null
	 * 
	 * @param out_filename
	 * @param format
	 * @return
	 */
	private AVOutputFormat findOutFormat(String filename, String format) {
		AVOutputFormat of = null;
		if ((of = av_guess_format(format, filename, null)) == null) {
			int index = -1;
			if ((index = filename.indexOf("://")) > 0) {
				format = filename.substring(0, index);
			}
			if ((of = av_guess_format(format, filename, null)) == null) {
				return null;
			}
		}
		return of;
	}

	/**
	 * 从输入音视频流获取音视频格式
	 * 
	 * @param avfc
	 */
	private void getInputParam(AVFormatContext avfc) {
		// get input video and audio stream indices from ifmt_ctx
		for (int idx = 0; idx < avfc.nb_streams(); idx++) {
			System.err.println("读取流参数："+idx);
			AVStream stream = avfc.streams(idx);
			AVCodecParameters codecpar = stream.codecpar();
			if (codecpar.codec_type() == AVMEDIA_TYPE_VIDEO) {
				AVRational frame_rate = stream.r_frame_rate();
				if (frame_rate.num() != AV_NOPTS_VALUE && frame_rate.den() != 0) {
					this.frameRate = (frame_rate.num()) / (frame_rate.den());
				}
				this.videoindex=idx;
				this.in_videoCodecpar = codecpar;
				this.in_videoStream = stream;
			} else if (codecpar.codec_type() == AVMEDIA_TYPE_AUDIO) {
				this.in_audioCodecpar = codecpar;
				this.in_audioStream = stream;
				this.audioindex=idx;
			}
		}
	}

	/**
	 * 开始一些准备工作（用于初始化一些配置参数）
	 * 
	 * @param filename
	 *            - url或文件地址
	 * @throws Exception
	 */
	private void start() throws Exception {
		/* auto detect the output format from the name.根据名称自动检测输出格式 */
		of = findOutFormat(out_filename, format);
		if (of == null) {
			throw new Exception("未知的视频格式");
		}
		/* allfcate the output media context 初始化输出格式上下文 */
		fc = new AVFormatContext();
		// 输出（Output）
		if (avformat_alloc_output_context2(fc, null, format, out_filename) < 0) {
			throw new Exception("初始化输出格式失败");
		}
		fc.oformat(of);
		fc.url().putString(out_filename);// 替换老的filename方法
		fc.max_delay(maxDelay);
		/*
		 * add the audio and video streams using the format codecs and initialize the
		 * codecs
		 */
		 av_dump_format(fc, 0, out_filename, 1);
		// 参数设置
		// 视频编码处理
		processVideoConfig(format);
		// 音频编码处理
		processAudioConfig(format);
		/*
		 * now that all the parameters are set, we can open the audio and video codecs
		 * and allocate the necessary encode buffers
		 * 所有参数都设置了，我们能够打开音频和视频编码器，并且分配必要的编码缓冲区
		 */
		if (out_videoStream != null) {
			System.err.println("开始处理视频");
			AVDictionary options = new AVDictionary(null);
			if (videoQuality >= 0) {
				av_dict_set(options, "crf", "" + videoQuality, 0);
			}
			if(out_videoCodec!=null) {
				/* open the codec */
				if (avcodec_open2(out_videoCodec, out_videoCodec.codec(), options) < 0) {
					av_dict_free(options);
					throw new Exception("avcodec_open2() error " + ret + ": Could not open video codec.");
				}
			}
			
			av_dict_free(options);

			/* copy the stream parameters to the muxer */
			if (in_videoStream != null) {// 从输入流复制视频编解码器参数到输出流
				if ((ret = avcodec_parameters_from_context(in_videoCodecpar, out_videoCodec)) < 0) {
					throw new Exception("avcodec_parameters_from_context() error: Could not copy the video stream parameters.");
				}
			}
			AVDictionary metadata = new AVDictionary(null);
			out_videoStream.metadata(metadata);
			System.err.println("处理完视频");
		}

		if (out_audioStream != null) {
			System.err.println("开始处理音频");
			AVDictionary options = new AVDictionary(null);
			if (audioQuality >= 0) {
				av_dict_set(options, "crf", "" + audioQuality, 0);
			}
			
			/* open the codec */
			if (avcodec_open2(out_audioCodec, out_Codec, options) < 0) {
				av_dict_free(options);
				throw new Exception("avcodec_open2() error " + ret + ": Could not open audio codec.");
			}
			av_dict_free(options);

			int audio_outbuf_size = 256 * 1024;
			// BytePointer audio_outbuf = new BytePointer(av_malloc(audio_outbuf_size));
			int audio_input_frame_size;
			/*
			 * ugly hack for PCM codecs (will be removed ASAP with new PCM support to
			 * compute the input frame size in samples
			 */
			if (out_audioCodec.frame_size() <= 1) {
				audio_outbuf_size = AV_INPUT_BUFFER_MIN_SIZE;
				audio_input_frame_size = audio_outbuf_size / out_audioCodec.channels();
				switch (out_audioCodec.codec_id()) {
				case AV_CODEC_ID_PCM_S16LE:
				case AV_CODEC_ID_PCM_S16BE:
				case AV_CODEC_ID_PCM_U16LE:
				case AV_CODEC_ID_PCM_U16BE:
					audio_input_frame_size >>= 1;
					break;
				default:
					break;
				}
			} else {
				audio_input_frame_size = out_audioCodec.frame_size();
			}
			// int bufferSize = audio_input_frame_size * audio_c.bits_per_raw_sample()/8 *
			// audio_c.channels();
			int planes = av_sample_fmt_is_planar(out_audioCodec.sample_fmt()) != 0 ? (int) out_audioCodec.channels() : 1;
			int data_size = av_samples_get_buffer_size((IntPointer) null, out_audioCodec.channels(),
					audio_input_frame_size, out_audioCodec.sample_fmt(), 1) / planes;
			BytePointer[] samples_out = new BytePointer[planes];
			for (int i = 0; i < samples_out.length; i++) {
				samples_out[i] = new BytePointer(av_malloc(data_size)).capacity(data_size);
			}
			// Pointer[] samples_in = new Pointer[AVFrame.AV_NUM_DATA_POINTERS];
			// PointerPointer samples_in_ptr = new
			// PointerPointer(AVFrame.AV_NUM_DATA_POINTERS);
			// PointerPointer samples_out_ptr = new
			// PointerPointer(AVFrame.AV_NUM_DATA_POINTERS);

			/* copy the stream parameters to the muxer */
			// 复制音频流数据
			if ((ret = avcodec_parameters_from_context(out_audioStream.codecpar(), out_audioCodec)) < 0) {
				throw new Exception( "avcodec_parameters_from_context() error: Could not copy the audio stream parameters.");
			}

			AVDictionary metadata = new AVDictionary(null);
			out_audioStream.metadata(metadata);
			System.err.println("处理完音频");
		}
		av_dump_format(fc, 0, out_filename, 1);
//		if(avio_open(fc.pb(), out_filename, AVIO_FLAG_WRITE)<0) {
//			 System.out.println("打开输出流失败");
//			 //fail
//			 throw new Exception("avio_open() error " + ret + ": Could not open audio codec.");
//		 }
		System.err.println("音视频预处理完毕，准备写入头信息");
		AVDictionary options = new AVDictionary(null);
		AVDictionary metadata = new AVDictionary(null);
		/* write the stream header, if any */
		avformat_write_header(fc.metadata(metadata), options);
		av_dict_free(options);
		System.err.println("写出头信息成功");
		av_dump_format(fc, 0, out_filename, 1);
	}

	/**
	 * 处理视频配置参数
	 * 
	 * @param format_name
	 * @param inpVideoStream
	 * @throws Exception
	 */
	private void processVideoConfig(String format_name)
			throws Exception {
		if (in_videoStream != null) {
			in_videoCodec = in_videoStream.codec();
		}

		// 视频帧处理start
		if (imageWidth > 0 && imageHeight > 0) {
			if (out_videoCodecId != 0) {
				of.video_codec(out_videoCodecId);
			} else if ("flv".equals(format_name)) {
				System.err.println("视频必到这里");
				of.video_codec(AV_CODEC_ID_FLV1);
			} else if ("mp4".equals(format_name)) {
				of.video_codec(AV_CODEC_ID_MPEG4);
			} else if ("3gp".equals(format_name)) {
				of.video_codec(AV_CODEC_ID_H263);
			} else if ("avi".equals(format_name)) {
				of.video_codec(AV_CODEC_ID_HUFFYUV);
			}

			/* find the video encoder */
			AVCodec videoCodec = null;
			if ((videoCodec = avcodec_find_encoder(of.video_codec())) == null) {
				throw new Exception("avcodec_find_encoder() error: Video codec not found.");
			}
			of.video_codec(videoCodec.id());

			AVRational frame_rate = av_d2q(frameRate, 1001000);
			AVRational supported_framerates = videoCodec.supported_framerates();
			if (supported_framerates != null) {
				int idx = av_find_nearest_q_idx(frame_rate, supported_framerates);
				frame_rate = supported_framerates.position(idx);
			}

			/* add a video output stream */
			if ((out_videoStream = avformat_new_stream(fc, null)) == null) {
				throw new Exception("avformat_new_stream() error: Could not allocate video stream.");
			}
			// 初始化上下文
			if ((out_videoCodec = avcodec_alloc_context3(videoCodec)) == null) {
				throw new Exception("avcodec_alloc_context3() error: Could not allocate video encoding context.");
			}
			double aspectRatio = 0;
//			if (in_videoStream != null) {
//				System.err.println("复制输入流的视频信息");
//				// 复制视频编码信息
//				if ((ret = avcodec_copy_context(out_videoStream.codec(), in_videoStream.codec())) < 0) {
//					throw new Exception("avcodec_copy_context() error:\tFailed to copy context from input to output stream codec context");
//				}
//				out_videoCodec.codec_id(in_videoCodec.codec_id());
//				videoBitrate = (int) in_videoCodec.bit_rate();
//				pixelFormat = in_videoCodec.pix_fmt();
//				aspectRatio = in_videoCodec.sample_aspect_ratio().den() / this.in_videoCodec.sample_aspect_ratio().den()* 1.d;
//				videoQuality = in_videoCodec.global_quality();
//				out_videoCodec.codec_tag(0);
//			}

			out_videoCodec.codec_type(AVMEDIA_TYPE_VIDEO);

			/* put sample parameters */
			out_videoCodec.bit_rate(videoBitrate);
			/*
			 * resolution must be a multiple of two. Scale height to maintain the aspect
			 * ratio.
			 */
			if (imageWidth % 2 == 1) {
				int roundedWidth = imageWidth + 1;
				imageHeight = (roundedWidth * imageHeight + imageWidth / 2) / imageWidth;
				imageWidth = roundedWidth;
			}
			out_videoCodec.width(imageWidth);
			out_videoCodec.height(imageHeight);
			if (aspectRatio > 0) {
				AVRational r = av_d2q(aspectRatio, 255);
				out_videoCodec.sample_aspect_ratio(r);
				out_videoStream.sample_aspect_ratio(r);
			}
			/*
			 * time base: this is the fundamental unit of time (in seconds) in terms of
			 * which frame timestamps are represented. for fixed-fps content, timebase
			 * should be 1/framerate and timestamp increments should be identically 1.
			 */
			out_videoCodec.time_base(av_inv_q(frame_rate));
			out_videoStream.time_base(av_inv_q(frame_rate));
			// 关键帧间隔，间隔越小画面越好，但码流也会越大（低延迟建议2）
			if (gopSize >= 0) {
				out_videoCodec.gop_size(gopSize); /* emit one intra frame every gopSize frames at most */
			}
			// 设置视频质量
			if (videoQuality >= 0) {
				out_videoCodec.flags(out_videoCodec.flags() | AV_CODEC_FLAG_QSCALE);
				out_videoCodec.global_quality((int) Math.round(FF_QP2LAMBDA * videoQuality));
			}

			if (pixelFormat != AV_PIX_FMT_NONE) {
				out_videoCodec.pix_fmt(pixelFormat);
			} else if (out_videoCodec.codec_id() == AV_CODEC_ID_RAWVIDEO || out_videoCodec.codec_id() == AV_CODEC_ID_PNG
					|| out_videoCodec.codec_id() == AV_CODEC_ID_HUFFYUV || out_videoCodec.codec_id() == AV_CODEC_ID_FFV1) {
				out_videoCodec.pix_fmt(AV_PIX_FMT_RGB32); // appropriate for common lossless formats
			} else if (out_videoCodec.codec_id() == AV_CODEC_ID_JPEGLS) {
				out_videoCodec.pix_fmt(AV_PIX_FMT_BGR24);
			} else if (out_videoCodec.codec_id() == AV_CODEC_ID_MJPEG || out_videoCodec.codec_id() == AV_CODEC_ID_MJPEGB) {
				out_videoCodec.pix_fmt(AV_PIX_FMT_YUVJ420P);
			} else {
				out_videoCodec.pix_fmt(AV_PIX_FMT_YUV420P); // lossy, but works with about everything
			}

			if (out_videoCodec.codec_id() == AV_CODEC_ID_MPEG2VIDEO) {
				/* just for testing, we also add B frames */
				out_videoCodec.max_b_frames(2);
			} else if (out_videoCodec.codec_id() == AV_CODEC_ID_MPEG1VIDEO) {
				/*
				 * Needed to avoid using macroblocks in which some coeffs overflow. This does
				 * not happen with normal video, it just happens here as the motion of the
				 * chroma plane does not match the luma plane.
				 */
				out_videoCodec.mb_decision(2);
			} else if (out_videoCodec.codec_id() == AV_CODEC_ID_H263) {
				// H.263 does not support any other resolution than the following
				if (imageWidth <= 128 && imageHeight <= 96) {
					out_videoCodec.width(128).height(96);
				} else if (imageWidth <= 176 && imageHeight <= 144) {
					out_videoCodec.width(176).height(144);
				} else if (imageWidth <= 352 && imageHeight <= 288) {
					out_videoCodec.width(352).height(288);
				} else if (imageWidth <= 704 && imageHeight <= 576) {
					out_videoCodec.width(704).height(576);
				} else {
					out_videoCodec.width(1408).height(1152);
				}
			} else if (out_videoCodec.codec_id() == AV_CODEC_ID_H264) {
				// default to constrained baseline to produce content that plays back on
				// anything,
				// without any significant tradeoffs for most use cases
				out_videoCodec.profile(AVCodecContext.FF_PROFILE_H264_CONSTRAINED_BASELINE);
			}

			// some formats want stream headers to be separate
			if ((of.flags() & AVFMT_GLOBALHEADER) != 0) {
				out_videoCodec.flags(out_videoCodec.flags() | AV_CODEC_FLAG_GLOBAL_HEADER);
			}

			if ((videoCodec.capabilities() & AV_CODEC_CAP_EXPERIMENTAL) != 0) {
				out_videoCodec.strict_std_compliance(AVCodecContext.FF_COMPLIANCE_EXPERIMENTAL);
			}

			if (maxBFrames >= 0) {
				out_videoCodec.max_b_frames(maxBFrames);
				out_videoCodec.has_b_frames(maxBFrames == 0 ? 0 : 1);
			}
		}
	}

	/***
	 * 预处理输出音频参数
	 * 
	 * @param format_name
	 * @param inpAudioStream
	 * @throws Exception
	 */
	private void processAudioConfig(String format_name) throws Exception {
		/*
		 * add an audio output stream
		 */
		if (this.audioChannels > 0) {
			// 是否有确定的音频格式，没有就根据format_name来确定
			if (out_audioCodecId != 0) {
				of.audio_codec(audioCodec);
			} else if ("flv".equals(format_name) || "mp4".equals(format_name) || "3gp".equals(format_name)) {
				System.err.println("必到这里");
				of.audio_codec(AV_CODEC_ID_AAC);
			} else if ("avi".equals(format_name)) {
				of.audio_codec(AV_CODEC_ID_PCM_S16LE);
			}
			/* find the audio encoder */
			if ((out_Codec = avcodec_find_encoder_by_name(audioCodecName)) == null && (out_Codec = avcodec_find_encoder(of.audio_codec())) == null) {
				throw new Exception("avcodec_find_encoder() error: Audio codec not found.");
			}
			of.audio_codec(out_Codec.id());

			if ((in_audioStream = avformat_new_stream(fc, null)) == null) {
				throw new Exception("avformat_new_stream() error: Could not allocate audio stream.");
			}

			if ((out_audioCodec = avcodec_alloc_context3(out_Codec)) == null) {
				throw new Exception("avcodec_alloc_context3() error: Could not allocate audio encoding context.");
			}

			// 复制参数
			if (in_audioStream != null&&out_audioCodec!=null) {
				AVCodecContext in_codec = in_audioStream.codec();
				// 复制输入流参数到输出流
				if (avcodec_copy_context(out_audioCodec, in_codec) < 0) {
					throw new Exception( "avcodec_copy_context() error:\tFailed to copy context from input audio to output audio stream codec context\n");
				}
//				out_audioStream.duration(in_audioStream.duration());
				out_audioCodec.time_base().num(in_audioStream.time_base().num());
				out_audioCodec.time_base().den(in_audioStream.time_base().den());
				this.audioBitrate = (int) in_codec.bit_rate();
				this.sampleRate = in_codec.sample_rate();
				this.audioChannels = in_codec.channels();
				this.sampleFormat = in_codec.sample_fmt();
				this.audioQuality = in_codec.global_quality();
				this.out_audioCodec.codec_tag(0);
			}
			out_audioCodec.codec_type(AVMEDIA_TYPE_AUDIO);
			out_audioCodec.codec_id(of.audio_codec());// 覆盖输出的音频编码格式，防止复制操作改变输出音频格式
			/* put sample parameters */
			out_audioCodec.bit_rate(this.audioBitrate);
			out_audioCodec.sample_rate(this.sampleRate);
			out_audioCodec.channels(this.audioChannels);
			out_audioCodec.channel_layout(av_get_default_channel_layout(this.audioChannels));

			if (sampleFormat != AV_SAMPLE_FMT_NONE) {
				out_audioCodec.sample_fmt(sampleFormat);
			} else {
				// use AV_SAMPLE_FMT_S16 by default, if available
				out_audioCodec.sample_fmt(AV_SAMPLE_FMT_FLTP);
				IntPointer formats = out_audioCodec.codec().sample_fmts();
				for (int i = 0; formats.get(i) != -1; i++) {
					if (formats.get(i) == AV_SAMPLE_FMT_S16) {
						out_audioCodec.sample_fmt(AV_SAMPLE_FMT_S16);
						break;
					}
				}
			}
			out_audioCodec.time_base().num(1).den(sampleRate);
//			out_audioStream.time_base().num(1).den(sampleRate);
			switch (out_audioCodec.sample_fmt()) {
			case AV_SAMPLE_FMT_U8:
			case AV_SAMPLE_FMT_U8P:
				out_audioCodec.bits_per_raw_sample(8);
				break;
			case AV_SAMPLE_FMT_S16:
			case AV_SAMPLE_FMT_S16P:
				out_audioCodec.bits_per_raw_sample(16);
				break;
			case AV_SAMPLE_FMT_S32:
			case AV_SAMPLE_FMT_S32P:
				out_audioCodec.bits_per_raw_sample(32);
				break;
			case AV_SAMPLE_FMT_FLT:
			case AV_SAMPLE_FMT_FLTP:
				out_audioCodec.bits_per_raw_sample(32);
				break;
			case AV_SAMPLE_FMT_DBL:
			case AV_SAMPLE_FMT_DBLP:
				out_audioCodec.bits_per_raw_sample(64);
				break;
			default:
				assert false;
			}
			if (audioQuality >= 0) {
				out_audioCodec.flags(out_audioCodec.flags() | AV_CODEC_FLAG_QSCALE);
				out_audioCodec.global_quality((int) Math.round(FF_QP2LAMBDA * audioQuality));
			}

			// some formats want stream headers to be separate
			if ((of.flags() & AVFMT_GLOBALHEADER) != 0) {
				out_audioCodec.flags(out_audioCodec.flags() | AV_CODEC_FLAG_GLOBAL_HEADER);
			}

			if ((out_Codec.capabilities() & AV_CODEC_CAP_EXPERIMENTAL) != 0) {
				out_audioCodec.strict_std_compliance(AVCodecContext.FF_COMPLIANCE_EXPERIMENTAL);
			}
		}
	}

	/**
	 * 设置媒体源（音视频源）
	 * 
	 * @param url
	 * @return
	 */
	public testRecorder from(String url) {
		in_fc = openInput(url);
		if (in_fc != null) {
			// 解决rtmp检索时间过长问题
			// 限制最大读取缓存
			in_fc.probesize(PROBESIZE);// 设置500k能保证高清视频也能读取到关键帧
			// 限制avformat_find_stream_info最大持续时长，设置成3秒
			in_fc.max_analyze_duration(MAX_ANALYZE_DURATION);
			in_fc = findStreamInfo(in_fc);
			// 如果有确定的输入格式上下文就可以根据此获取对应的音视频编码等数据
			getInputParam(in_fc);
			
		}
		return this;
	}
	
//	/**
//	 * 获取视频位置
//	 * 
//	 * @param pFormatCtx
//	 * @return
//	 */
//	protected int findVideoStreamIndex(AVFormatContext pFormatCtx) {
//		int i = 0, videoStream = -1;
//		for (i = 0; i < pFormatCtx.nb_streams(); i++) {
//			AVStream stream = pFormatCtx.streams(i);
//			AVCodecContext codec = stream.codec();
//			if (codec.codec_type() == AVMEDIA_TYPE_VIDEO) {
//				videoStream = i;
//				break;
//			}
//		}
//		return videoStream;
//	}

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
	 * 检索流信息（rtsp/rtmp检索时间过长问题解决）
	 * 
	 * @param pFormatCtx
	 * @return
	 */
	protected AVFormatContext findStreamInfo(AVFormatContext pFormatCtx) throws StreamInfoNotFoundException {
		if (avformat_find_stream_info(pFormatCtx, (PointerPointer<?>) null) >= 0) {
			return pFormatCtx;
		}
		throw new StreamInfoNotFoundException("Didn't retrieve stream information");
	}

	public testRecorder grabtoPush() throws Exception {
		AVPacket pkt = new AVPacket();
	
		for (int err_index = 0; av_read_frame(in_fc, pkt) >= 0;) {
			if(pkt==null) {//连续读到一定数量空包说明网络故障
				err_index++;
				if(err_index>1000) {
					break;
				}
				continue;
			}
			err_index=0;
			
			AVStream in_stream = in_fc.streams(pkt.stream_index());
			pkt.pts(AV_NOPTS_VALUE);
	  		pkt.pos(-1);
			if (in_stream.codec().codec_type() == AVMEDIA_TYPE_VIDEO && out_videoStream != null) {
	            pkt.stream_index(out_videoStream.index());
	            pkt.duration((int) av_rescale_q(pkt.duration(), in_stream.codec().time_base(), out_videoStream.codec().time_base()));
	            pkt.dts(av_rescale_q_rnd(pkt.dts(), in_stream.time_base(), out_videoStream.time_base(),(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX)));
	            writePacket(AVMEDIA_TYPE_VIDEO, pkt);
	        } else if (in_stream.codec().codec_type() == AVMEDIA_TYPE_AUDIO && out_audioStream != null ) {
	            pkt.stream_index(out_audioStream.index());
	            pkt.duration((int) av_rescale_q(pkt.duration(), in_stream.codec().time_base(), out_audioStream.codec().time_base()));
	            pkt.dts(av_rescale_q_rnd(pkt.dts(), in_stream.time_base(), out_audioStream.time_base(),(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX)));
	            writePacket(AVMEDIA_TYPE_AUDIO, pkt);
	        }
		}
		return this;
	}
	
	 private void writePacket(int mediaType, AVPacket avPacket) throws Exception {
	        AVStream avStream = (mediaType == AVMEDIA_TYPE_VIDEO) ? out_audioStream : (mediaType == AVMEDIA_TYPE_AUDIO) ? out_videoStream : null;
	        if ((ret = av_interleaved_write_frame(fc, avPacket)) < 0) {
                throw new Exception("av_interleaved_write_frame() error " + ret + " while writing interleaved packet.");
            }
	    }

	/**
	 * 释放资源
	 */
	private void release() {
		// av_free(pFrame);// Free the YUV frame
		// avcodec_close(pCodecCtx);// Close the codec

		avformat_close_input(in_fc);// Close the video file
	}

	public static void main(String[] args) throws Exception {
		testRecorder recorder = new testRecorder().from("rtmp://media3.sinovision.net:1935/live/livestream")
				.start("rtmp://106.14.182.20:1935/rtmp/eguid", "flv", 800, 600, 25, 2).grabtoPush();

	}
}
