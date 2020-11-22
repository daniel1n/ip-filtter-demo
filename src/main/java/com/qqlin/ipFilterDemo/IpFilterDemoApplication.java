package com.qqlin.ipFilterDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author lin.qingquan
 */
@SpringBootApplication
@ServletComponentScan("com.qqlin.ipFilterDemo")
public class IpFilterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpFilterDemoApplication.class, args);
    }

}
