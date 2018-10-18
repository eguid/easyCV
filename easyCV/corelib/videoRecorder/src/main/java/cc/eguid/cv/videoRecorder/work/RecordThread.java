package cc.eguid.cv.videoRecorder.work;

import java.io.IOException;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import cc.eguid.cv.videoRecorder.recorder.FFmpegFrameRecorderPlus;

/**
 * 录制任务工作线程
 * @author eguid
 *
 */
public class RecordThread extends Thread {
	
	protected FFmpegFrameGrabber grabber =null;
	protected FFmpegFrameRecorderPlus record =null;
	
	/**
	 * 运行状态，0-初始状态，1-运行，2-停止
	 */
	protected volatile int status=0;
	protected volatile int pause=0;//是否暂停，1-暂停
	protected int err_stop_num=3;//默认错误数量达到三次终止录制
	
	public RecordThread(String name,FFmpegFrameGrabber grabber, FFmpegFrameRecorderPlus record,Integer err_stop_num) {
		super(name);
		this.grabber = grabber;
		this.record = record;
		if(err_stop_num!=null) {
			this.err_stop_num=err_stop_num;
		}
	}
	/**
	 * 运行过一次后必须进行重置参数和运行状态
	 */
	public void reset(FFmpegFrameGrabber grabber, FFmpegFrameRecorderPlus record) {
		this.grabber = grabber;
		this.record = record;
		this.status=0;
	}
	
	public int getErr_stop_num() {
		return err_stop_num;
	}
	
	public void setErr_stop_num(int err_stop_num) {
		this.err_stop_num = err_stop_num;
	}
	
	public FFmpegFrameGrabber getGrabber() {
		return grabber;
	}

	public void setGrabber(FFmpegFrameGrabber grabber) {
		this.grabber = grabber;
	}

	public FFmpegFrameRecorderPlus getRecord() {
		return record;
	}

	public void setRecord(FFmpegFrameRecorderPlus record) {
		this.record = record;
	}

	public int getStatus() {
		return status;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				if(status==2) {
					Thread.sleep(25);
					continue;
				}
				//核心任务循环
				mainLoop();
			}catch(InterruptedException e) {
			}
		}
	}

	/**
	 * 核心转换处理循环
	 */
	private void mainLoop() {
		long startime=System.currentTimeMillis();
		long err_index = 0;//采集或推流失败次数
		long frame_index=0;
		int pause_num=0;//暂停次数
		if(status==0) {//正在运行
			status=1;
		}
		try {
			for(;status==1;frame_index++) {
				Frame pkt=grabber.grabFrame();
				if(pause==1) {//暂停状态
					pause_num++;
					continue;
				}
				if(pkt==null) {//采集空包结束
					if(err_index>err_stop_num) {//超过三次则终止录制
						break;
					}
					err_index++;
					continue;
				}
				record.record(pkt);
			}
		}catch (Exception e) {//推流失败
			status=2;//到这里表示已经停止了
			System.err.println("异常导致停止录像，详情："+e.getMessage());
		}finally {
			status=2;
			stopRecord();
			System.err.println("录像已停止，持续时长："+(System.currentTimeMillis()-startime)/1000+"秒，共录制："+frame_index+"帧，遇到的错误数："+err_index+",录制期间共暂停次数："+pause_num);
		}
	}
	
	/**
	 * 停止录制
	 */
	private void stopRecord() {
		try {
			if(grabber!=null) {
				grabber.close();
			}
			if(record!=null) {
				record.stop();
			}
		} catch (IOException e) {
		}
	}
	
	/**
	 * 暂停
	 */
	public void pause() {
		pause=1;
	}
	
	/**
	 * 继续（从暂停中恢复）
	 */
	public void carryon() {
		pause=0;
		status=1;
	}
	
	/**
	 * 结束
	 */
	public void over() {
		status=2;
	}

}
