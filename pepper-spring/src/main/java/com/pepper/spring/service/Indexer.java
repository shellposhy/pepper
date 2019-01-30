package com.pepper.spring.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.lucene.Configuration;
import com.pepper.lucene.PepperException;
import com.pepper.lucene.analyzer.IAnalyzer;
import com.pepper.lucene.analyzer.PaodingAnalyzer;
import com.pepper.lucene.common.PepperResult;
import com.pepper.lucene.comparator.base.PepperSortField;
import com.pepper.lucene.comparator.base.PepperSortField.FieldType;
import com.pepper.lucene.service.IndexDao;

import static cn.com.lemon.base.Strings.isNullOrEmpty;
import static cn.com.lemon.base.Preasserts.checkArgument;

public class Indexer {
	private static final Logger LOG = LoggerFactory.getLogger(Indexer.class.getName());
	private IndexDao indexService;
	// configuration parameter
	private String dic;
	private String preTag;
	private String postTag;
	private String indexName;

	public Indexer(String dic, String preTag, String postTag, String indexName) {
		this.dic = dic;
		this.preTag = preTag;
		this.postTag = postTag;
		this.indexName = indexName;
		Configuration indexConfig = new Configuration();
		indexConfig.setAnalyzerFactory(new PaodingAnalyzer(this.dic));
		indexConfig.setHightLightPreTag(this.preTag);
		indexConfig.setHightLightPostTag(this.postTag);
		indexConfig.setHightLightAnalyzerMode(IAnalyzer.MAX_WORD_LENGTH_MODE);
		indexService = com.pepper.lucene.service.IndexService.getInstance(this.indexName);
		indexService.setIndexConfig(indexConfig);
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

	/**
	 * The index generated
	 * 
	 * @param doc
	 * @param path
	 * @return
	 */
	public boolean index(Document doc, String... path) {
		checkArgument(null != path && path.length > 0, "When data index save, index storage address is not empty!");
		try {
			if (path.length == 1) {
				indexService.addDocument(path[0], doc);
				return true;
			} else {
				for (String address : path) {
					indexService.addDocument(address, doc);
				}
				return true;
			}
		} catch (PepperException e) {
			LOG.error("There is an exception in save index!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Delete index file
	 * 
	 * @param queryString
	 *            search condition
	 * @param path
	 *            the index storage address
	 * @return {@link Boolean}
	 */
	public boolean delete(String queryString, String... path) {
		checkArgument(null != path && path.length > 0, "When data index delete, index storage address is not empty!");
		checkArgument(!isNullOrEmpty(queryString), "When data index delete, delete condition is not empty!");
		try {
			if (path.length == 1) {
				indexService.deleteDocuments(path[0], queryString);
			} else {
				for (String address : path) {
					indexService.deleteDocuments(address, queryString);
				}
			}
			return true;
		} catch (PepperException e) {
			LOG.error("There is an exception in save index!");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Delete index file base on index key value {@code UUID}
	 * 
	 * @param uuid
	 *            index key value
	 * @param path
	 *            the index storage address
	 * @return {@link Boolean}
	 */
	public boolean delete(String uuid, String path) {
		checkArgument(!isNullOrEmpty(uuid) && !isNullOrEmpty(path),
				"When data index delete, delete condition and path are not empty!");
		try {
			indexService.deleteDocuments(path, new TermQuery(new Term("UUID", uuid)));
			return true;
		} catch (PepperException e) {
			e.printStackTrace();
			return false;
		}
	}

	/** getter and setter method */
	public String getDic() {
		return dic;
	}

	public String getPreTag() {
		return preTag;
	}

	public String getPostTag() {
		return postTag;
	}

	public String getIndexName() {
		return indexName;
	}

}
