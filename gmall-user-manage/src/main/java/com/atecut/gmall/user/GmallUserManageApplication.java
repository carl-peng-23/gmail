package com.atecut.gmall.user;

import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.context.annotation.ComponentScan;
		import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.atecut.gmall.user.mapper")
@SpringBootApplication
@ComponentScan("com.atecut.gmall")
public class GmallUserManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallUserManageApplication.class, args);
	}

}
