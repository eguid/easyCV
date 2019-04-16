package cc.eguid.cv.web.videoimageshotweb.dao;

import java.util.List;

import cc.eguid.cv.web.videoimageshotweb.pojo.VideoshotInfo;

public interface VideoshotInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(VideoshotInfo record);

    int insertSelective(VideoshotInfo record);

    VideoshotInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(VideoshotInfo record);

    int updateByPrimaryKey(VideoshotInfo record);

    /**
     * 搜索列表
     * @param info
     * @return
     */
	List<VideoshotInfo> selectBySelective(VideoshotInfo info);
}