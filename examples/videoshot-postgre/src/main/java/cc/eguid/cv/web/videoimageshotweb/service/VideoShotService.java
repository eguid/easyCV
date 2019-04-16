package cc.eguid.cv.web.videoimageshotweb.service;

import java.util.List;

import cc.eguid.cv.web.videoimageshotweb.pojo.ShotParam;
import cc.eguid.cv.web.videoimageshotweb.pojo.VideoshotInfo;

/**
 * 视频快照（截图）服务
 * @author eguid
 *
 */
public interface VideoShotService {

	/**
	 * 视频快照（截图）
	 * @param param-参数
	 * @return
	 * @throws Exception 
	 */
	VideoshotInfo shot(ShotParam param) throws Exception;

	/**
	 * 列表
	 * @return
	 */
	List<VideoshotInfo> list(VideoshotInfo info);

	/**
	 * 根据ID获取信息
	 * @param id
	 * @return
	 */
	VideoshotInfo get(String id);
	
}
