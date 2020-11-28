package restdoc.client.dubbo.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void configureMessageConverters(
//            List<HttpMessageConverter<?>> converters) {
//
//        converters.add(createXmlHttpMessageConverter());
//        converters.add(new MappingJackson2HttpMessageConverter());
//    }
//
//    @Bean
//    public HttpMessageConverter<Object> createXmlHttpMessageConverter() {
//        return new MarshallingHttpMessageConverter();
//    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        //set path extension to true
        configurer.favorPathExtension(true).
                //set favor parameter to false
                        favorParameter(false).
                //ignore the accept headers
                        ignoreAcceptHeader(true).
                //dont use Java Activation Framework since we are manually specifying the mediatypes required below
                        useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON).
                mediaType("xml", MediaType.APPLICATION_XML).
                mediaType("json", MediaType.APPLICATION_JSON);
    }
}
