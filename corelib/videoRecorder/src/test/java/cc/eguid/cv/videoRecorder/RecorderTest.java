package cc.eguid.cv.videoRecorder;

import java.io.IOException;

import cc.eguid.cv.videoRecorder.entity.RecordTask;
import cc.eguid.cv.videoRecorder.manager.DefaultTasksManager;
import cc.eguid.cv.videoRecorder.manager.TasksManager;
import cc.eguid.cv.videoRecorder.recorder.JavaCVRecord;

public class RecorderTest {

	//单个测试
	public static void testSingle(TasksManager manager) throws Exception {
		System.out.println("最后一个测试");
		String src="rtmp://media3.sinovision.net:1935/live/livestream",out="eguid.flv";
		RecordTask task=manager.createRcorder(src, out);
		manager.start(task);
		
		Thread.sleep(5*1000);
		System.out.println("最后一个，当前任务数量："+manager.list().size());
	
		manager.stop(task);
	}
	//多个同时测试
	public static void test3() throws Exception {
		String src="rtmp://media3.sinovision.net:1935/live/livestream",out="test";
		TasksManager manager=new DefaultTasksManager(10);
		RecordTask[] tasks =new RecordTask[20] ;
		
//		//开始10个
		for(int i=1;i<20;i++) {
			String file=out+i+".mp4";
			RecordTask task=manager.createRcorder(src, file);
			System.err.println("初始化任务，任务详情："+task+"，输出位置："+file);
//			manager.start(task);
			tasks[i]=task;
		}
//		
		for(int i=1;i<20;i++) {
			
			boolean ret=manager.start(tasks[i]);
			System.err.println("启动："+i+(ret?"，成功":"，失败"));
		}
//		
		System.err.println("当前任务数量："+manager.list().size());
		
		Thread.sleep(5*1000);
//		//暂停全部
		for(RecordTask task:manager.list()) {
			manager.pause(task);
		}
		Thread.sleep(5*1000);
		//恢复全部
		for(RecordTask task:manager.list()) {
			manager.carryon(task);
		}
		//停止全部
		for(RecordTask task:manager.list()) {
			manager.stop(task);
		}
		testSingle(manager);//增加一个测试看看历史任务
	
	}
	
	public static void test2() throws org.bytedeco.javacv.FrameGrabber.Exception, IOException, InterruptedException {

			// Recorder cv1=new
			// JavaCVRecord().from("rtmp://media3.sinovision.net:1935/live/livestream").audioParam(2,
			// 128*1000, 48*1000).
			// to("test.mp4");
			// JavaCVRecord cv2=new
			// JavaCVRecord().from("rtmp://media3.sinovision.net:1935/live/livestream")
			// .to("test1.mp4");
			// JavaCVRecord cv3=new
			// JavaCVRecord().from("rtmp://media3.sinovision.net:1935/live/livestream")
			// .to("test2.mp4");
			// JavaCVRecord cv4=new
			// JavaCVRecord().from("rtmp://media3.sinovision.net:1935/live/livestream")
			// .to("test3.mp4");

			// cvrecord.stream("rtmp://media3.sinovision.net:1935/live/livestream","eguid.mp4");
			// JavaCVRecord cvrecord=new
			// JavaCVRecord("rtmp://media3.sinovision.net:1935/live/livestream","rtmp://106.14.182.20:1935/rtmp/eguid",300,200);
			JavaCVRecord record = new JavaCVRecord();
//			record.from("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov").to("rtmp://eguid.cc:1935/rtmp/eguid");
//			record.forward();
			// cvrecord.stream().forward();//转封装
			record.from("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov").videoParam(200, 100, 30, 10000).to("eguid.mp4").start();
			
			// cv1.start();
			// cv2.start();
			// cv3.start();
			// cv4.start();
			 Thread.sleep(5000);
			 record.stop();
//			 Thread.sleep(10000);
//			 record.from("rtmp://media3.sinovision.net:1935/live/livestream").to("eguid1.mp4").start();
//			 Thread.sleep(5000);
//			 record.stop();
			// cv1.pause();
			// Thread.sleep(10000);
			// cv1.carryon();
			// Thread.sleep(3000);
			// cv1.stop();//停止
			// Thread.sleep(2000);
			// cv1.restart();
			// cv2.start();
			// cv3.start();
			// cv4.start();
			// Thread.sleep(5000);
			// cv1.stop();
			// cv2.stop();
			// cv3.stop();
			// cv4.stop();

	}
	
	public static void main(String[] args) throws Exception {
		test2();
	}
}
