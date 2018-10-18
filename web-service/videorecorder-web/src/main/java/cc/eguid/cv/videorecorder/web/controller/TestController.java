package cc.eguid.cv.videorecorder.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;
import cc.eguid.cv.videorecorder.web.service.RecordService;

/**
 * 测试
 * @author eguid
 *
 */
@Controller
public class TestController {
	
	@Autowired
	RecordService recorderService;
	
	/**
	 * 监控端首页
	 * @return
	 */
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String index(Model model,@RequestParam(required=false)String isWork) {
		model.addAttribute("hello", "视频录像历史列表");
		boolean flag=(isWork!=null&&isWork.length()>0&&isWork.equals("true"))?true:false;
		model.addAttribute("list", recorderService.list(flag));
		return "monitor";
	}
	
	/**
	 * 播放录像视频（点播）
	 * @param model
	 * @param id -录像ID
	 * @return
	 */
	@RequestMapping(value="/play",method=RequestMethod.GET)
	public String play(Model model,@RequestParam(required=false)Integer id) {
		RecordInfo info =recorderService.get(id);
		model.addAttribute("record", info);
		return "play";
	}
	
	/**
	 * 测试录像
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/test",method=RequestMethod.GET)
	public String test(Model model) {
		model.addAttribute("hello", "测试视频录像");
		return "testRecord";
	}
}
