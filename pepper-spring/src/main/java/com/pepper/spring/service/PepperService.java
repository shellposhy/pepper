package com.pepper.spring.service;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.spring.annotation.Pepper;
import com.pepper.spring.enums.EIndexOperate;

import static com.pepper.spring.util.Objects.doc;

import static cn.com.lemon.annotation.Reflections.get;
import static cn.com.lemon.base.Strings.isNullOrEmpty;

public class PepperService {
	private final static Logger LOG = LoggerFactory.getLogger(PepperService.class.getName());
	private final static String OPERATE_TYPE = "operate";

	// Lucene index path
	private String indexAddress;
	private Indexer indexer;

	public PepperService(String indexAddress, Indexer indexer) {
		this.indexAddress = indexAddress;
		this.indexer = indexer;
	}

	public PepperService register(Object clazz) {
		LOG.info("Service registration container:{}", clazz.getClass().getName());
		Pepper pepper = clazz.getClass().getAnnotation(Pepper.class);
		if (null != pepper) {
			Object operate = get(clazz, OPERATE_TYPE);
			operate = operate == null ? EIndexOperate.INSERT : operate;
			Document doc = doc(clazz);
			if (null != doc) {
				String path = this.indexAddress;
				path = path.endsWith("/") ? path : path + "/";
				String dataAddress = isNullOrEmpty(pepper.path()) ? clazz.getClass().getSimpleName() : pepper.path();
				path = dataAddress.startsWith("/") ? path + dataAddress.substring(1) : path + dataAddress;
				indexer.index(doc, path);
			}
		}
		return this;
	}

	public String getIndexAddress() {
		return indexAddress;
	}

	public Indexer getIndexer() {
		return indexer;
	}
}
