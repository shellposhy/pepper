package com.pepper.spring.model;

import java.io.Serializable;

import org.apache.lucene.document.Field;

import com.pepper.spring.annotation.PepperField;
import com.pepper.spring.enums.EDataType;

public class ExtendInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	@PepperField(key = "SubTitle", store = Field.Store.YES, index = Field.Index.ANALYZED, termVector = Field.TermVector.NO, type = EDataType.STRING)
	private String subTitle = "即成立一百周年";
	@PepperField(key = "IntroTitle", store = Field.Store.YES, index = Field.Index.ANALYZED, termVector = Field.TermVector.NO, type = EDataType.STRING)
	private String introTitle = "汪洋副主席主持";

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getIntroTitle() {
		return introTitle;
	}

	public void setIntroTitle(String introTitle) {
		this.introTitle = introTitle;
	}

}
