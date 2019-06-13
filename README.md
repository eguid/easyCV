[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![LICENSE](https://camo.githubusercontent.com/f969af70fa6573766a11cb0a968fc82b069298f1/68747470733a2f2f696d672e736869656c64732e696f2f6769746875622f6c6963656e73652f6c697a68696368616f2f6f6e652e737667)](https://github.com/eguid/easyCV/blob/master/LICENSE)
# easyCV
      video recorder and snapshot(video image capture) service,based on javaCV. 
      基于javaCV的跨平台视频录像和快照(视频截图)服务，开箱即用。
### 更新
      1、录像服务的持久层设计不合理，现去除录像服务的持久层接口
      2、新增两个与springboot+postgre数据库演示demo
  
### 演示demo
1、[截图服务在线演示：http://eguid.cc/screenshot/test](http://eguid.cc/screenshot/test)<br />
      
2、[录像服务在线演示：http://eguid.cc/videorecord/test](http://eguid.cc/videorecord/test)<br />
       
       可以通过http://eguid.cc/screenshot/查看历史截图列表
       同样可以通过http://eguid.cc/videorecord/查看历史录像列表并进行点播观看
 
### dependency library
      Corelib based on 'javacv 1.4.x',exaples based on 'spring-boot 2.x'.

### build
      Based on jdk1.8,build on maven 3.7.

### core lib
      核心库提供截图快照和视频录像两套API，exaples中提供了几个演示示例，分别依赖录像和截图corelib

### exaples
    提供了几个springboot演示服务demo，截图服务演示demo默认使用8081端口，录像服务使用8082端口。
    其中截图功能支持保存成文件和返回base64两种方式获取截图。
    截图文件与录像文件都需要额外配合一个单独http/ftp服务才能进行访问
    录像服务演示demo除了需要指定保存路径外，与截图服务相同，我们一般把录像文件存放到http/ftp服务的根目录下，方便点播，推荐使用nginx、apache和iis）。
    demo中涉及到截图和录像信息的表结构都是简单的单表，这里就不提供了，直接查看*Mapper.xml文件即可

### support
    Video source support rtsp/rtmp/flv/hls/file formats,Record video file support mp4/flv/mkv/avi .... formats.
    Image file support jpg/png/jpeg/gif/bmp.
    视频源支持rtsp/rtmp/flv/hls/视频文件等多种格式。
    录像文件可以支持mp4/flv/mkv/avi等多种视频格式。
    视频快照（视频截图）支持jpg/png/jpeg/gif/bmp等图片格式，常见的格式基本都支持。

