# Jfinal JustAuth Demo

> 此 demo 主要为了演示 Jfinal 如何通过 JustAuth 快速集成第三方平台的登录，如果有小伙伴是基于 Spring Boot 的可以参考这个 [**` demo`**](https://github.com/xkcoding/spring-boot-demo/tree/master/spring-boot-demo-social)
https://github.com/xkcoding/spring-boot-demo/tree/master/spring-boot-demo-social

## 步骤

### 1. 创建工程

使用 idea 或者 eclipse 创建一个最简单的 maven 工程

### 2. 添加依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.xkcoding</groupId>
  <artifactId>jfinal-justauth-demo</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <name>jfinal-justauth-demo</name>

  <description>
    JFinal 使用 JustAuth 快速集成第三方登录
  </description>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>com.jfinal</groupId>
      <artifactId>jfinal-undertow</artifactId>
      <version>1.7</version>
    </dependency>
    <dependency>
      <groupId>com.jfinal</groupId>
      <artifactId>jfinal</artifactId>
      <version>4.3</version>
    </dependency>
    <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-all</artifactId>
      <version>4.5.16</version>
    </dependency>
    <dependency>
      <groupId>me.zhyd.oauth</groupId>
      <artifactId>JustAuth</artifactId>
      <version>1.8.1</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <finalName>jfinal-justauth-demo</finalName>
    <pluginManagement>
      <plugins>
        <plugin>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.1.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>2.22.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-war-plugin</artifactId>
          <version>3.2.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-install-plugin</artifactId>
          <version>2.5.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-deploy-plugin</artifactId>
          <version>2.8.2</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>
```

### 3. 添加配置类

```java
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
```

### 4. 添加Controller处理

```java
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
        redirect(authRequest.authorize());
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
        // 移除校验通过的state
        AuthState.delete(oauthType);

        renderJson(response);
    }

    private AuthRequest getAuthRequest(String oauthType) {
        AuthSource authSource = AuthSource.valueOf(oauthType.toUpperCase());
        String state = AuthState.create(oauthType);
        switch (authSource) {
            case QQ:
                return getQqAuthRequest(state);
            case GITHUB:
                return getGithubAuthRequest(state);
            case MI:
                return getMiAuthRequest(state);
            default:
                throw new RuntimeException("暂不支持的第三方登录");
        }
    }

    private AuthRequest getQqAuthRequest(String state) {
        AuthConfig authConfig = AuthConfig.builder().clientId(PropKit.get("qq.clientId")).clientSecret(PropKit.get("qq.clientSecret")).redirectUri(PropKit.get("qq.redirectUri")).state(state).build();
        return new AuthQqRequest(authConfig);
    }

    private AuthRequest getGithubAuthRequest(String state) {
        AuthConfig authConfig = AuthConfig.builder().clientId(PropKit.get("github.clientId")).clientSecret(PropKit.get("github.clientSecret")).redirectUri(PropKit.get("github.redirectUri")).state(state).build();
        return new AuthGithubRequest(authConfig);
    }

    private AuthRequest getMiAuthRequest(String state) {
        AuthConfig authConfig = AuthConfig.builder().clientId(PropKit.get("mi.clientId")).clientSecret(PropKit.get("mi.clientSecret")).redirectUri(PropKit.get("mi.redirectUri")).state(state).build();
        return new AuthMiRequest(authConfig);
    }
}
```

### 5. 添加配置文件
```properties
qq.clientId=10********85
qq.clientSecret=1f7********************629e
qq.redirectUri=http://oauth.xkcoding.com/demo/oauth/callback/qq

github.clientId=2***************916
github.clientSecret=e4e8cb602c87cf**********************a6f095b
github.redirectUri=http://oauth.xkcoding.com/demo/oauth/callback/github

mi.clientId=288****************994
mi.clientSecret=nFe****************=
mi.redirectUri=http://oauth.xkcoding.com/demo/oauth/callback/mi
```

### 6. 添加启动类

```java
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
```

---

Enjoy ~