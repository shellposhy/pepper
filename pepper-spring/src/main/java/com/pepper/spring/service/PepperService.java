package com.pepper.spring.service;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.pepper.spring.annotation.Pepper;
import com.pepper.spring.enums.EIndexOperate;

import static com.pepper.spring.util.Objects.doc;

import static cn.com.lemon.annotation.Reflections.get;
import static cn.com.lemon.base.Strings.isNullOrEmpty;

public class PepperService implements ApplicationContextAware, InitializingBean {
	private final static Logger LOG = LoggerFactory.getLogger(PepperService.class.getName());
	private final static String OPERATE_TYPE = "operate";
	@Resource
	private IndexService indexService;

	// Lucene index path
	private String indexAddress;

	public PepperService(String indexAddress, IndexService indexService) {
		this.indexAddress = indexAddress;
	}

	public PepperService register(Object clazz) {
		LOG.info("Service registration container:{}", clazz.getClass().getName());
		Pepper pepper = clazz.getClass().getAnnotation(Pepper.class);
		Object operate = get(clazz, OPERATE_TYPE);
		if (null != pepper) {
			operate = operate == null ? EIndexOperate.INSERT : operate;
			Document doc = doc(clazz);
			if (null != doc) {
				String path = this.indexAddress;
				path = path.endsWith("/") ? path : path + "/";
				String dataAddress = isNullOrEmpty(pepper.path()) ? clazz.getClass().getSimpleName() : pepper.path();
				path = dataAddress.startsWith("/") ? path + dataAddress.substring(1) : path + dataAddress;
				indexService.index(doc, path);
			}
		}
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(Pepper.class);
		if (serviceBeanMap.size() > 0) {
			for (Object object : serviceBeanMap.values()) {
				Pepper pepper = object.getClass().getAnnotation(Pepper.class);
				Object operate = get(object, OPERATE_TYPE);
				if (null != pepper) {
					operate = operate == null ? EIndexOperate.INSERT : operate;
					Document doc = doc(object);
					if (null != doc) {
						String path = this.indexAddress;
						path = path.endsWith("/") ? path : path + "/";
						String dataAddress = isNullOrEmpty(pepper.path()) ? object.getClass().getSimpleName()
								: pepper.path();
						path = dataAddress.startsWith("/") ? path + dataAddress.substring(1) : path + dataAddress;
						indexService.index(doc, path);
					}
				}
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	public String getIndexAddress() {
		return indexAddress;
	}

}
