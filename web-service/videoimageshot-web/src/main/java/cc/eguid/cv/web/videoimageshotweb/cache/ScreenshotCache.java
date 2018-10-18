package cc.eguid.cv.web.videoimageshotweb.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.eguid.cv.web.videoimageshotweb.pojo.ScreenshotHistory;

/**
 * 截图缓存
 * 
 * @author eguid
 *
 */
public class ScreenshotCache {
	public static Map<String, ScreenshotHistory> historyList = new ConcurrentHashMap<>();

	public static Collection<ScreenshotHistory> getHistoryList() {
		return historyList.values();
	}

	/**
	 * 不需要设置id（id由名称和时间组合）
	 * 
	 * @param his
	 * @return
	 * @return
	 */
	public static ScreenshotHistory save(ScreenshotHistory his) {
		his.setId(getId(his));
		return historyList.put(his.getId(), his);
	}

	public static String getId(ScreenshotHistory his) {
		if(his.getName()!=null) {
			return  his.getName()+his.getShottime().getTime();
		}else {
			return  his.getFmt()+his.getShottime().getTime();
		}
	}

	/**
	 * 根据id获取截图记录
	 * @param id
	 * @return
	 */
	public static ScreenshotHistory get(String id) {
		return historyList.get(id);
	}
	
	
}
