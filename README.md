[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![LICENSE](https://camo.githubusercontent.com/f969af70fa6573766a11cb0a968fc82b069298f1/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f6c697a68696368616f2f6f6e652e737667)](https://github.com/eguid/easyCV/blob/master/LICENSE)
# easyCV
      Video recorder and snapshot(video image capture) library,based on javaCPP & javaCV & FFmpeg. 
      基于javaCV的跨平台视频录像和快照(视频截图)库，开箱即用。
### Update
	2019年12月2日
	1、本次更新主要针对videoRecorder工作线程在特定情况下（异常）出现无法回收的bug
	
	2019年7月22日
	1、新增BufferGrabber和BufferedImageGrabber的连续截图语法糖
	2、截图库不再依赖javaCV
	
	2019年7月17日b
	1、增加连续截图功能，目前只限通过BytesGrabber进行连续截图
	1.1、连续截图提供视频源地址、截图总数、间隔（隔几帧）
	1.2、简单测试了连续截图功能暂时未发现问题，近期将实现其他接口的连续截图功能语法糖
	2、代码结构调整，抽象出一个桥接接口Grabber，用于方便不同接口实现能够对像素格式进行不同方式的操作
	
	2019年7月17日a
	1、调整了截图库代码结构
	2、向下兼容老版本，但不再推荐使用原有的调用方式
	3、新增了一个图像像素数据字节数组抓取器(BytesGrabber)，它能够获得图像的像素数组
	4、新增了一个Base64图像编码数据抓取器（Base64Grabber），用于取代原来的调用方式
	5、重新根据ffmpeg4.x最新解码库api实现视频解码流程，支持B、P帧解码，与原有代码不兼容，所以新创建新的FFmpeg4VideoImageGrabber以示区别
	6、推荐的使用方式是
	`
	//可以通过BufferedImageGrabber直接截取得到java图像
	BufferedImageGrabber grabber=new FFmpeg4VideoImageGrabber();
	//可以通过Base64Grabber轻松截取base64图像编码数据
	Base64Grabber grabber=new FFmpeg4VideoImageGrabber();
	//可以通过BufferGrabber获取截图图像的缓冲数据
	BufferGrabber grabber =new FFmpeg4VideoImageGrabber();
	//可以通过BytesGrabber获取截图图像的像素数组数据
	BytesGrabber grabber=new FFmpeg4VideoImageGrabber();
	`
	根据需要自行调用和管理，本项目只提供可靠的截图库，而不是大而全的框架。
  
### Use samples
	目前除了Base64Grabber之外其他Grabber都实现了连续截图api
```
	//可以通过BufferedImageGrabber直接截取得到java图像
	BufferedImageGrabber grabber=new FFmpeg4VideoImageGrabber();
	//可以通过Base64Grabber轻松截取base64图像编码数据
	Base64Grabber grabber=new FFmpeg4VideoImageGrabber();
	//可以通过BufferGrabber获取截图图像的缓冲数据
	BufferGrabber grabber =new FFmpeg4VideoImageGrabber();
	//可以通过BytesGrabber获取截图图像的像素数组数据
	BytesGrabber grabber=new FFmpeg4VideoImageGrabber();
```

### Online demo 
1、[截图服务在线演示：http://eguid.cc/screenshot/test](http://eguid.cc/screenshot/test)<br />
      
2、[录像服务在线演示：http://eguid.cc/videorecord/test](http://eguid.cc/videorecord/test)<br />
       
       可以通过http://eguid.cc/screenshot/查看历史截图列表
       同样可以通过http://eguid.cc/videorecord/查看历史录像列表并进行点播观看
 
### Dependency library
      Corelib based on 'javaCPP 1.4.X' & 'javaCPP-FFMPEG-1.4.x' & 'javacv 1.4.x',exaples based on 'spring-boot 2.x'.

### Build
      Based on jdk1.8,build on maven 3.7.

### About core library
      核心库提供截图快照和视频录像两套API，exaples中提供了几个演示示例，分别依赖录像和截图corelib

### Examples project description
    提供了几个springboot演示服务demo，截图服务演示demo默认使用8081端口，录像服务使用8082端口。
    其中截图功能支持保存成文件和返回base64两种方式获取截图。
    截图文件与录像文件都需要额外配合一个单独http/ftp服务才能进行访问
    录像服务演示demo除了需要指定保存路径外，与截图服务相同，我们一般把录像文件存放到http/ftp服务的根目录下，方便点播，推荐使用nginx、apache和iis）。
    demo中涉及到截图和录像信息的表结构都是简单的单表，这里就不提供了，直接查看*Mapper.xml文件即可

### Format of video&Image support
    Video source support rtsp/rtmp/flv/hls/file formats,Record video file support mp4/flv/mkv/avi .... formats.
    Image file support jpg/png/jpeg/gif/bmp.
    视频源支持rtsp/rtmp/flv/hls/视频文件等多种格式。
    录像文件可以支持mp4/flv/mkv/avi等多种视频格式。
    视频快照（视频截图）支持jpg/png/jpeg/gif/bmp等图片格式，常见的格式基本都支持。

