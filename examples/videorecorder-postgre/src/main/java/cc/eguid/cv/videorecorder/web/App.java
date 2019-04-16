package cc.eguid.cv.videorecorder.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动springboot
 * @author eguid
 *
 */
@SpringBootApplication
@MapperScan("cc.eguid.cv.videorecorder.web.dao")
public class App {
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
