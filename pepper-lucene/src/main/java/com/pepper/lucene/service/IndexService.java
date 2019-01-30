package com.pepper.lucene.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pepper.lucene.Configuration;
import com.pepper.lucene.Configuration.DirectoryType;
import com.pepper.lucene.PepperException;
import com.pepper.lucene.Peppering;
import com.pepper.lucene.common.Operator;
import com.pepper.lucene.common.PepperResult;
import com.pepper.lucene.comparator.base.PepperSortField;

import static cn.com.lemon.base.Preasserts.checkArgument;
import static cn.com.lemon.base.Strings.isNullOrEmpty;

/**
 * The Pepper service API
 * <p>
 * contains the all methods
 * 
 * @author shellpo shih
 * @see IndexDao
 * @see Logger
 * @version 1.0
 */
public class IndexService implements IndexDao {
	private static final transient Logger LOG = LoggerFactory.getLogger(IndexService.class.getName());
	private static Map<String, IndexService> serviceMap = new HashMap<String, IndexService>();
	private Map<String, Peppering> indexMap;
	private Configuration indexConfig;

	/* ================================================ */
	/* =============The Index Initialize ==================== */
	/* =============================================== */
	private IndexService() {
		this.indexMap = new HashMap<String, Peppering>();
	}

	public static IndexService getInstance(String name) {
		IndexService indexService = (IndexService) serviceMap.get(name);
		if (indexService == null) {
			indexService = new IndexService();
			serviceMap.put(name, indexService);
		}
		return indexService;
	}

	private Directory getDirectory(DirectoryType directoryType, String indexPath) throws PepperException {
		LOG.debug("Before read or write index files, The file path is not allowed to be empty!");
		checkArgument(!isNullOrEmpty(indexPath),
				"Before read or write index files, The file path is not allowed to be empty!");
		LockFactory lockFactory = null;
		if (this.indexConfig.isRunOnNFS()) {
			lockFactory = NoLockFactory.getNoLockFactory();
		}
		try {
			switch (directoryType) {
			case NIO_FS:
				return new NIOFSDirectory(new File(indexPath), lockFactory);
			case MMap:
				return new MMapDirectory(new File(indexPath), lockFactory);
			case RAM:
				return new RAMDirectory();
			case Simple_FS:
				return new SimpleFSDirectory(new File(indexPath), lockFactory);
			default:
				break;
			}
			return new SimpleFSDirectory(new File(indexPath), lockFactory);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PepperException(e.getMessage(), e);
		}
	}

	public Peppering getIndex(String indexPath) throws PepperException {
		LOG.debug("Before read or write index files, The file path is not allowed to be empty!");
		Peppering index = (Peppering) this.indexMap.get(indexPath);
		if (index == null) {
			index = new Peppering(getDirectory(this.indexConfig.getDefaultDirectoryType(), indexPath),
					this.indexConfig);
			this.indexMap.put(indexPath, index);
		}
		return index;
	}

	public void closeIndex(String indexPath) throws PepperException {
		Peppering index = (Peppering) this.indexMap.get(indexPath);
		if (index != null) {
			index.close();
			this.indexMap.remove(indexPath);
		}
	}

	public void closeAllOpenedIndexes() throws PepperException {
		if (this.indexMap.size() > 0) {
			for (Peppering index : this.indexMap.values()) {
				index.close();
			}
			this.indexMap.clear();
		}
	}

