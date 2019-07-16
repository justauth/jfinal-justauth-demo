package com.xkcoding.demo;

import com.jfinal.server.undertow.UndertowServer;
import com.xkcoding.demo.config.DemoConfig;

/**
 * <p>
 * 启动类
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-07-16 14:18
 */
public class DemoApplication {
    public static void main(String[] args) {
        UndertowServer.start(DemoConfig.class, 8080, true);
    }
}
