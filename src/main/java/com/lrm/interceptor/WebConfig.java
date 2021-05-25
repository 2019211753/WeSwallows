package com.lrm.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 拦截路径管理类
 *
 * @author 山水夜止
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 允许游客访问登录页、注册页 (对应的路径url)
     * spring2更新之后需要取消拦截静态资源了 并且在打开页面时，也需要在请求头中包含token
     * 还有一个/error转发的问题
     * 1. 打开直接调用API的html文件，在拦截器中放开对html的拦截，但对API不放开
     * 即未登录仍然返回401
     * 2. 打开没直接调用API的资源 即没有登录也可以访问的资源，要进行拦截。注意区分哪些是要放开的，
     * 如登陆注册的背景图
     *
     * @param registry 继承父类
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register")
                .excludePathPatterns("/layui-v2.5.7/**")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/backstage.html")
                .excludePathPatterns("/404.html")
                .excludePathPatterns("classify.html")
                .excludePathPatterns("/comment.html")
                .excludePathPatterns("/editmine.html")
                .excludePathPatterns("/editor.html")
                .excludePathPatterns("/files.html")
                .excludePathPatterns("/homepage.html")
                .excludePathPatterns("/honor.html")
                .excludePathPatterns("/loginn.html")
                .excludePathPatterns("/message.html")
                .excludePathPatterns("/mine.html")
                .excludePathPatterns("/others.html")
                .excludePathPatterns("/search.html")
                .excludePathPatterns("/signin.html");
        registry.addInterceptor(new AuthorityInterceptor())
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    告知系统static 当成 静态资源访问

        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + path);
    }

}
