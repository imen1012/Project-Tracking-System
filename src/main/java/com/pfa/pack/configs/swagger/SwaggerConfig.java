package com.pfa.pack.configs.swagger;

import static springfox.documentation.builders.PathSelectors.regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@Profile(value = {"dev", "qa"})
public class SwaggerConfig {
	
	private static final String BASE_PACKAGE = "com.pfa.pack.controllers.api";
	private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);
	
	static {
		logger.info("************ entering " + SwaggerConfig.class.getName() + " ************");
	}
	
	@Bean
	public Docket productApi() {
		
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
				.paths(regex("/api/.*"))
				.build()
		.apiInfo(this.metaData());
	}

	private ApiInfo metaData() {
		
		final String title = "Spring Boot project for Project management";
		final String description = "Assign projects to employees by managers";
		final String version = "1.0";
		final String termsOfServiceUrl = "Terms of service";
		final String name = "Selim Horri, Imen Touk, Badr Idoudi";
		final String url = "https://github.com/SelimHorri?tab=repositories";
		final String email = "horri.slim@gmail.com";
		final Contact contact = new Contact(name, url, email);
		final String license = "Apache Licence version 2.0";
		final String licenseUrl = "https://apache.org/license.html";
		
		final ApiInfo apiInfo = new ApiInfoBuilder()
													.title(title)
													.description(description)
													.version(version)
													.termsOfServiceUrl(termsOfServiceUrl)
													.contact(contact)
													.license(license)
													.licenseUrl(licenseUrl)
		.build();
		
		return apiInfo;
	}
	
	
	
}









