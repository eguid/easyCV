package cc.eguid.cv.videorecorder.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;
import cc.eguid.cv.videoRecorder.entity.RecordTask;
import cc.eguid.cv.videorecorder.web.pojo.ErrorMsg;
import cc.eguid.cv.videorecorder.web.service.RecordService;
import cc.eguid.cv.videorecorder.web.util.CommonUtil;

/**
 * 录像
 * @author eguid
 *
 */
@RestController
@CrossOrigin
public class RecordController {
	
	@Autowired
	RecordService recorderService;
	/**
	 * 录像
	 * @return
	 */
	@RequestMapping("/record")
	public ErrorMsg record(@RequestParam(required=true)String src,@RequestParam(required=true)String out) {
		ErrorMsg errormsg=new ErrorMsg();
		errormsg.setCode("0");
		if(CommonUtil.isAllNullOrEmpty(src,out)) {
			errormsg.setMsg("失败");
			return errormsg;
		}
		
		RecordTask rt=null;
		try {
			rt=recorderService.record(src, out);
			if(rt!=null) {
				errormsg.setCode("1");
				errormsg.setMsg(rt.getId().toString());
				return errormsg;
			}
		} catch (Exception e) {
		}
		errormsg.setMsg("失败");
		return errormsg;
	}
	
	/**
	 * 停止录像
	 * @return
	 */
	@RequestMapping("/stop")
	public ErrorMsg stop(@RequestParam(required=true)Integer id) {
		ErrorMsg errormsg=new ErrorMsg();
		errormsg.setCode("0");
		try {
			if(recorderService.stop(id)) {
				errormsg.setCode("1");
				errormsg.setMsg("成功");
				return errormsg;
			}
		} catch (Exception e) {
		}
		errormsg.setMsg("失败");
		return errormsg;
	}
	
	/**
	 * 列表
	 * @param isWork
	 * @return
	 */
	@RequestMapping("/list")
	public List<?> list(@RequestParam(required=false)String isWork) {
		boolean flag=(isWork!=null&&isWork.length()>0&&isWork.equals("true"))?true:false;
		return recorderService.list(flag);
	}
	
	/**
	 * 根据id查询
	 * @return
	 */
	@RequestMapping("/get")
	public Object get(@RequestParam(required=true)Integer id) {
		ErrorMsg errormsg=new ErrorMsg();
		errormsg.setCode("0");
		if(CommonUtil.isNull(id)) {
			errormsg.setMsg("id不能为空");
			return errormsg;
		}
		RecordInfo info=recorderService.get(id);
		System.err.println(info);
		if(info!=null) {
			return info;
		}
		errormsg.setMsg("失败");
		return errormsg;
	}
}
