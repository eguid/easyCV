package cc.eguid.cv.videorecorder.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.eguid.cv.videoRecorder.entity.RecordInfo;
import cc.eguid.cv.videorecorder.web.pojo.CameraRecordInfo;
import cc.eguid.cv.videorecorder.web.pojo.ErrorMsg;
import cc.eguid.cv.videorecorder.web.pojo.SuccessMsg;
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
	
	Logger log=LoggerFactory.getLogger(RecordController.class);
	
	@Autowired
	RecordService<CameraRecordInfo> recorderService;
	
	/**
	 * 录像
	 * @return
	 */
	@RequestMapping("/record")
	public ErrorMsg record(@RequestParam(required=true)String src,@RequestParam(required=true)String out) {
		log.info("调用录像");
		
		if(CommonUtil.isAllNullOrEmpty(src,out)) {
			ErrorMsg errormsg=ErrorMsg.createErrorMsg("视频地址和名称不能为空");
			return errormsg;
		}
		
		CameraRecordInfo rt=null;
		try {
			log.info("准备录像");
			rt=recorderService.record(src, out);
			if(rt!=null) {
				SuccessMsg msg=SuccessMsg.createSuccessMsg(rt.getId().toString());
				return msg;
			}
		} catch (Exception e) {
		}
		
		return ErrorMsg.createErrorMsg("操作失败");
	}
	
	/**
	 * 开始摄像机录像
	 * @param src -视频源，目前支持rtmp，目前支持mp4
	 * @param cameraid
	 * @return
	 */
	@RequestMapping("/startRecordCamera")
	public ErrorMsg startRecord(@RequestParam(required=true)String src,@RequestParam(required=true)Integer cameraid) {
		log.info("开始摄像机录像操作："+src+","+cameraid);
		if(CommonUtil.isAllNullOrEmpty(src)||cameraid==null) {
			ErrorMsg errormsg=ErrorMsg.createErrorMsg("视频地址和摄像机ID不能为空");
			return errormsg;
		}
		StringBuilder sb=new StringBuilder().append(cameraid).append(".mp4");
		String out=sb.toString();
		CameraRecordInfo rt=null;
		try {
			log.info("准备录像");
			//是否有正在工作的录像任务
			if(recorderService.existWorking(src, out)) {
				return ErrorMsg.createErrorMsg("录像任务重复，请选择其他摄像机");
			}
			//不存在重复任务，则开始录像
			rt=recorderService.record(cameraid,src, out);
			if(rt!=null) {
				SuccessMsg msg=SuccessMsg.createSuccessMsg("操作成功，正在录像...",rt);
				return msg;
			}
		} catch (Exception e) {
			log.error("录像操作失败",e);
		}
		
		return ErrorMsg.createErrorMsg("操作失败");
	}
	
	/**
	 * 停止摄像机视频录像
	 * @return
	 */
	@RequestMapping("/stopRecordCamera")
	public ErrorMsg stopRecordCamera(@RequestParam(required=true)String src,@RequestParam(required=true)Integer cameraid) {
		log.info("停止摄像机录像操作："+src+","+cameraid);
		if(CommonUtil.isAllNullOrEmpty(src)||cameraid==null) {
			ErrorMsg errormsg=ErrorMsg.createErrorMsg("视频地址和摄像机ID不能为空");
			return errormsg;
		}
		StringBuilder sb=new StringBuilder().append(cameraid).append(".mp4");
		String out=sb.toString();
		CameraRecordInfo param=new CameraRecordInfo();
		param.setSrc(src);
		param.setOut(out);
		//是否存在录像任务
		if(!recorderService.exist(param)) {
			return ErrorMsg.createErrorMsg("录像任务不存在");
		}
		try {
			CameraRecordInfo rt=recorderService.getWork(param);
			if(rt==null||rt.getId()==null) {
				return ErrorMsg.createErrorMsg("重复操作，录像任务已停止工作");
			}
			//停止录像任务
			if(recorderService.stop(rt.getId())) {
				SuccessMsg msg=SuccessMsg.createSuccessMsg("操作成功，已停止录像",rt);
				return msg;
			}
			return ErrorMsg.createErrorMsg("停止录像任务失败，请稍后重试！");
		} catch (Exception e) {
			log.error("操作失败",e);
		}
		return ErrorMsg.createErrorMsg("操作失败");
	}
	
	/**
	 * 停止录像
	 * @return
	 */
	@RequestMapping("/stop")
	public ErrorMsg stop(@RequestParam(required=true)Integer id) {
		log.info("停止录像操作");
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
	public List<CameraRecordInfo> list(CameraRecordInfo info) {
		log.info("获取录像列表："+info);
		return recorderService.list(info);
	}
	
	/**
	 * 根据id查询
	 * @return
	 */
	@RequestMapping("/get")
	public Object get(@RequestParam(required=true)Integer id) {
		log.info("查询录像："+id);
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
