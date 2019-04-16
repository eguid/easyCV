# easyCV
      video recording and snapshot service,based on javaCV. 
      基于javaCV的跨平台视频录像和快照(截图)服务，开箱即用。
### 更新
      1、录像服务的持久层设计不合理，现去除录像服务的持久层接口
      2、新增两个与springboot+postgre数据库演示demo
# License
  [![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)

### 演示demo
1、[截图服务在线演示：http://eguid.cc/screenshot/test](http://eguid.cc/screenshot/test)<br />
      
2、[录像服务在线演示：http://eguid.cc/videorecord/test](http://eguid.cc/videorecord/test)<br />
       
       可以通过http://eguid.cc/screenshot/查看历史截图列表
       同样可以通过http://eguid.cc/videorecord/查看历史录像列表并进行点播观看
 
### dependency library
      Corelib based on 'javacv 1.4.x',exaples based on 'spring-boot 2.x'.

### build
      Project is based on jdk1.8,build on maven 3.7.

### core lib
      核心库提供截图快照和视频录像两套API，exaples中几个示例都是基于这两个库。

### exaples
     提供了几个springboot服务，截图服务默认使用8081端口，录像服务使用8082端口。
    其中截图功能支持保存成文件和返回base64两种方式获取截图。
    截图文件与录像文件都需要额外配合一个单独http/ftp服务才能进行访问
    录像服务除了需要指定保存路径外，与截图服务相同，我们一般把录像文件存放到http/ftp服务的根目录下，方便点播，推荐使用nginx）。
    截图和录像信息的表结构都是简单的单表，这里就不提供了，直接查看*Mapper.xml文件即可

### support
    Support rtsp/rtmp/flv/hls/file...,Recorder support mp4/flv/mkv/avi....
    Image support jpg/png/jpeg/gif/bmp.
    视频源支持rtsp/rtmp/flv/hls/视频文件等多种音视频流媒体源。
    录像文件可以支持mp4/flv/mkv/avi等视频格式。
    视频快照（截图）支持jpg/png/jpeg/gif/bmp等格式。

