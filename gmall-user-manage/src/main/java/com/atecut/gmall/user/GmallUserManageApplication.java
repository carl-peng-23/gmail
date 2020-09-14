package com.atecut.gmall.user;

		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.atecut.gmall.user.mapper")
@SpringBootApplication
public class GmallUserManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallUserManageApplication.class, args);
	}

}
