package cc.eguid.cv.videorecorder.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.eguid.cv.videorecorder.web.dao.RecordInfoMapper;
import cc.eguid.cv.videorecorder.web.pojo.CameraRecordInfo;

/**
 * 默认服务实现(自定义持久化实现)
 * @author eguid
 *
 */
@Service("recordService")
public class DefaultRecordServiceImpl extends RecordServiceTemplate implements RecordService<CameraRecordInfo>{

	@Autowired
	RecordInfoMapper recordMapper;
	
	public List<CameraRecordInfo> list(CameraRecordInfo info) {
		log.info("获取列表：");
		return recordMapper.selectBySelective(info);
	}

	
	public boolean save(CameraRecordInfo info) {
		log.info("保存："+info);
		int ret=-1;
		if(get(info.getId())==null) {
			log.info("插入："+info);
			ret=recordMapper.insert(info);
			log.info("保存后结果："+ret);
		}else{
			log.info("更新："+info);
			ret=recordMapper.update(info);
			log.info("保存后结果："+ret);
		}
		log.info("保存后结果："+ret);
		return ret>=0;
	}

	@Override
	public CameraRecordInfo get(Integer id) {
		log.info("根据ID获取信息："+id);
		CameraRecordInfo ret=recordMapper.selectById(id);
		log.info("根据ID获取信息结果："+ret);
		return ret;
	}

	@Override
	public CameraRecordInfo getWork(CameraRecordInfo info) {
		 List<CameraRecordInfo> list=list(info);
		 for(CameraRecordInfo cr:list) {
			 if(cr.getEndtime()==null) {
				 //服务中是否存在正在工作的录像任务
				 if(existWorking(cr.getSrc(), cr.getOut())) {
					 return cr;
				 }
			 }
		 }
		 return null;
	}

	@Override
	public boolean exist(CameraRecordInfo info) {
		List<CameraRecordInfo> ret=list(info);
		if(ret==null||ret.size()<1) {
			return false;
		}
		return true;
	}



}
