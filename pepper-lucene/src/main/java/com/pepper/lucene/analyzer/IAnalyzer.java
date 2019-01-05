package com.pepper.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;

/**
 * Search engine tokenizer interface
 * 
 * @author shellpo shih
 * @version 1.0
 */
public abstract interface IAnalyzer {
	public static final String MOST_WORDS_MODE = "most-words";
	public static final String MAX_WORD_LENGTH_MODE = "max-word-length";

	public abstract Analyzer getAnalyzer(String paramString);
}
