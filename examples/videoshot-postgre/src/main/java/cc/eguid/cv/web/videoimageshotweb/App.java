package cc.eguid.cv.web.videoimageshotweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cc.eguid.cv.web.videoimageshotweb.dao")
public class App {
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
