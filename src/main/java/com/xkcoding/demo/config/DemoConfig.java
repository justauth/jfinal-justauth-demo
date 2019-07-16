package com.xkcoding.demo.config;

import com.jfinal.config.*;
import com.jfinal.template.Engine;
import com.xkcoding.demo.controller.OauthController;
import com.xkcoding.demo.controller.TestController;

/**
 * <p>
 * 配置类
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-07-16 14:19
 */
public class DemoConfig extends JFinalConfig {
    /**
     * 配置环境基础信息
     */
    @Override
    public void configConstant(Constants constants) {
        // 设置开发模式，打印请求日志
        constants.setDevMode(true);
    }

    /**
     * 配置路由信息
     */
    @Override
    public void configRoute(Routes routes) {
        routes.add("/demo/test", TestController.class);
        routes.add("/demo/oauth", OauthController.class);
    }

    /**
     * 配置模板引擎相关
     */
    @Override
    public void configEngine(Engine engine) {

    }

    /**
     * 配置插件相关
     */
    @Override
    public void configPlugin(Plugins plugins) {

    }

    /**
     * 配置拦截器相关
     */
    @Override
    public void configInterceptor(Interceptors interceptors) {

    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
