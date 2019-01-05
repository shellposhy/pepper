package com.pepper.lucene.common;

import java.io.Serializable;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * Lucene Search Result Object
 * 
 * @see Serializable
 * @author shellpo shih
 * @version 1.0
 */
public class PepperResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public int totalHits = 0;
	public ScoreDoc[] scoreDocs;
	public Document[] documents;

	public PepperResult() {
	}

	public PepperResult(TopDocs topDocs) {
		this(topDocs.totalHits, topDocs.scoreDocs);
	}

	public PepperResult(int totalHits, ScoreDoc[] scoreDocs) {
		this.totalHits = totalHits;
		this.scoreDocs = scoreDocs;
	}
}
