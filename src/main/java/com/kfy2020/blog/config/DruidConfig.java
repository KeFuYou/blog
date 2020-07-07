package com.kfy2020.blog.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



/*** 应为我们是读取单独配置的datasource.yml 数据源配置文件 所以需要指定去 classpath:datasource.yml
 文件里面找 prefix = "spring.datasource" 再绑定到 DruidDataSource的属性上去
 而 @PropertySource 注解 又不支持 读取.yml类型的配置文件，所以我这里自定义了一个 YamlPropertySourceFactory
 类，去让 @PropertySource 注解 可以识别 .yml类型的配置文件
 具体实现可以看我的另一篇博客：https://blog.csdn.net/sgl520lxl/article/details/102139764
 里面的（拓展:） 部分是怎么实现的。*/
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:datasource.yml")
@Configuration
public class DruidConfig {

    //根据配置文件绑定属性，生成 DruidDataSource 组件 注入到ioc容器
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
        return  new DruidDataSource();
    }

    //配置Druid的监控
    //1、配置一个管理后台的Servlet
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> initParams = new HashMap<>();

        initParams.put("loginUsername","kfy");//druid 后台登录名
        initParams.put("loginPassword","4152736429");//druid 后台登录密码
        //initParams.put("allow","");//默认就是允许所有访问
        //initParams.put("deny","192.168.15.21");//拒绝的ip

        bean.setInitParameters(initParams);
        return bean;
    }

    //2、配置一个web监控的filter
    @Bean
    public FilterRegistrationBean webStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String,String> initParams = new HashMap<>();
        //放行静态文件和druid
        initParams.put("exclusions","*.js,*.css,/druid/*");
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return  bean;
    }
}
