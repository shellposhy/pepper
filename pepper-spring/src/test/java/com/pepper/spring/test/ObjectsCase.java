package com.pepper.spring.test;

import org.apache.lucene.document.Document;
import org.junit.Before;
import org.junit.Test;

import com.pepper.spring.model.Article;
import com.pepper.spring.util.Objects;

import cn.com.lemon.base.util.Jsons;

import static cn.com.lemon.base.Strings.uuid;

import java.util.Date;

import static cn.com.lemon.base.DateUtil.year;
import static cn.com.lemon.base.DateUtil.month;
import static cn.com.lemon.base.DateUtil.day;;

public class ObjectsCase {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Article article = new Article();
		article.setUuid(uuid());
		article.setDocTime(new Date());
		article.setYear(year(article.getDocTime()));
		article.setMonth(month(article.getDocTime()));
		article.setDay(day(article.getDocTime()));
		Document doc = Objects.doc(article);
		System.out.println(Jsons.json(doc));
	}

}
