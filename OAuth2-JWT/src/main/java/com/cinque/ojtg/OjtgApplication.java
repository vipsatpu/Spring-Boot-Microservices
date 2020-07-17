package com.cinque.ojtg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan( basePackages = {"com.cinque"})
public class OjtgApplication {

	private String apiBasePackage = "com.cinque.ojtg";
	private String apiName = "Cinque Authentication & Authorization API";
	private String apiDescription = "Documentation OAUTH-TFA API v1.0";
	private String apiVersion = "1.0";
	
	public static void main(String[] args) {
		SpringApplication.run(OjtgApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Bean
	public Docket swaggerPersonApi10() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
					.apis(RequestHandlerSelectors.basePackage(apiBasePackage))
					.paths(PathSelectors.any())
				.build()
				.apiInfo(new ApiInfoBuilder().version(apiVersion).title(apiName).description(apiDescription).build());
	}
}
