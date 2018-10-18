package cc.eguid.cv.corelib.videoimageshot;

import static org.bytedeco.javacpp.avcodec.av_free_packet;
import static org.bytedeco.javacpp.avcodec.av_new_packet;
import static org.bytedeco.javacpp.avcodec.avcodec_close;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_encode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_find_decoder;
import static org.bytedeco.javacpp.avcodec.avcodec_find_encoder;
import static org.bytedeco.javacpp.avcodec.avcodec_open2;
import static org.bytedeco.javacpp.avcodec.avpicture_fill;
import static org.bytedeco.javacpp.avcodec.avpicture_get_size;
import static org.bytedeco.javacpp.avformat.AVIO_FLAG_READ_WRITE;
import static org.bytedeco.javacpp.avformat.av_dump_format;
import static org.bytedeco.javacpp.avformat.av_guess_format;
import static org.bytedeco.javacpp.avformat.av_read_frame;
import static org.bytedeco.javacpp.avformat.av_register_all;
import static org.bytedeco.javacpp.avformat.av_write_frame;
import static org.bytedeco.javacpp.avformat.av_write_trailer;
import static org.bytedeco.javacpp.avformat.avformat_alloc_context;
import static org.bytedeco.javacpp.avformat.avformat_close_input;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_free_context;
import static org.bytedeco.javacpp.avformat.avformat_network_init;
import static org.bytedeco.javacpp.avformat.avformat_new_stream;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avformat.avformat_write_header;
import static org.bytedeco.javacpp.avformat.avio_close;
import static org.bytedeco.javacpp.avformat.avio_open;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_VIDEO;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_BGR24;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_RGB24;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_YUV420P;
import static org.bytedeco.javacpp.avutil.av_frame_alloc;
import static org.bytedeco.javacpp.avutil.av_free;
import static org.bytedeco.javacpp.avutil.av_malloc;
import static org.bytedeco.javacpp.swscale.SWS_BILINEAR;
import static org.bytedeco.javacpp.swscale.sws_freeContext;
import static org.bytedeco.javacpp.swscale.sws_getContext;
import static org.bytedeco.javacpp.swscale.sws_scale;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avcodec.AVPicture;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVIOContext;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil.AVDictionary;
import org.bytedeco.javacpp.avutil.AVFrame;
import org.bytedeco.javacpp.swscale.SwsContext;

import cc.eguid.cv.corelib.videoimageshot.core.JavaImgConverter;
import cc.eguid.cv.corelib.videoimageshot.exception.CodecNotFoundExpception;
import cc.eguid.cv.corelib.videoimageshot.exception.FileNotOpenException;
import cc.eguid.cv.corelib.videoimageshot.exception.StreamInfoNotFoundException;
import cc.eguid.cv.corelib.videoimageshot.exception.StreamNotFoundException;

public class TestFFmpeg {

	public static void main(String[] args) throws IOException {
		// http://fms.cntv.lxdns.com/live/flv/channel2.flv
		String[] arg = {"rtmp://live.hkstv.hk.lxdns.com/live/hks", 
				"rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov",
				"http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8" };
		openvideo(arg[0]);
	}

