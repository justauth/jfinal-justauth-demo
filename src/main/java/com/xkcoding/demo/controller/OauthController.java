package com.xkcoding.demo.controller;

import cn.hutool.core.lang.Dict;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.PropKit;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthMiRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;

/**
 * <p>
 * Oauth Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-07-16 14:39
 */
public class OauthController extends Controller {
    // 加载配置文件
    static {
        PropKit.use("oauth.properties");
    }

    public void index() {
        // @formatter:off
        Dict dictType = Dict.create()
                .set("QQ登录", "http://oauth.xkcoding.com/demo/oauth/login/qq")
                .set("GitHub登录", "http://oauth.xkcoding.com/demo/oauth/login/github")
                .set("小米登录", "http://oauth.xkcoding.com/demo/oauth/login/mi");
        // @formatter:on
        renderJson(dictType);
    }

    /**
     * 参数获取参考：https://www.jfinal.com/doc/3-4
     * Jfinal 中路径参数只能取最后一个，分隔符默认为 - 比如 /demo/para0-para1-para2
     * 此时可通过 getPara(0) -> para0 ; getPara(1) -> para1 ; getPara(2) -> para2
     */
    public void login() {
        String oauthType = getPara(0);
        AuthRequest authRequest = getAuthRequest(oauthType);
        redirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    /**
     * 登录成功后的回调
     * 坑点: 因为Jfinal不支持 /{oauthType}/callback 这种格式的URL，因此回调地址修改如下 /callback/{oauthType}
     *
     * @param callback 携带返回的信息
     */
    public void callback(@Para("") AuthCallback callback) {
        String oauthType = getPara(0);
        AuthRequest authRequest = getAuthRequest(oauthType);
        AuthResponse response = authRequest.login(callback);
        renderJson(response);
    }

    private AuthRequest getAuthRequest(String oauthType) {
        AuthSource authSource = AuthSource.valueOf(oauthType.toUpperCase());
        switch (authSource) {
            case QQ:
                return getQqAuthRequest();
            case GITHUB:
                return getGithubAuthRequest();
            case MI:
                return getMiAuthRequest();
            default:
                throw new RuntimeException("暂不支持的第三方登录");
        }
    }

    private AuthRequest getQqAuthRequest() {
        AuthConfig authConfig = AuthConfig.builder().clientId(PropKit.get("qq.clientId")).clientSecret(PropKit.get("qq.clientSecret")).redirectUri(PropKit.get("qq.redirectUri")).build();
        return new AuthQqRequest(authConfig);
    }

    private AuthRequest getGithubAuthRequest() {
        AuthConfig authConfig = AuthConfig.builder().clientId(PropKit.get("github.clientId")).clientSecret(PropKit.get("github.clientSecret")).redirectUri(PropKit.get("github.redirectUri")).build();
        return new AuthGithubRequest(authConfig);
    }

    private AuthRequest getMiAuthRequest() {
        AuthConfig authConfig = AuthConfig.builder().clientId(PropKit.get("mi.clientId")).clientSecret(PropKit.get("mi.clientSecret")).redirectUri(PropKit.get("mi.redirectUri")).build();
        return new AuthMiRequest(authConfig);
    }
}
