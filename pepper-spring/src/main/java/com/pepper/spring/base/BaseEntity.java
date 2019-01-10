package com.pepper.spring.base;

import java.io.Serializable;

import org.apache.lucene.document.Field;

import com.pepper.spring.annotation.PepperField;
import com.pepper.spring.enums.EDataType;

/**
 * Lucene base Model
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 6549885313472962978L;
	@PepperField(key = "UUID", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, termVector = Field.TermVector.NO, type = EDataType.STRING)
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
