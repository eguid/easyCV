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

	/**开始状态*/
	public static final int START_STATUS=1;
	/**停止状态*/
	public static final int STOP_STATUS=2;
	/**暂停状态*/
	public static final int PAUSE_STATUS=1;
	
	protected FFmpegFrameGrabber grabber =null;
	protected FFmpegFrameRecorderPlus record =null;
	
	/**
	 * 运行状态，0-初始状态，1-运行，2-停止
	 */
	protected volatile int status=0;
	protected volatile int pause=0;//是否暂停，1-暂停
	protected int err_stop_num=3;//默认错误数量达到三次终止录制
	
	/**
	 * 
	 * @param name -线程名称
	 * @param grabber -抓取器
	 * @param record -录制器
	 * @param err_stop_num 允许的错误次数，超过该次数后即停止任务
	 */
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
		go();
		for(;;) {
			if(status==2) {
				System.out.println("工作线程进入等待状态");
				synchronized (this){
					try {
						wait();
					}catch(InterruptedException e) {
					}
				}
				System.out.println("工作线程唤醒");
				continue;
			}
			//核心任务循环
			mainLoop();
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
		try {
			for(;status==START_STATUS;frame_index++) {
				Frame pkt=grabber.grabFrame();
				if(pause==1) {//暂停状态
					pause_num++;
					continue;
				}
				if(pkt==null) {//采集空包结束
					if(err_index>err_stop_num) {//超过三次则终止录制
						System.err.println(getName()+"工作线程采集视频帧连续"+err_index+"次空包，本次任务终止");
						break;
					}
					err_index++;
					continue;
				}
				record.record(pkt);
			}
		}catch (Exception e) {//推流失败
			status=STOP_STATUS;//到这里表示已经停止了
			System.err.println("异常导致停止录像，详情："+e.getMessage());
		}finally {
			status=STOP_STATUS;
			stopRecord();
			System.out.println(getName()+"工作线程的录像任务已结束，持续时长："+(System.currentTimeMillis()-startime)/1000+"秒，共录制："+frame_index+"帧，遇到的错误数："+err_index+",录制期间共暂停次数："+pause_num);
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
		pause=PAUSE_STATUS;
	}
	
	/**
	 * 继续（从暂停中恢复）
	 */
	public void carryon() {
		if(pause==PAUSE_STATUS) {//如果暂停状态为1才允许
			pause=0;
			status=START_STATUS;
		}
	}
	
	/**
	 * 结束
	 */
	public void over() {
		status=STOP_STATUS;
	}

	/**
	 * 开始（如果线程处于等待状态则唤醒）
	 */
	public void go() {
		if(status==0) {//如果初始状态，则设置为开始状态1
			status=START_STATUS;
			System.out.println("开启工作线程");
		}
		if(status==STOP_STATUS) {//如果是停止状态，设置为开始状态1，并唤醒线程
			this.status=START_STATUS;
			synchronized(this) {
				notify();
			}
			System.out.println("唤醒工作线程");
		}
	}
}
