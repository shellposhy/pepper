package com.pepper.spring;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.pepper.spring.annotation.Pepper;

import static cn.com.lemon.base.Strings.uuid;

public class PepperService implements ApplicationContextAware, InitializingBean {
	private final static Logger LOG = LoggerFactory.getLogger(PepperService.class.getName());

	private Map<String, Object> handlers = new HashMap<String, Object>();

	// Lucene index path
	private String indexAddress;

	public PepperService(String indexAddress) {
		this.indexAddress = indexAddress;
	}

	public PepperService register(String uuid, Object serviceBean) {
		LOG.info("Service registration container:{}", uuid);
		if (!handlers.containsKey(uuid)) {
			handlers.put(uuid, serviceBean);
		}
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(Pepper.class);
		if (serviceBeanMap.size() > 0) {
			for (Object object : serviceBeanMap.values()) {
				handlers.put(uuid(), object);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	public Map<String, Object> getHandlers() {
		return handlers;
	}

	public String getIndexAddress() {
		return indexAddress;
	}

}
