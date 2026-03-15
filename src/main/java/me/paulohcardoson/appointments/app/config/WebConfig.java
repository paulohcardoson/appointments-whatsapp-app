package me.paulohcardoson.appointments.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

	private final JwtAuthInterceptor jwtAuthInterceptor;

	public WebConfig(JwtAuthInterceptor jwtAuthInterceptor) {
		this.jwtAuthInterceptor = jwtAuthInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtAuthInterceptor)
			.addPathPatterns("/patients/")
			.addPathPatterns("/patients/create")
			.addPathPatterns("/appointments/")
			.addPathPatterns("/appointments/create")
			.addPathPatterns("/me");
	}
}