	/**
	 * 保存帧
	 * @param pFrame
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	private static ByteBuffer saveFrame(AVFrame pFrame, int width, int height) throws IOException {
//		byte[] arr=new byte[width*height*3];
//		BytePointer data = pFrame.data(0).get(arr);
//		JavaImgConverter.viewBGR(width, height,ByteBuffer.wrap(arr));
		BytePointer data=pFrame.data(0);
		int size=width*height*3;
		ByteBuffer buf=data.position(0).limit(size).asBuffer();
		return buf;
	}
	
	public static BufferedImage frame2BufferImage(AVFrame pFrame, int width, int height) {
		BytePointer data=pFrame.data(0);
		int size=width*height*3;
		ByteBuffer buf=data.position(0).limit(size).asBuffer();
		return JavaImgConverter.BGR2BufferedImage(buf, width, height);
	}
	
	/**
	 * 把YUVJ420P数据编码保存成jpg图片
	 * @param pFrame -YUVJ420P数据
	 * @param index -序号
	 * @return
	 */
	private static int saveImg(AVFrame pFrame, int index,String out_file) {
		int width= pFrame.width(), height= pFrame.height();
		// 分配AVFormatContext对象
		AVFormatContext pFormatCtx = avformat_alloc_context();
		// 设置输出文件格式
		pFormatCtx.oformat(av_guess_format("PNG", null, null));
		if (pFormatCtx.oformat() == null) {
			return -1;
		}
		// 创建并初始化一个和该url相关的AVIOContext
		AVIOContext pb = new AVIOContext();
		if (avio_open(pb, out_file, AVIO_FLAG_READ_WRITE) < 0) {
			System.err.println("Couldn't open output file.");
			return -1;
		}
		pFormatCtx.pb(pb);
		// 构建一个新stream
		AVCodec codec = null;
		AVStream pAVStream = avformat_new_stream(pFormatCtx, codec);
		if (pAVStream == null) {
			return -1;
		}
		// 设置该stream的信息
		AVCodecContext pCodecCtx = pAVStream.codec();
		pCodecCtx.codec_id(pFormatCtx.oformat().video_codec());
		pCodecCtx.codec_type(AVMEDIA_TYPE_VIDEO);
		pCodecCtx.pix_fmt(pFrame.format());
		pCodecCtx.width(width);
		pCodecCtx.height(height);
		pCodecCtx.time_base().num(1);
		pCodecCtx.time_base().den(25);
		// Begin Output some information
		av_dump_format(pFormatCtx, 0, out_file, 1);
		// End Output some information
		// 查找解码器
		AVCodec pCodec = avcodec_find_encoder(pCodecCtx.codec_id());
		if (pCodec == null) {
			System.err.println("Codec not found.");
			return -1;
		}
		// 设置pCodecCtx的解码器为pCodec
		if (avcodec_open2(pCodecCtx, pCodec, (PointerPointer) null) < 0) {
			System.err.println("Could not open codec.");
			return -1;
		}

		// Write Header
		avformat_write_header(pFormatCtx, (PointerPointer) null);

		int y_size = width * height;

		// 给AVPacket分配足够大的空间
		AVPacket pkt = new AVPacket();
		av_new_packet(pkt, y_size * 3);
		//
		int[] got_picture_arr = { 0 };
//		IntPointer got_picture = new IntPointer(got_picture_arr);
		int ret = avcodec_encode_video2(pCodecCtx, pkt, pFrame, got_picture_arr);
		if (ret < 0) {
			System.err.println("Encode Error.\n");
			return -1;
		}
		if (pkt != null && !pkt.isNull()) {
			// pkt.stream_index = pAVStream->index;
			ret = av_write_frame(pFormatCtx, pkt);
		}
		// Write Trailer
		if (av_write_trailer(pFormatCtx) >= 0) {
			System.err.println("Encode Successful.");
		}

		av_free_packet(pkt);

		if (pAVStream != null) {
			avcodec_close(pAVStream.codec());
		}

		if (pFormatCtx != null) {
			avio_close(pFormatCtx.pb());
			avformat_free_context(pFormatCtx);
		}

		return 0;
	}

