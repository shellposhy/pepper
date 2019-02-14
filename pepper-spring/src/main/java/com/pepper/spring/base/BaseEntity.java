package com.pepper.spring.base;

import java.io.Serializable;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pepper.spring.annotation.PepperField;
import com.pepper.spring.enums.EDataType;
import com.pepper.spring.enums.EIndexOperate;

/**
 * Lucene base Model
 * <p>
 * Each {@link Document} and the follow fields:<blockquote>
 * 
 * <pre>
 * UUID,DocTime,Year,Month,Day
 * </pre>
 * 
 * </blockquote>
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Every Java bean must contained the base field */
	@PepperField(key = "UUID", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.STRING)
	private String uuid;
	@PepperField(key = "DocTime", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.DATETIME)
	private Date docTime;
	@PepperField(key = "Year", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.INTEGER)
	private int year;
	@PepperField(key = "Month", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.INTEGER)
	private int month;
	@PepperField(key = "Day", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.INTEGER)
	private int day;
	/** Index file operation mode,default added new index files. */
	private EIndexOperate operate = EIndexOperate.INSERT;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getDocTime() {
		return docTime;
	}

	public void setDocTime(Date docTime) {
		this.docTime = docTime;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public EIndexOperate getOperate() {
		return operate;
	}

	public void setOperate(EIndexOperate operate) {
		this.operate = operate;
	}

}
