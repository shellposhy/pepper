package com.pepper.lucene.comparator.base;

import java.io.Serializable;
import org.apache.lucene.search.SortField;

import com.pepper.lucene.comparator.MdFieldComparatorSource;

/**
 * The <code>PepperSortField</code>class
 * <p>
 * sort documents by terms in an individual field. Fields must be indexed in
 * order to sort by them.
 * 
 * @see Serializable
 * @author shellpo shih
 * @version 1.0
 */
public class PepperSortField implements Serializable {
	private static final long serialVersionUID = 1L;
	private SortField sortField;
	private int fieldType = 0;

	public enum FieldType {
		Byte, Custom, Doc, Double, Float, Int, Long, Score, Short, String, StringVal;
	}

	public PepperSortField(String fieldName, FieldType type, boolean reverse) {
		if (type == FieldType.StringVal) {
			this.sortField = new SortField(fieldName, new MdFieldComparatorSource(Integer.valueOf(this.fieldType)),
					reverse);
		} else {
			this.sortField = new SortField(fieldName, this.fieldType, reverse);
		}
	}

	public PepperSortField(String str) {
		String[] ss = str.split("\\|");
		this.fieldType = Integer.valueOf(ss[1]).intValue();
		boolean reverse = "1".equals(ss[2]);
		if (this.fieldType == FieldType.StringVal.ordinal()) {
			this.sortField = new SortField(ss[0], new MdFieldComparatorSource(Integer.valueOf(this.fieldType)),
					reverse);
		} else {
			this.sortField = new SortField(ss[0], this.fieldType, reverse);
		}
	}

	public static PepperSortField[] getSortFields(String str) {
		String[] ss = str.split(",");
		PepperSortField[] mdSortFields = new PepperSortField[ss.length];
		for (int i = 0; i < ss.length; i++) {
			mdSortFields[i] = new PepperSortField(ss[i]);
		}
		return mdSortFields;
	}

	public SortField getSortField() {
		return this.sortField;
	}

	public String toString() {
		return this.sortField.getField() + "|" + this.fieldType + "|" + (this.sortField.getReverse() ? "1" : "0");
	}
}
