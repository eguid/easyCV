package cc.eguid.cv.web.videoimageshotweb.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cc.eguid.cv.corelib.videoimageshot.FFmpegScreenshot;
import cc.eguid.cv.corelib.videoimageshot.Screenshot;
import cc.eguid.cv.web.videoimageshotweb.dao.VideoshotInfoMapper;
import cc.eguid.cv.web.videoimageshotweb.pojo.ScreenshotInfo;
import cc.eguid.cv.web.videoimageshotweb.pojo.ShotParam;
import cc.eguid.cv.web.videoimageshotweb.pojo.VideoshotInfo;
import cc.eguid.cv.web.videoimageshotweb.util.CommonUtil;

@Service("videoShotService")
public class VideoShotServiceImpl implements VideoShotService {
	Logger log =LoggerFactory.getLogger(this.getClass());
	
	@Value("${shot.dir}")
	protected String dir;
	
	@Value("${shot.url}")
	protected String url;
	
	public static final Screenshot shoter=new FFmpegScreenshot();
	
	@Autowired
	VideoshotInfoMapper shotMapper;
	
	@Override
	public VideoshotInfo shot(ShotParam param) throws Exception {
		long now=System.currentTimeMillis();
		String src=param.getSrc(),out = param.getOutput(),fmt=param.getFmt(),name=param.getName();
		Integer needBase64=param.getNeedBase64();
		Integer width=param.getWidth(),height=param.getHeight();
		String base64;
		boolean isSuccess=false;
		if(out==null) {
			out=dir;
		}
		try {
			ScreenshotInfo info=new ScreenshotInfo();
			//需要转换base64编码
			// 是否需要base64编码，0或空只要文件，不要base64编码（默认），1-只返回base64编码，2-base64和文件都需要
			if(needBase64 == null||needBase64==0) {
				//只保存文件
				String outfilepath=out+name+"."+fmt;
				isSuccess=shoter.shot(src,outfilepath,fmt,width,height);
				info.setImgfile(out);
			}else if (needBase64==1){
				//只返回base64
				base64=shoter.getImgBase64(src, fmt,width,height);
				isSuccess=(base64!=null);
				info.setBase64(base64);
			}else if(needBase64==2) {
				//既要保存文件也转换base64
				String outfilepath=out+name+"."+fmt;
				base64=shoter.shotAndGetBase64(src, outfilepath, fmt,width,height);
				isSuccess=(base64!=null);
				info.setBase64(base64);
				info.setImgfile(out);
			}
			if(isSuccess) {
				log.info("截图成功，正在保存数据");
				return save(param,info);
			}
			
			throw new RuntimeException("截图操作失败");
		} catch (Exception e) {
			log.error("截图失败，报错：",e);
			throw e;
		}finally {
			System.err.println("总耗时："+(System.currentTimeMillis()-now));
		}
		
	}
	
	/*保存*/
	public VideoshotInfo save(ShotParam param, ScreenshotInfo info) {
		String src=param.getSrc(),fmt=param.getFmt(),name=param.getName();
		Integer width=param.getWidth(),height=param.getHeight();
		String file=info.getImgfile();
		Integer id=Integer.valueOf(name);
		String requrl=(url+name+"."+fmt);
		VideoshotInfo videoshotinfo=new VideoshotInfo(CommonUtil.UUID(),src,file,requrl,fmt,width,height,new Date(),id);
		
		if(shotMapper.insertSelective(videoshotinfo)==1) {
			log.info("保存截图数据成功");
			videoshotinfo.setBase64(info.getBase64());
			return videoshotinfo;
		}
		log.error("保存截图数据失败");
		
		info=null;param=null;
		return videoshotinfo;
	}

	@Override
	public List<VideoshotInfo> list(VideoshotInfo info) {
		return shotMapper.selectBySelective(info);
	}

	@Override
	public VideoshotInfo get(String id) {
		return shotMapper.selectByPrimaryKey(id);
	}

}