	public static ByteBuffer openvideo(String url) throws IOException {
		if(url==null) {
			throw new IllegalArgumentException("Didn't open video file");
		}
		AVFormatContext pFormatCtx = new AVFormatContext(null);
		int i=0, videoStream;
		AVCodecContext pCodecCtx = null;
		AVCodec pCodec = null;
		AVFrame pFrame = null;
		AVFrame pFrameRGB = null;
		AVPacket packet = new AVPacket();
		int[] frameFinished = new int[1];
		int numBytes;
		BytePointer buffer = null;

		AVDictionary optionsDict = null;
		SwsContext sws_ctx = null;

		// Register all formats and codecs
		av_register_all();
		avformat_network_init();

		// Open video file
		if (avformat_open_input(pFormatCtx, url, null, null) != 0) {
			System.err.println("Didn't open video file");
			throw new FileNotOpenException("Didn't open video file");
		}

		// Retrieve stream information
		if (avformat_find_stream_info(pFormatCtx, (PointerPointer) null) < 0) {
			System.err.println("Didn't retrieve stream information");
			throw new StreamInfoNotFoundException("Didn't retrieve stream information");
		}

		// Dump information about file onto standard error
		av_dump_format(pFormatCtx, 0, url, 0);

		// Find the first video stream
		videoStream = -1;
		for (i = 0; i < pFormatCtx.nb_streams(); i++) {
			if (pFormatCtx.streams(i).codec().codec_type() == AVMEDIA_TYPE_VIDEO) {
				videoStream = i;
				break;
			}
		}
		if (videoStream == -1) {
			System.err.println("Didn't find a video stream");
			throw new StreamNotFoundException("Didn't open video file");
		}

		// Get a pointer to the codec context for the video stream
		pCodecCtx = pFormatCtx.streams(videoStream).codec();

		// Find the decoder for the video stream
		pCodec = avcodec_find_decoder(pCodecCtx.codec_id());
		if (pCodec == null) {
			System.err.println("Codec not found");
			throw new CodecNotFoundExpception("Codec not found");
		}
		// Open codec
		if (avcodec_open2(pCodecCtx, pCodec, optionsDict) < 0) {
			System.err.println("Could not open codec");
			throw new CodecNotFoundExpception("Could not open codec"); // Could not open codec
		}

		// Allocate video frame
		pFrame = av_frame_alloc();

		// Allocate an AVFrame structure
		pFrameRGB = av_frame_alloc();
		if (pFrameRGB == null) {
			return null;
		}

		int width = pCodecCtx.width(), height = pCodecCtx.height();
		pFrameRGB.width(width);
		pFrameRGB.height(height);
		pFrameRGB.format(AV_PIX_FMT_RGB24);
		
		// Determine required buffer size and allocate buffer
		numBytes = avpicture_get_size(AV_PIX_FMT_RGB24,width, height);
		buffer = new BytePointer(av_malloc(numBytes));

		sws_ctx = sws_getContext(pCodecCtx.width(), pCodecCtx.height(), pCodecCtx.pix_fmt(), pCodecCtx.width(),
				pCodecCtx.height(), AV_PIX_FMT_RGB24, SWS_BILINEAR, null, null, (DoublePointer) null);

		// Assign appropriate parts of buffer to image planes in pFrameRGB
		// Note that pFrameRGB is an AVFrame, but AVFrame is a superset
		// of AVPicture
		avpicture_fill(new AVPicture(pFrameRGB), buffer, AV_PIX_FMT_RGB24,width, height);

		// Read frames and save first five frames to disk
		while (av_read_frame(pFormatCtx, packet) >= 0) {
			// Is this a packet from the video stream?
			if (packet.stream_index() == videoStream) {
				// Decode video frame
				avcodec_decode_video2(pCodecCtx, pFrame, frameFinished, packet);

				// Did we get a video frame?
				if(frameFinished!=null) {
	                //转换图像格式，将解压出来的YUV420P的图像转换为BRG24的图像
	                sws_scale(sws_ctx, pFrame.data(),pFrame.linesize(), 0, pCodecCtx.height(),pFrameRGB.data(), pFrameRGB.linesize());
	 
				}
				if (frameFinished[0] != 0) {
					// Convert the image from its native format to RGB
					sws_scale(sws_ctx, pFrame.data(), pFrame.linesize(), 0, pCodecCtx.height(), pFrameRGB.data(),pFrameRGB.linesize());

					// Save the frame to disk
					return saveFrame(pFrameRGB, pCodecCtx.width(), pCodecCtx.height());
//					frame2BufferImage(pFrameRGB, pCodecCtx.width(), pCodecCtx.height());
				}
			}

			// Free the packet that was allocated by av_read_frame
			av_free_packet(packet);
		}

		// Free the RGB image
		av_free(buffer);
		av_free(pFrameRGB);

		// Free the YUV frame
		av_free(pFrame);

		// Close the codec
		avcodec_close(pCodecCtx);

		// Close the video file
		avformat_close_input(pFormatCtx);
		return null;

	}


	public static boolean YV12ToBGR24_FFmpeg(BytePointer pYUV, BytePointer pBGR24,int width,int height)
	{
	    if (width < 1 || height < 1 || pYUV == null || pBGR24 == null)
	        return false;
	    //int srcNumBytes,dstNumBytes;
	    //uint8_t *pSrc,*pDst;
	    AVPicture pFrameYUV = new AVPicture(), pFrameBGR = new AVPicture();
	    
	    //pFrameYUV = avpicture_alloc();
	    //srcNumBytes = avpicture_get_size(PIX_FMT_YUV420P,width,height);
	    //pSrc = (uint8_t *)malloc(sizeof(uint8_t) * srcNumBytes);
	    avpicture_fill(pFrameYUV,pYUV,AV_PIX_FMT_YUV420P,width,height);

	    //U,V互换
	    BytePointer ptmp=pFrameYUV.data(1);
	    pFrameYUV.data(1, pFrameYUV.data(2));
	    pFrameYUV.data(2,ptmp);
	    //pFrameBGR = avcodec_alloc_frame();
	    //dstNumBytes = avpicture_get_size(PIX_FMT_BGR24,width,height);
	    //pDst = (uint8_t *)malloc(sizeof(uint8_t) * dstNumBytes);
	    avpicture_fill(pFrameBGR,pBGR24,AV_PIX_FMT_BGR24,width,height);

	    SwsContext imgCtx = null;
	    imgCtx = sws_getContext(width,height,AV_PIX_FMT_YUV420P,width,height,AV_PIX_FMT_BGR24,SWS_BILINEAR,null, null, (DoublePointer) null);

	    if (imgCtx != null){
	        sws_scale(imgCtx,pFrameYUV.data(),pFrameYUV.linesize(),0,height,pFrameBGR.data(),pFrameBGR.linesize());
	        if(imgCtx != null){
	            sws_freeContext(imgCtx);
	            imgCtx = null;
	        }
	        return true;
	    }
	    else{
	        sws_freeContext(imgCtx);
	        imgCtx = null;
	        return false;
	    }
	}
	
}