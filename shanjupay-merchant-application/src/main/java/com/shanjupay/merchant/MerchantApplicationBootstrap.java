package com.shanjupay.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*****
 *@Author NJL
 *@Description MerchantApplicationBootstrap 启动类
 */
@SpringBootApplication
//启用发现客户端
@EnableDiscoveryClient
public class MerchantApplicationBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MerchantApplicationBootstrap.class, args);
    }
}
