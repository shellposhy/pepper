package com.pepper.lucene.comparator;

import java.io.IOException;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldComparator;

/**
 * Provides a {@link FieldComparator} for custom field sorting.
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class MdStringFieldComparator extends FieldComparator<String> {
	private IndexReader reader;
	private String fieldName;
	private MapFieldSelector mapFieldSelector;
	private String[] strs;

	public MdStringFieldComparator(String fieldName, int numHits, int sortPos, boolean reversed) {
		this.fieldName = fieldName;
		this.strs = new String[numHits];
		this.mapFieldSelector = new MapFieldSelector(new String[] { fieldName });
	}

	public int compare(int slot1, int slot2) {
		if (this.strs[slot1] == null) {
			return -1;
		}
		if (this.strs[slot2] == null) {
			return 1;
		}
		return this.strs[slot1].compareTo(this.strs[slot2]);
	}

	public int compareBottom(int arg0) throws IOException {
		return 0;
	}

	public void copy(int slot, int doc) throws IOException {
		this.strs[slot] = this.reader.document(doc, this.mapFieldSelector).get(this.fieldName);
	}

	public void setBottom(int slot) {
	}

	public void setNextReader(IndexReader reader, int docBase) throws IOException {
		this.reader = reader;
	}

	public String value(int slot) {
		return this.strs[slot];
	}
}
