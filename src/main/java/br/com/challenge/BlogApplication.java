package br.com.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BlogApplication {

  static void main(String[] args) {
    new SpringApplication(BlogApplication.class).run(args);
  }
}
