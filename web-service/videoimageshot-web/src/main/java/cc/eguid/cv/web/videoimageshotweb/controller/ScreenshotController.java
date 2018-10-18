package cc.eguid.cv.web.videoimageshotweb.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cc.eguid.cv.corelib.videoimageshot.FFmpegScreenshot;
import cc.eguid.cv.corelib.videoimageshot.Screenshot;
import cc.eguid.cv.web.videoimageshotweb.cache.ScreenshotCache;
import cc.eguid.cv.web.videoimageshotweb.pojo.ErrorMsg;
import cc.eguid.cv.web.videoimageshotweb.pojo.ScreenshotHistory;
import cc.eguid.cv.web.videoimageshotweb.pojo.ScreenshotInfo;
import cc.eguid.cv.web.videoimageshotweb.pojo.ShotParam;
import cc.eguid.cv.web.videoimageshotweb.util.CommonUtil;

/**
 * 视频截图（允许跨域请求）
 * @author eguid
 *
 */
@RestController
@CrossOrigin
public class ScreenshotController {

	private ErrorMsg errormsg=new ErrorMsg();
	private final Screenshot shoter=new FFmpegScreenshot();
	
	@RequestMapping(value="shot",method=RequestMethod.GET)
	public Object shot(ShotParam param) {
		long now=System.currentTimeMillis();
		
		String url=null,imgurl = null,fmt=null,base64=null;
		if(CommonUtil.isNullOrEmpty((url=param.getSrc()))) {
			errormsg.setMsg("视频源地址不能为空");
			return errormsg;
		}
		fmt=param.getFmt();
		try {
			ScreenshotInfo info=new ScreenshotInfo();
			if(!CommonUtil.isNullOrEmpty(param.getOutput())) {
				if(CommonUtil.isNullOrEmpty(param.getName())) {
					errormsg.setMsg("图片名称不能为空");
					return errormsg;
				}
				imgurl=param.getOutput()+param.getName();
				info.setSrc(imgurl);
				if(null!=param.getNeedBase64()&&param.getNeedBase64()==1) {
					if(CommonUtil.isNullOrEmpty(fmt)) {
						errormsg.setMsg("图片格式不能为空");
						return errormsg;
					}
					base64=shoter.shotAndGetBase64(url, imgurl, fmt);
					info.setBase64(base64);
				}else {
					shoter.shot(url	,imgurl,fmt);
				}
				save(param,info);
				return info;
			}
			if(CommonUtil.isNullOrEmpty(fmt)) {
				errormsg.setMsg("图片格式不能为空");
				return errormsg;
			}
			long cu=System.currentTimeMillis();
			base64=shoter.getImgBase64(url, fmt);
			System.err.println("截图耗时："+(System.currentTimeMillis()-cu));
			info.setBase64(base64);
			save(param,info);
			System.err.println("总耗时："+(System.currentTimeMillis()-now));
			return info;
		} catch (Exception e) {
			errormsg.setMsg("截图失败");
			return errormsg;
		}
		
	}

	@RequestMapping(value="help",method=RequestMethod.GET)
	public Object help() {
		errormsg.setMsg("请求的参数说明：name-截图名称（必须，可带后缀名，例如eguid.png）；"
				+ "src-视频源地址（必须，rtsp,rtmp,hls,视频文件等等可访问可播放的视频地址）；"
				+ "fmt-保存的图片格式（可选参数，默认保存jpg格式，支持'jpg','png','jpeg','gif','bmp'等等）"
				+ "output-图片保存位置（可选）；needBase64-是否需要base64编码的图像数据（可选）；");
		return errormsg;
	}
	
	private void save(ShotParam param,ScreenshotInfo info) {
		ScreenshotHistory his=new ScreenshotHistory(param);
		his.setBase64(info.getBase64());
		his.setShottime(new Date());
		ScreenshotCache.save(his);
	}
}
