package cn.com.pepper;

import java.io.IOException;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * Lucene Indexing Exception
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class PepperException extends Exception {
	public static final int CAUSE_TYPE_Exception = 10;
	public static final int CAUSE_TYPE_LockObtainFailedException = 11;
	public static final int CAUSE_TYPE_IOException = 12;
	public static final int CAUSE_TYPE_CorruptIndexException = 13;
	private static final long serialVersionUID = 1L;

	public PepperException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public PepperException(String message) {
		super(message);
	}

	public int getCauseType() {
		Throwable throwable = getCause();
		if (throwable != null) {
			if ((throwable instanceof LockObtainFailedException)) {
				return 11;
			}
			if ((throwable instanceof IOException)) {
				return 12;
			}
			if ((throwable instanceof CorruptIndexException)) {
				return 13;
			}
		}
		return 10;
	}
}
