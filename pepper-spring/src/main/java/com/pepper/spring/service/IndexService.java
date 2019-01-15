package com.pepper.spring.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pepper.lucene.Configuration;
import com.pepper.lucene.PepperException;
import com.pepper.lucene.analyzer.IAnalyzer;
import com.pepper.lucene.analyzer.PaodingAnalyzer;
import com.pepper.lucene.common.PepperResult;
import com.pepper.lucene.comparator.base.PepperSortField;
import com.pepper.lucene.comparator.base.PepperSortField.FieldType;
import com.pepper.lucene.service.IndexDao;
import com.pepper.spring.util.Resources;

import static cn.com.lemon.base.Strings.isNullOrEmpty;
import static cn.com.lemon.base.Preasserts.checkArgument;

@Service
public class IndexService {
	private static final Logger LOG = LoggerFactory.getLogger(IndexService.class.getName());
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

	/**
	 * Search engine search service
	 * <p>
	 * search condition<blockquote>
	 * 
	 * <pre>
	*     Title:China
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * 
	 * @param queryString
	 *            search condition
	 * @param hits
	 *            data sampling number
	 * @param sortFields
	 *            search result sort field
	 * @param hightLightFields
	 *            search result high light sort field
	 * @param first
	 *            paging first number
	 * @param size
	 *            paging size
	 * @param path
	 *            the index storage address
	 * @return {@link PepperResult}
	 */
	public PepperResult search(String queryString, Integer hits, PepperSortField[] sortFields,
			String[] hightLightFields, Integer first, Integer size, String... path) {
		checkArgument(null != path && path.length > 0, "When data retrieval, index storage address is not empty!");
		PepperResult result = new PepperResult();
		// if query condition is null or empty,default search all
		if (isNullOrEmpty(queryString))
			queryString = "*:*";
		/***/
		LOG.debug("Query condition:[" + queryString + "]");
		try {
			if (path.length == 1) {
				return indexService.search(path[0], queryString, hits, sortFields, hightLightFields, first, size);
			} else {
				return indexService.mSearch(path, queryString, hits, sortFields, hightLightFields, first, size);
			}
		} catch (PepperException e) {
			LOG.error("There is an exception in search engine search!");
			e.printStackTrace();
			result.documents = null;
			result.totalHits = 0;
			return result;
		}
	}

	public PepperResult search(String queryString, Integer first, Integer size, String... path) {
		PepperSortField[] sortFields = { new PepperSortField("DocTime", FieldType.StringVal, true) };
		// Default data sampling: 1 million
		// Default data sort: date desc
		return search(queryString, 1000000, sortFields, null, first, size, path);
	}

	
}
