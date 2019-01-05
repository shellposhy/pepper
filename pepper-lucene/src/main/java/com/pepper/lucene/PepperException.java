package com.pepper.lucene;

/**
 * Lucene Indexing Exception
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class PepperException extends Exception {
	private static final long serialVersionUID = -4977944640525893593L;

	public PepperException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public PepperException(String message) {
		super(message);
	}

}