	public IndexWriterConfig getIndexWriterConfig(String analyzerMode) {
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35,
				this.indexConfig.getAnalyzerFactory().getAnalyzer(analyzerMode));
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		return iwc;
	}

	private <T> T searchOperate(String[] indexPaths, Operator<IndexSearcher, T> operator, boolean ignoreNoIndex)
			throws PepperException {
		IndexSearcher indexSearcher = multiSearch(indexPaths, ignoreNoIndex);
		if (indexSearcher == null) {
			return null;
		}
		try {
			return operator.operate(indexSearcher);
		} catch (Exception e) {
			throw new PepperException(e.getMessage());
		} finally {
			try {
				indexSearcher.close();
			} catch (CorruptIndexException localCorruptIndexException1) {
			} catch (IOException localIOException1) {
			}
		}
	}

	private IndexSearcher multiSearch(String[] indexPaths, boolean ignoreNoIndex) throws PepperException {
		IndexSearcher multiSearcher = null;
		List<IndexReader> readerList = new LinkedList<IndexReader>();
		if (indexPaths == null) {
			for (Peppering index : this.indexMap.values()) {
				try {
					readerList.add(index.getReader());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for (String indexPath : indexPaths) {
				try {
					readerList.add(getIndex(indexPath).getReader());
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		if (readerList.isEmpty()) {
			if (ignoreNoIndex) {
				return null;
			}
			throw new PepperException("No Directory or Index Files！");
		}
		MultiReader mReader = new MultiReader((IndexReader[]) readerList.toArray(new IndexReader[readerList.size()]));
		multiSearcher = new IndexSearcher(mReader);
		return multiSearcher;
	}

	/* ================================================ */
	/* =============The Index Operate ==================== */
	/* =============================================== */

	/* =================Add Document==================== */
	public void addDocument(String indexPath, Document doc) throws PepperException {
		getIndex(indexPath).addDocument(doc);
	}

	public void addDocuments(String indexPath, Collection<Document> docs) throws PepperException {
		getIndex(indexPath).addDocuments(docs);
	}

	/* =================Delete Document==================== */
	public void deleteDocuments(String indexPath, String queryString) throws PepperException {
		getIndex(indexPath).deleteDocuments(queryString);
	}

	public void deleteDocuments(String indexPath, Query query) throws PepperException {
		getIndex(indexPath).deleteDocuments(query);
	}

	public void deleteAll(String indexPath) throws PepperException {
		getIndex(indexPath).deleteAll();
	}

	/* =================Update Document==================== */
	public void updateDocument(String indexPath, String field, String value, Document doc) throws PepperException {
		getIndex(indexPath).updateDocument(field, value, doc);
	}

	public void updateDocuments(String indexPath, String field, String value, Collection<Document> docs)
			throws PepperException {
		getIndex(indexPath).updateDocuments(field, value, docs);
	}

	/* =================Search Document==================== */
	public PepperResult search(String indexPath, String queryString, int firstResult, int maxResult)
			throws PepperException {
		return getIndex(indexPath).search(queryString, firstResult, maxResult);
	}

	public PepperResult search(String indexPath, String queryString, int numHits, int firstResult, int maxResult)
			throws PepperException {
		return getIndex(indexPath).search(queryString, numHits, firstResult, maxResult);
	}

	public PepperResult search(String indexPath, String queryString, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return getIndex(indexPath).search(queryString, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult search(String indexPath, String queryString, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return getIndex(indexPath).search(queryString, numHits, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult search(String indexPath, Query query, PepperSortField[] sortFields, String[] hightLightFields,
			int firstResult, int maxResult) throws PepperException {
		return getIndex(indexPath).search(query, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult search(String indexPath, Query query, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return getIndex(indexPath).search(query, numHits, sortFields, hightLightFields, firstResult, maxResult);
	}

	/* =================mSearch Document==================== */
	public PepperResult mSearch(String[] indexPaths, String queryString, int firstResult, int maxResult)
			throws PepperException {
		return mSearch(indexPaths,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				null, null, firstResult, maxResult);
	}

	public PepperResult mSearch(String[] indexPaths, String queryString, int numHits, int firstResult, int maxResult)
			throws PepperException {
		return mSearch(indexPaths,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				numHits, null, null, firstResult, maxResult);
	}

	public PepperResult mSearch(String[] indexPaths, String queryString, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return mSearch(indexPaths,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult mSearch(String[] indexPaths, String queryString, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return mSearch(indexPaths,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				numHits, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult mSearch(String[] indexPaths, Query query, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return mSearch(indexPaths, query, 0, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult mSearch(String[] indexPaths, Query query, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return multiSearch(indexPaths, query, numHits, sortFields, hightLightFields, firstResult, maxResult, true);
	}

	private PepperResult multiSearch(String[] indexPaths, final Query query, final int numHits,
			final PepperSortField[] sortFields, final String[] hightLightFields, final int firstResult,
			final int maxResult, boolean ignoreNoIndex) throws PepperException {
		return searchOperate(indexPaths, new Operator<IndexSearcher, PepperResult>() {
			public PepperResult operate(IndexSearcher is) throws Exception {
				int myNumHits = numHits >= 0 ? numHits : is.maxDoc();
				if (myNumHits == 0) {
					myNumHits = IndexService.this.indexConfig.getDefaultNumHits();
				}
				TopDocsCollector<?> collector = Peppering.lowLevelSearch(is, query, myNumHits, sortFields);
				TopDocs topDocs = collector.topDocs(firstResult, maxResult);
				ScoreDoc[] scoreDocs = topDocs.scoreDocs;
				PepperResult sr = new PepperResult(topDocs);
				if (sr.totalHits > myNumHits) {
					sr.totalHits = myNumHits;
				}
				Document doc = null;
				int numDocs = topDocs.scoreDocs.length;
				if (numDocs > 0) {
					Document[] docs = new Document[numDocs];
					if (hightLightFields == null) {
						for (int i = 0; i < numDocs; i++) {
							docs[i] = is.doc(scoreDocs[i].doc);
						}
					} else {
						Highlighter highlighter = null;
						highlighter = new Highlighter(
								new SimpleHTMLFormatter(IndexService.this.indexConfig.getHightLightPreTag(),
										IndexService.this.indexConfig.getHightLightPostTag()),
								new QueryScorer(query));
						highlighter.setTextFragmenter(
								new SimpleFragmenter(IndexService.this.indexConfig.getFragmentSize()));
						String tmpValue = null;
						for (int i = 0; i < numDocs; i++) {
							doc = is.doc(scoreDocs[i].doc);
							for (String hightLightField : hightLightFields) {
								Field lcField = (Field) doc.getFieldable(hightLightField);
								if (lcField != null) {
									tmpValue = doc.get(hightLightField);
									if ((tmpValue != null) && (!tmpValue.isEmpty())) {
										tmpValue = highlighter.getBestFragment(
												IndexService.this.indexConfig.getAnalyzerFactory()
														.getAnalyzer(IndexService.this.indexConfig
																.getHightLightAnalyzerMode()),
												hightLightField, tmpValue);
										if (tmpValue != null) {
											lcField.setValue(tmpValue);
										}
									}
								}
							}
							docs[i] = doc;
						}
					}
					sr.documents = docs;
				}
				return sr;
			}
		}, ignoreNoIndex);
	}

	public PepperResult searchInAllOpenedIndexes(String queryString, int firstResult, int maxResult)
			throws PepperException {
		return multiSearch(null,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				0, null, null, firstResult, maxResult, false);
	}

	public PepperResult searchInAllOpenedIndexes(String queryString, int numHits, int firstResult, int maxResult)
			throws PepperException {
		return multiSearch(null,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				numHits, null, null, firstResult, maxResult, false);
	}

	public PepperResult searchInAllOpenedIndexes(String queryString, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return multiSearch(null,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				0, sortFields, hightLightFields, firstResult, maxResult, false);
	}

	public PepperResult searchInAllOpenedIndexes(String queryString, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return multiSearch(null,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				numHits, sortFields, hightLightFields, firstResult, maxResult, false);
	}

	public PepperResult searchInAllOpenedIndexes(Query query, PepperSortField[] sortFields, String[] hightLightFields,
			int firstResult, int maxResult) throws PepperException {
		return multiSearch(null, query, 0, sortFields, hightLightFields, firstResult, maxResult, false);
	}

	public PepperResult searchInAllOpenedIndexes(Query query, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException {
		return multiSearch(null, query, numHits, sortFields, hightLightFields, firstResult, maxResult, false);
	}

	/* =================Count Document==================== */
	public int numDocs(String indexPath) throws PepperException {
		return getIndex(indexPath).numDocs();
	}

	public int count(String indexPath, String queryString, int numHits) throws PepperException {
		return getIndex(indexPath).count(queryString, numHits);
	}

	public int count(String indexPath, Query query, int numHits) throws PepperException {
		return getIndex(indexPath).count(query, numHits);
	}

	public int mCount(String[] indexPaths, String queryString, int numHits) throws PepperException {
		return mCount(indexPaths,
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				numHits);
	}

	public int mCount(String[] indexPaths, Query query, int numHits) throws PepperException {
		return multiCount(indexPaths, query, numHits, true);
	}

	public int countInAllOpenedIndexes(String queryString, int numHits) throws PepperException {
		return countInAllOpenedIndexes(
				Peppering.parseQueryString(queryString,
						this.indexConfig.getAnalyzerFactory().getAnalyzer(this.indexConfig.getParserAnalyzerMode())),
				numHits);
	}

	public int countInAllOpenedIndexes(Query query, int numHits) throws PepperException {
		return multiCount(null, query, numHits, false);
	}

	private int multiCount(String[] indexPaths, final Query query, final int numHits, boolean ignoreNoIndex)
			throws PepperException {
		return searchOperate(indexPaths, new Operator<IndexSearcher, Integer>() {
			public Integer operate(IndexSearcher is) throws Exception {
				int myNumHits = numHits >= 0 ? numHits : is.maxDoc();
				if (myNumHits == 0) {
					myNumHits = IndexService.this.indexConfig.getDefaultNumHits();
				}
				TopDocsCollector<?> collector = Peppering.lowLevelSearch(is, query, myNumHits, null);
				int c = collector.getTotalHits();
				return Integer.valueOf(c > myNumHits ? myNumHits : c);
			}
		}, ignoreNoIndex).intValue();
	}

	/* ================================================ */
	/* =============The Index Getter And Setter ============== */
	/* =============================================== */
	public void setIndexConfig(Configuration indexConfig) {
		this.indexConfig = indexConfig;
	}

	public Configuration getIndexConfig() {
		return this.indexConfig;
	}
}
