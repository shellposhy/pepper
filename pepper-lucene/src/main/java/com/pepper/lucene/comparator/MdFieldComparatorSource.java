package com.pepper.lucene.comparator;

import java.io.IOException;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

/**
 * Provides a {@link FieldComparator} for custom field sorting.
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class MdFieldComparatorSource extends FieldComparatorSource {
	private static final long serialVersionUID = 1L;
	private Integer fieldType;

	public MdFieldComparatorSource(Integer fieldType) {
		this.fieldType = fieldType;
	}

	public FieldComparator<String> newComparator(String fieldName, int numHits, int sortPos, boolean reversed)
			throws IOException {
		if (this.fieldType.intValue() == 11) {
			return new MdStringFieldComparator(fieldName, numHits, sortPos, reversed);
		}
		return new MdStringFieldComparator(fieldName, numHits, sortPos, reversed);
	}
}
