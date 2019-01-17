package com.pepper.spring.test;

import org.apache.lucene.document.Document;
import org.junit.Before;
import org.junit.Test;

import com.pepper.spring.model.Article;
import com.pepper.spring.util.Objects;

import cn.com.lemon.base.Strings;
import cn.com.lemon.base.util.Jsons;

public class ObjectsCase {
	

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Article article = new Article();
		article.setUuid(Strings.uuid());
		Document doc = Objects.doc(article);
		System.out.println(Jsons.json(doc));
	}

}
