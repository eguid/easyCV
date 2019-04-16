package cc.eguid.cv.web.videoimageshotweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cc.eguid.cv.web.videoimageshotweb.pojo.ErrorMsg;
import cc.eguid.cv.web.videoimageshotweb.pojo.ShotParam;
import cc.eguid.cv.web.videoimageshotweb.pojo.VideoshotInfo;
import cc.eguid.cv.web.videoimageshotweb.service.VideoShotService;
import cc.eguid.cv.web.videoimageshotweb.util.CommonUtil;

/**
 * 视频截图（允许跨域请求）
 * @author eguid
 *
 */
@RestController
@CrossOrigin
public class ScreenshotController {

	Logger log=LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	VideoShotService shotService;
	
	
	@RequestMapping(value="shot",method=RequestMethod.GET)
	public Object shot(ShotParam param) {
		ErrorMsg errormsg=new ErrorMsg();
		Integer needBase64=param.getNeedBase64();
		if(CommonUtil.isNullOrEmpty(param.getSrc(),param.getName())) {
			errormsg.setMsg("视频源地址和图片名称不能为空");
			return errormsg;
		}
		int index=-1;
		//如果名称不含包后缀名，则判断是否有格式参数
		if((index=param.getName().indexOf("."))>0){
			//如果有后缀名，切除后缀并把后缀放到fmt
			String name=param.getName();
			param.setFmt(name.substring(index+1));
			param.setName(name.substring(0, index));
		}else if(CommonUtil.isNullOrEmpty(param.getFmt())) {
			//没有后缀名且格式参数为空
			errormsg.setMsg("图片格式不能为空");
			return errormsg;
		}
		if(!CommonUtil.isInt(param.getName())) {
			errormsg.setMsg("摄像机ID不标准");
			return errormsg;
		}
		log.error(param.toString());
		//默认保存路径使用配置文件配置
		// 是否需要base64编码，0或空只要文件，不要base64编码（默认），1-只返回base64编码，2-base64和文件都需要
//		if(needBase64==null||needBase64==0||needBase64==2) {
//			if(CommonUtil.isNullOrEmpty(param.getOutput())) {
//				errormsg.setMsg("图片保存位置不能为空");
//				return errormsg;
//			}
//		}
		
		try {
			VideoshotInfo ret=shotService.shot(param);
			return ret;
		} catch (Exception e) {
			errormsg.setMsg("截图失败");
			return errormsg;
		}
		
	}

	@RequestMapping(value="help",method=RequestMethod.GET)
	public Object help() {
		ErrorMsg errormsg=new ErrorMsg("请求的参数说明：name-截图名称（必须，可带后缀名，例如eguid.png，不带后缀名必须有fmt参数）；"
				+ "src-视频源地址（必须，rtsp,rtmp,hls,视频文件等等可访问可播放的视频地址）；"
				+ "fmt-保存的图片格式（可选参数，默认保存jpg格式，如果名称中不带后缀名则该参数必填，支持'jpg','png','jpeg','gif','bmp'等等）"
				+ "output-图片保存位置（可选），为空默认使用配置文件中的路径；needBase64-是否需要base64编码的图像数据（可选）；");
		return errormsg;
	}
//	
//	private void save(ShotParam param,ScreenshotInfo info) {
//		ScreenshotHistory his=new ScreenshotHistory(param);
//		his.setBase64(info.getBase64());
//		his.setShottime(new Date());
//		ScreenshotCache.save(his);
//	}
}
