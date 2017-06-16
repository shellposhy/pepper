package cn.com.pepper.service;

import java.util.Collection;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import cn.com.pepper.Configuration;
import cn.com.pepper.PepperException;
import cn.com.pepper.Peppering;
import cn.com.pepper.common.PepperResult;
import cn.com.pepper.comparator.base.PepperSortField;

/**
 * The Pepper service API
 * <p>
 * contains the all methods
 * 
 * @author shellpo shih
 * @version 1.0
 */
public abstract interface IndexDao {
	/* ================= Initialize ======================== */
	public abstract Peppering getIndex(String indexPath) throws PepperException;

	public abstract void closeIndex(String indexPath) throws PepperException;

	public abstract void closeAllOpenedIndexes() throws PepperException;

	/* =================Add Document==================== */
	public abstract void addDocument(String indexPath, Document doc) throws PepperException;

	public abstract void addDocuments(String indexPath, Collection<Document> docs) throws PepperException;

	/* =================Delete Document==================== */
	public abstract void deleteDocuments(String indexPath, String queryString) throws PepperException;

	public abstract void deleteDocuments(String indexPath, Query query) throws PepperException;

	public abstract void deleteAll(String indexPath) throws PepperException;

	/* =================Update Document==================== */
	public abstract void updateDocument(String indexPath, String field, String value, Document doc)
			throws PepperException;

	public abstract void updateDocuments(String indexPath, String field, String value, Collection<Document> docs)
			throws PepperException;

	/* =================Search Document==================== */
	public abstract PepperResult search(String indexPath, String queryString, int firstResult, int maxResult)
			throws PepperException;

	public abstract PepperResult search(String indexPath, String queryString, int numHits, int firstResult,
			int maxResult) throws PepperException;

	public abstract PepperResult search(String indexPath, String queryString, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult search(String indexPath, String queryString, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult search(String indexPath, Query query, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult search(String indexPath, Query query, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult mSearch(String[] indexPaths, String queryString, int firstResult, int maxResult)
			throws PepperException;

	public abstract PepperResult mSearch(String[] indexPaths, String queryString, int numHits, int firstResult,
			int maxResult) throws PepperException;

	public abstract PepperResult mSearch(String[] indexPaths, String queryString, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult mSearch(String[] indexPaths, String queryString, int numHits,
			PepperSortField[] sortFields, String[] hightLightFields, int firstResult, int maxResult)
			throws PepperException;

	public abstract PepperResult mSearch(String[] indexPaths, Query query, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult mSearch(String[] indexPaths, Query query, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult searchInAllOpenedIndexes(String queryString, int firstResult, int maxResult)
			throws PepperException;

	public abstract PepperResult searchInAllOpenedIndexes(String queryString, int numHits, int firstResult,
			int maxResult) throws PepperException;

	public abstract PepperResult searchInAllOpenedIndexes(String queryString, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult searchInAllOpenedIndexes(String queryString, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult searchInAllOpenedIndexes(Query query, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	public abstract PepperResult searchInAllOpenedIndexes(Query query, int numHits, PepperSortField[] sortFields,
			String[] hightLightFields, int firstResult, int maxResult) throws PepperException;

	/* =================Count Document==================== */
	public abstract int numDocs(String indexPath) throws PepperException;

	public abstract void setIndexConfig(Configuration indexConfig);

	public abstract Configuration getIndexConfig();

	public abstract int count(String indexPath, String queryString, int numHits) throws PepperException;

	public abstract int count(String indexPath, Query query, int numHits) throws PepperException;

	public abstract int mCount(String[] indexPaths, String queryString, int numHits) throws PepperException;

	public abstract int mCount(String[] indexPaths, Query query, int numHits) throws PepperException;

	public abstract int countInAllOpenedIndexes(String queryString, int numHits) throws PepperException;

	public abstract int countInAllOpenedIndexes(Query query, int numHits) throws PepperException;
}
