package com.pepper.lucene;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.apache.lucene.util.Version;

import com.pepper.lucene.common.Operator;
import com.pepper.lucene.common.PepperResult;
import com.pepper.lucene.comparator.base.PepperSortField;
import com.pepper.lucene.parser.MdQueryParser;

/**
 * The Lucene Index Api
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class Peppering {
	private static final transient Logger LOG = Logger.getLogger(Peppering.class.getName());
	private Directory directory;
	private IndexReader indexReader;
	private Configuration configuration;

	/* ================================================ */
	/* =============The Index Initialize ==================== */
	/* =============================================== */
	public Peppering(Directory directory, Configuration indexConfig) {
		LOG.debug("====init===");
		this.directory = directory;
		this.configuration = indexConfig;
		try {
			if (exists()) {
				this.indexReader = IndexReader.open(directory);
			}
		} catch (IndexNotFoundException indexNotFoundException) {
			indexNotFoundException.printStackTrace();
		} catch (NoSuchDirectoryException noSuchDirectoryException) {
			noSuchDirectoryException.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean exists() throws PepperException {
		try {
			return IndexReader.indexExists(this.directory);
		} catch (IOException e) {
			throw new PepperException(e.getMessage());
		}
	}

	public IndexWriter getWriter(String analyzerMode) throws PepperException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35,
				this.configuration.getAnalyzerFactory().getAnalyzer(analyzerMode));
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		try {
			return new IndexWriter(this.directory, indexWriterConfig);
		} catch (CorruptIndexException e) {
			throw new PepperException(e.getMessage());
		} catch (LockObtainFailedException e) {
			throw new PepperException("The write index process is used！", e);
		} catch (IOException e) {
			throw new PepperException("Read or Write Index Error！", e);
		}
	}

	private void writeOperate(Operator<IndexWriter, Object> operator) throws PepperException {
		IndexWriter indexWriter = getWriter(this.configuration.getWriterAnalyzerMode());
		try {
			operator.operate(indexWriter);
		} catch (Exception e) {
			throw new PepperException(e.getMessage());
		} finally {
			try {
				indexWriter.close();
			} catch (CorruptIndexException localCorruptIndexException) {
			} catch (IOException localIOException) {
			}
		}
	}

	public IndexReader getReader() throws PepperException {
		if (this.indexReader == null) {
			try {
				this.indexReader = IndexReader.open(this.directory);
			} catch (IndexNotFoundException e) {
				throw new PepperException("Not find the index！", e);
			} catch (NoSuchDirectoryException e) {
				throw new PepperException("Try to list a non-existent directory！", e);
			} catch (CorruptIndexException e) {
				throw new PepperException("Lucene detects  an inconsistency in the index！", e);
			} catch (IOException e) {
				throw new PepperException("Failed or  interrupted I/O operations！", e);
			}
		}
		synchronized (this) {
			IndexReader ir = null;
			try {
				ir = IndexReader.openIfChanged(this.indexReader);
			} catch (CorruptIndexException e) {
				throw new PepperException(e.getMessage());
			} catch (IOException e) {
				throw new PepperException("Failed or  interrupted I/O operations！", e);
			}
			if (ir != null) {
				try {
					this.indexReader.close();
				} catch (IOException e) {
					throw new PepperException("The Index Reader Closed Error！", e);
				}
				this.indexReader = ir;
			}
			return this.indexReader;
		}
	}

	private <T> T readOperate(Operator<IndexReader, T> o) throws PepperException {
		IndexReader indexReader = getReader();
		try {
			return o.operate(indexReader);
		} catch (Exception e) {
			throw new PepperException(e.getMessage());
		}
	}

	public IndexSearcher getSearcher() throws PepperException {
		return new IndexSearcher(getReader());
	}

	private <T> T searchOperate(Operator<IndexSearcher, T> operator) throws PepperException {
		IndexSearcher indexSearcher = getSearcher();
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

	public void close() throws PepperException {
		if (this.indexReader != null) {
			try {
				this.indexReader.close();
			} catch (IOException e) {
				throw new PepperException("Closing the index error！", e);
			}
			this.indexReader = null;
		}
		if (this.directory != null) {
			try {
				this.directory.close();
				this.directory = null;
			} catch (IOException e) {
				throw new PepperException("Closing the index error！", e);
			}
		}
		this.configuration = null;
	}

	/* ================================================ */
	/* =============The Index Operate ==================== */
	/* =============================================== */

	/* =================Add Document==================== */
	public void addDocument(final Document doc) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.addDocument(doc);
				return null;
			}
		});
	}

	public void addDocuments(final Collection<Document> docs) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.addDocuments(docs);
				return null;
			}
		});
	}

	/* =================Delete Document==================== */
	public void deleteDocuments(String queryString) throws PepperException {
		deleteDocuments(parseQueryString(queryString,
				this.configuration.getAnalyzerFactory().getAnalyzer(this.configuration.getParserAnalyzerMode())));
	}

	public void deleteDocuments(final Query query) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.deleteDocuments(query);
				return null;
			}
		});
	}

	public void deleteDocuments(final Query... querys) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.deleteDocuments(querys);
				return null;
			}
		});
	}

	public void deleteDocuments(final Term... terms) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.deleteDocuments(terms);
				return null;
			}
		});
	}

	public void deleteDocuments(final Term term) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.deleteDocuments(term);
				return null;
			}
		});
	}

	public void deleteDocuments(String field, String value) throws PepperException {
		deleteDocuments(new Term(field, value));
	}

	public void deleteAll() throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.deleteAll();
				return null;
			}
		});
	}

	/* =================Update Document==================== */
	public void updateDocument(String field, String value, Document doc) throws PepperException {
		updateDocument(new Term(field, value), doc);
	}

	public void updateDocument(final Term term, final Document doc) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.updateDocument(term, doc);
				return null;
			}
		});
	}

	public void updateDocuments(String field, String value, Collection<Document> docs) throws PepperException {
		updateDocuments(new Term(field, value), docs);
	}

	public void updateDocuments(final Term delTerm, final Collection<Document> docs) throws PepperException {
		writeOperate(new Operator<IndexWriter, Object>() {
			public Object operate(IndexWriter iw) throws Exception {
				iw.updateDocuments(delTerm, docs);
				return null;
			}
		});
	}

	/* =================Search Document==================== */
	public PepperResult search(String queryString, int firstResult, int maxResult) throws PepperException {
		return search(queryString, null, null, firstResult, maxResult);
	}

	public PepperResult search(String queryString, PepperSortField[] sortFields, String[] hightLightFields,
			int firstResult, int maxResult) throws PepperException {
		Query query = parseQueryString(queryString,
				this.configuration.getAnalyzerFactory().getAnalyzer(this.configuration.getParserAnalyzerMode()));
		return search(query, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult search(String queryString, int numHits, int firstResult, int maxResult) throws PepperException {
		return search(queryString, numHits, null, null, firstResult, maxResult);
	}

	public PepperResult search(String queryString, int numHits, PepperSortField[] sortFields, String[] hightLightFields,
			int firstResult, int maxResult) throws PepperException {
		Query query = parseQueryString(queryString,
				this.configuration.getAnalyzerFactory().getAnalyzer(this.configuration.getParserAnalyzerMode()));
		return search(query, numHits, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult search(Query query, PepperSortField[] sortFields, String[] hightLightFields, int firstResult,
			int maxResult) throws PepperException {
		return search(query, 0, sortFields, hightLightFields, firstResult, maxResult);
	}

	public PepperResult search(final Query query, final int numHits, final PepperSortField[] sortFields,
			final String[] hightLightFields, final int firstResult, final int maxResult) throws PepperException {
		return searchOperate(new Operator<IndexSearcher, PepperResult>() {
			public PepperResult operate(IndexSearcher is) throws Exception {
				int myNumHits = numHits >= 0 ? numHits : is.maxDoc();
				if (myNumHits == 0) {
					myNumHits = Peppering.this.configuration.getDefaultNumHits();
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
								new SimpleHTMLFormatter(Peppering.this.configuration.getHightLightPreTag(),
										Peppering.this.configuration.getHightLightPostTag()),
								new QueryScorer(query));
						highlighter.setTextFragmenter(
								new SimpleFragmenter(Peppering.this.configuration.getFragmentSize()));
						String tmpValue = null;
						for (int i = 0; i < numDocs; i++) {
							doc = is.doc(scoreDocs[i].doc);
							for (String hightLightField : hightLightFields) {
								Field lcField = (Field) doc.getFieldable(hightLightField);
								if (lcField != null) {
									tmpValue = doc.get(hightLightField);
									if ((tmpValue != null) && (!tmpValue.isEmpty())) {
										tmpValue = highlighter.getBestFragment(
												Peppering.this.configuration.getAnalyzerFactory()
														.getAnalyzer(Peppering.this.configuration
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
		});
	}

	/* =================Count Document==================== */
	public int count(String queryString, int numHits) throws PepperException {
		return count(parseQueryString(queryString,
				this.configuration.getAnalyzerFactory().getAnalyzer(this.configuration.getParserAnalyzerMode())),
				numHits);
	}

	public int count(final Query query, final int numHits) throws PepperException {
		return searchOperate(new Operator<IndexSearcher, Integer>() {
			public Integer operate(IndexSearcher is) throws Exception {
				int myNumHits = numHits >= 0 ? numHits : is.maxDoc();
				if (myNumHits == 0) {
					myNumHits = Peppering.this.configuration.getDefaultNumHits();
				}
				TopDocsCollector<?> collector = Peppering.lowLevelSearch(is, query, myNumHits, null);

				int c = collector.getTotalHits();
				return Integer.valueOf(c > myNumHits ? myNumHits : c);
			}
		}).intValue();
	}

	/* ================================================ */
	/* =============The Index utilities ===================== */
	/* =============================================== */
	public static Query parseQueryString(String qs, Analyzer analyzer) throws PepperException {
		MdQueryParser parser = new MdQueryParser(Version.LUCENE_35, new String[] { "Title" }, analyzer);
		try {
			Query q = parser.parse(qs);
			return q;
		} catch (ParseException e) {
			throw new PepperException(e.getMessage(), e);
		}
	}

	public static TopDocsCollector<?> lowLevelSearch(IndexSearcher is, Query query, int numHits,
			PepperSortField[] sortFields) throws Exception {
		Sort sort = null;
		if ((sortFields != null) && (sortFields.length > 0)) {
			SortField[] lcSortFields = new SortField[sortFields.length];
			for (int i = 0; i < sortFields.length; i++) {
				lcSortFields[i] = sortFields[i].getSortField();
			}
			sort = new Sort(lcSortFields);
		}
		TopDocsCollector<?> collector = null;
		if (sort == null) {
			collector = TopScoreDocCollector.create(numHits, true);
		} else {
			collector = TopFieldCollector.create(sort, numHits, false, true, true, true);
		}
		is.search(query, collector);
		return collector;
	}

	public int docFreq(final String field, final String word) throws PepperException {
		return readOperate(new Operator<IndexReader, Integer>() {
			public Integer operate(IndexReader ir) throws Exception {
				return Integer.valueOf(ir.docFreq(new Term(field, word)));
			}
		}).intValue();
	}

	public long wordFreq(final String field, final String word) throws PepperException {
		return readOperate(new Operator<IndexReader, Long>() {
			public Long operate(IndexReader ir) throws Exception {
				long sum = 0L;
				TermDocs termDocs = ir.termDocs(new Term(field, word));
				while (termDocs.next()) {
					sum += termDocs.freq();
				}
				return Long.valueOf(sum);
			}
		}).longValue();
	}

	public int numDocs() throws PepperException {
		return readOperate(new Operator<IndexReader, Integer>() {
			public Integer operate(IndexReader ir) throws Exception {
				return Integer.valueOf(ir.numDocs());
			}
		}).intValue();
	}

	public String[] getDocTerms(final int docNumber, final String field) throws PepperException {
		return readOperate(new Operator<IndexReader, String[]>() {
			public String[] operate(IndexReader ir) throws Exception {
				return ir.getTermFreqVector(docNumber, field).getTerms();
			}
		});
	}

	/* ================================================ */
	/* =============The Index Getter And Setter ============== */
	/* =============================================== */
	public void setIndexConfig(Configuration indexConfig) {
		this.configuration = indexConfig;
	}

	public Configuration getIndexConfig() {
		return this.configuration;
	}
}
