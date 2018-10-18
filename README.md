# easyCV
video recording and snapshot service,based on javaCV. 基于javaCV的视频录像和快照(截图)服务。


### dependency library
Core lib based on 'javacv 1.4.x',web service based on 'spring-boot 2.x'.

### build
Project is based on jdk1.8,build on maven.

### core lib
  The core library of video recording and snapshots is two separate modules.
  截图快照和视频录像是两个独立的核心库。

### web service
  Web services used springboot services,each web service is an independent micro service.
  The default port of video recording service is '8082',video capture service is '8081'.
    同样的，web服务也是两个独立的springboot微服务。
  其中截图功能是支持文件和base64两种方式生成截图，而录像服务除了需要指定保存路径外，还需要配置一个可访问的http/ftp访问地址（我们一般把录像文件存放到一     个http/ftp服务的目录下，以方便点播录像文件）

### support
   Video source support rtsp/rtmp/flv/hls/file...,record file support mp4/flv/mkv/avi....
   Image format support jpg/png/jpeg/gif/bmp.
   视频源支持多种音视频流媒体源，录像文件可以任意指定保存的视频格式，视频截图快照支持以上五种格式。

