package auth.background;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

//import auth.background.config.DBConfig;

@SpringBootApplication
public class AuthAdminAPIApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(AuthAdminAPIApplication.class, args);
 
	}
	//fastjson
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
	     FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
	     FastJsonConfig fastJsonConfig = new FastJsonConfig();
	     fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
	     fastConverter.setFastJsonConfig(fastJsonConfig);
	     HttpMessageConverter<?> converter = fastConverter;
	     return new HttpMessageConverters(converter);
	  }
}
