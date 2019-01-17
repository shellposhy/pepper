package com.pepper.spring.base;

import java.io.Serializable;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pepper.spring.annotation.PepperField;
import com.pepper.spring.enums.EDataType;

import static cn.com.lemon.base.Strings.uuid;
import static cn.com.lemon.base.DateUtil.year;
import static cn.com.lemon.base.DateUtil.month;
import static cn.com.lemon.base.DateUtil.day;;

/**
 * Lucene base Model
 * <p>
 * Each {@link Document} and the follow fields:<blockquote>
 * 
 * <pre>
 * UUID
 * </pre>
 * 
 * <pre>
 * DocTime
 * </pre>
 * 
 * <pre>
 * Year
 * </pre>
 * 
 * <pre>
 * Month
 * </pre>
 * 
 * <pre>
 * Day
 * </pre>
 * 
 * </blockquote>
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 6549885313472962978L;
	@PepperField(key = "UUID", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.STRING)
	private String uuid = uuid();
	@PepperField(key = "DocTime", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.DATETIME)
	private Date docTime = new Date();
	@PepperField(key = "Year", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.INTEGER)
	private int year = year(getDocTime());
	@PepperField(key = "Month", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.INTEGER)
	private int month = month(getDocTime());
	@PepperField(key = "Day", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, type = EDataType.INTEGER)
	private int day = day(getDocTime());

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

}
