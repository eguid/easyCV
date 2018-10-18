package cc.eguid.cv.web.videoimageshotweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cc.eguid.cv.web.videoimageshotweb.cache.ScreenshotCache;
import cc.eguid.cv.web.videoimageshotweb.pojo.ScreenshotHistory;

/**
 * 服务监控
 * @author eguid
 *
 */
@Controller
public class MonitorController {

	@RequestMapping(value="/",method=RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("hello", "视频截图记录列表");
		model.addAttribute("hislist", ScreenshotCache.getHistoryList());
		return "monitor";
	}
	
	/**
	 * 图片预览
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/view",method=RequestMethod.GET)
	public String view(Model model,@RequestParam(required=true)String id) {
		model.addAttribute("hello", "截图预览");
		ScreenshotHistory his=ScreenshotCache.get(id);
		model.addAttribute("his", his);
		return "viewImage";
	}
	
	@RequestMapping(value="/test",method=RequestMethod.GET)
	public String testShot(Model model) {
		model.addAttribute("hello", "视频截图测试");
		return "testImgshot";
	}
}
