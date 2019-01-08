package com.pepper.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.pepper.spring.annotation.Pepper;

public class PepperService implements ApplicationContextAware, InitializingBean {

	// Lucene index path
	private String indexAddress;

	public PepperService(String indexAddress) {
		this.indexAddress = indexAddress;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(Pepper.class);
	}

}
