package com.pepper.spring.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.pepper.lucene.Configuration;
import com.pepper.lucene.analyzer.IAnalyzer;
import com.pepper.lucene.analyzer.PaodingAnalyzer;
import com.pepper.lucene.service.IndexDao;
import com.pepper.spring.util.Resources;

@Service
public class IndexService {
	private static final String RESOURCE_NAME = "pepper";
	private IndexDao indexService;

	@PostConstruct
	public void init() {
		Resources resources = new Resources(RESOURCE_NAME);
		Configuration indexConfig = new Configuration();
		indexConfig.setAnalyzerFactory(new PaodingAnalyzer(resources.value("paoding.dic.address")));
		indexConfig.setHightLightPreTag(resources.value("high.light.pre.tag"));
		indexConfig.setHightLightPostTag(resources.value("high.light.post.tag"));
		indexConfig.setHightLightAnalyzerMode(IAnalyzer.MAX_WORD_LENGTH_MODE);
		indexService = com.pepper.lucene.service.IndexService.getInstance(resources.value("index.service.name"));
		indexService.setIndexConfig(indexConfig);
		if (null != indexService) {
			resources.close();
		}
	}
}
