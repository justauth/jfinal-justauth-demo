package com.xkcoding.demo.controller;

import com.jfinal.core.Controller;

/**
 * <p>
 * 测试Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-07-16 14:21
 */
public class TestController extends Controller {
    /**
     * 默认调用 index 方法
     */
    public void index(){
        renderText("hello world test");
    }
}
