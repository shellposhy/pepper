package com.pepper.spring.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.lucene.document.Field;

import com.pepper.spring.annotation.Pepper;
import com.pepper.spring.annotation.PepperField;
import com.pepper.spring.base.BaseEntity;
import com.pepper.spring.enums.EDataType;

@Pepper(value = Article.class, path = "/content")
public class Article extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@PepperField(key = "ID", store = Field.Store.YES, index = Field.Index.ANALYZED, termVector = Field.TermVector.NO, type = EDataType.INTEGER)
	private Integer id = 122;
	@PepperField(key = "Title", store = Field.Store.YES, index = Field.Index.ANALYZED, termVector = Field.TermVector.YES, type = EDataType.STRING)
	private String title = "关于我们";
	@PepperField(key = "Content", store = Field.Store.NO, index = Field.Index.ANALYZED, termVector = Field.TermVector.WITH_POSITIONS_OFFSETS, type = EDataType.STRING)
	private String content = "习总书记指出扶贫开发是全党全社会的共同责任，要动员和凝聚全社会力量广泛参与。万夫一力，天下无敌。";
	@PepperField(key = "Author", store = Field.Store.YES, index = Field.Index.ANALYZED, termVector = Field.TermVector.YES, type = EDataType.STRING)
	private String author = "中国社会扶贫网";
	@PepperField(key = "Publish", store = Field.Store.YES, index = Field.Index.NOT_ANALYZED, termVector = Field.TermVector.NO, type = EDataType.DATETIME)
	private Date publish = new Date();
	@PepperField(key = "ExtendInfo", type = EDataType.OBJECT)
	private ExtendInfo extendInfo = new ExtendInfo();
	@PepperField(key = "DataType", store = Field.Store.YES, index = Field.Index.NO, termVector = Field.TermVector.NO, type = EDataType.ENUM)
	private EDataType dataType = EDataType.INTEGER;
	@PepperField(key = "BigDecimal", store = Field.Store.YES, index = Field.Index.NO, type = EDataType.BIGDECIMAL)
	private BigDecimal bigDecimal = new BigDecimal("4521");
	@PepperField(key = "BigInteger", store = Field.Store.YES, index = Field.Index.NO, type = EDataType.BIGINTEGER)
	private BigInteger bigInteger = new BigInteger("8888");

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getPublish() {
		return publish;
	}

	public void setPublish(Date publish) {
		this.publish = publish;
	}

	public ExtendInfo getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(ExtendInfo extendInfo) {
		this.extendInfo = extendInfo;
	}

	public EDataType getDataType() {
		return dataType;
	}

	public void setDataType(EDataType dataType) {
		this.dataType = dataType;
	}

	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	public BigInteger getBigInteger() {
		return bigInteger;
	}

	public void setBigInteger(BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}

}
